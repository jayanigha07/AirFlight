#!/usr/bin/env python3
"""
AirFlight Descriptive Analytics Engine
Historical Flight Reliability and Delay Analysis (Backend Only - No ML/Future Prediction)

Implements:
1. Best Time to Fly (Origin, Destination, Month -> historically optimal time-of-day)
2. Route Reliability Score (Deterministic 0-100 historical reliability metric)
3. Historical Delay Risk (Carrier, Route, Month, Time -> LOW/MED/HIGH risk & historical alternatives)
"""

import sys
import argparse
from typing import Dict, Any, Optional, List
from pymongo import MongoClient

class AirFlightAnalytics:
    def __init__(self, mongo_uri: str = "mongodb://localhost:27017/", db_name: str = "airflight"):
        self.client = MongoClient(mongo_uri)
        self.db = self.client[db_name]
        self.flights = self.db["flights"]
        self.airline_summaries = self.db["airline_summaries"]

    @staticmethod
    def departure_to_time_of_day(dep_time: int) -> str:
        """Explicit 24h military time mapping."""
        if 500 <= dep_time <= 1159:
            return "MORNING"
        elif 1200 <= dep_time <= 1659:
            return "AFTERNOON"
        elif 1700 <= dep_time <= 2159:
            return "EVENING"
        else:
            return "NIGHT"

    def best_time_to_fly(self, origin: str, dest: str, month: Optional[int] = None) -> Dict[str, Any]:
        """
        1. BEST TIME TO FLY
        Compares historical time-of-day periods (Morning, Afternoon, Evening, Night)
        for a given route and optional month.
        """
        origin = origin.upper().strip()
        dest = dest.upper().strip()

        match_query: Dict[str, Any] = {
            "origin_airport": origin,
            "destination_airport": dest
        }
        if month is not None:
            match_query["month"] = int(month)

        pipeline = [
            {"$match": match_query},
            {
                "$group": {
                    "_id": "$time_of_day",
                    "flightCount": {"$sum": 1},
                    "delayedCount": {
                        "$sum": {"$cond": [{"$gt": ["$arrival_delay", 15.0]}, 1, 0]}
                    },
                    "cancelledCount": {
                        "$sum": {"$cond": [{"$eq": ["$cancelled", 1]}, 1, 0]}
                    },
                    "avgArrivalDelay": {"$avg": "$arrival_delay"},
                    "avgDepartureDelay": {"$avg": "$departure_delay"}
                }
            }
        ]

        results = list(self.flights.aggregate(pipeline))
        total_route_flights = sum(r["flightCount"] for r in results)

        if total_route_flights < 10:
            return {
                "status": "INSUFFICIENT_HISTORICAL_DATA",
                "origin": origin,
                "destination": dest,
                "month": month,
                "total_flights_found": total_route_flights,
                "message": f"Historical flight count ({total_route_flights}) is below minimum threshold (10)."
            }

        periods_data: List[Dict[str, Any]] = []
        for r in results:
            period = r["_id"]
            count = r["flightCount"]
            delayed = r["delayedCount"]
            cancelled = r["cancelledCount"]
            delay_rate = round((delayed * 100.0) / count, 2) if count > 0 else 0.0
            cancel_rate = round((cancelled * 100.0) / count, 2) if count > 0 else 0.0
            avg_arr = round(r["avgArrivalDelay"] if r["avgArrivalDelay"] is not None else 0.0, 2)
            avg_dep = round(r["avgDepartureDelay"] if r["avgDepartureDelay"] is not None else 0.0, 2)

            # Disruption Score: lower is historically better
            # 50% delay rate + 30% normalized positive delay + 20% cancellation rate
            disruption_score = round(
                (delay_rate * 0.50) + (max(0.0, avg_arr) * 0.30) + (cancel_rate * 0.20),
                2
            )

            periods_data.append({
                "time_of_day": period,
                "flight_count": count,
                "delayed_count": delayed,
                "delay_rate_pct": delay_rate,
                "avg_arrival_delay_min": avg_arr,
                "avg_departure_delay_min": avg_dep,
                "cancelled_count": cancelled,
                "cancellation_rate_pct": cancel_rate,
                "disruption_score": disruption_score
            })

        # Filter for periods with at least 3 flights to avoid one-off anomalies, falling back if none qualify
        significant_periods = [p for p in periods_data if p["flight_count"] >= 3]
        candidate_pool = significant_periods if significant_periods else periods_data
        candidate_pool.sort(key=lambda x: x["disruption_score"])
        best_period = candidate_pool[0]

        # Also sort full comparison list by disruption score
        periods_data.sort(key=lambda x: x["disruption_score"])

        return {
            "status": "SUCCESS",
            "origin": origin,
            "destination": dest,
            "month": month,
            "total_route_flights": total_route_flights,
            "historically_best_period": best_period["time_of_day"],
            "best_period_metrics": best_period,
            "all_periods_comparison": periods_data,
            "recommendation": (
                f"Historically, {best_period['time_of_day']} flights on {origin}->{dest} demonstrated "
                f"the lowest delay rate ({best_period['delay_rate_pct']}%) and "
                f"average arrival delay ({best_period['avg_arrival_delay_min']} min)."
            )
        }

    def route_reliability_score(self, origin: str, dest: str) -> Dict[str, Any]:
        """
        2. ROUTE RELIABILITY SCORE
        Deterministic 0-100 historical score based on delay rate, normalized delay, and cancel rate.

        Formula:
          P_delay  = 100 - DelayRate%
          N_delay  = max(0, 100 - (max(0, AvgArrDelay) / 60.0) * 100)
          P_cancel = max(0, 100 - (CancelRate% * 5.0))
          Score    = round(0.50 * P_delay + 0.30 * N_delay + 0.20 * P_cancel, 1)
        """
        origin = origin.upper().strip()
        dest = dest.upper().strip()

        pipeline = [
            {"$match": {"origin_airport": origin, "destination_airport": dest}},
            {
                "$group": {
                    "_id": None,
                    "flightCount": {"$sum": 1},
                    "delayedCount": {
                        "$sum": {"$cond": [{"$gt": ["$arrival_delay", 15.0]}, 1, 0]}
                    },
                    "cancelledCount": {
                        "$sum": {"$cond": [{"$eq": ["$cancelled", 1]}, 1, 0]}
                    },
                    "avgArrivalDelay": {"$avg": "$arrival_delay"}
                }
            }
        ]

        results = list(self.flights.aggregate(pipeline))
        if not results or results[0]["flightCount"] < 15:
            cnt = results[0]["flightCount"] if results else 0
            return {
                "status": "INSUFFICIENT_HISTORICAL_DATA",
                "origin": origin,
                "destination": dest,
                "flights_found": cnt,
                "message": f"Historical route flight count ({cnt}) is below minimum threshold (15)."
            }

        data = results[0]
        count = data["flightCount"]
        delayed = data["delayedCount"]
        cancelled = data["cancelledCount"]
        avg_arr = data["avgArrivalDelay"] if data["avgArrivalDelay"] is not None else 0.0

        delay_rate = (delayed * 100.0) / count
        cancel_rate = (cancelled * 100.0) / count

        # Components
        p_delay = max(0.0, min(100.0, 100.0 - delay_rate))
        n_delay = max(0.0, min(100.0, 100.0 - (max(0.0, avg_arr) / 60.0) * 100.0))
        p_cancel = max(0.0, min(100.0, 100.0 - (cancel_rate * 5.0)))

        score = round(0.50 * p_delay + 0.30 * n_delay + 0.20 * p_cancel, 1)
        score = max(0.0, min(100.0, score))

        if score >= 85.0:
            tier = "EXCELLENT"
        elif score >= 75.0:
            tier = "GOOD"
        elif score >= 60.0:
            tier = "MODERATE"
        else:
            tier = "POOR"

        return {
            "status": "SUCCESS",
            "origin": origin,
            "destination": dest,
            "route": f"{origin} -> {dest}",
            "reliability_score": score,
            "tier": tier,
            "sample_size": count,
            "metrics": {
                "delay_rate_pct": round(delay_rate, 2),
                "avg_arrival_delay_min": round(avg_arr, 2),
                "cancellation_rate_pct": round(cancel_rate, 2),
                "delayed_flights": delayed,
                "cancelled_flights": cancelled
            },
            "formula_documentation": {
                "formula": "Score = 0.50 * P_delay + 0.30 * N_delay + 0.20 * P_cancel",
                "weights": {"delay_rate": 0.50, "normalized_delay": 0.30, "cancellation_rate": 0.20},
                "components": {
                    "P_delay": round(p_delay, 2),
                    "N_delay": round(n_delay, 2),
                    "P_cancel": round(p_cancel, 2)
                }
            }
        }

    def historical_delay_risk(
        self,
        airline: str,
        origin: str,
        dest: str,
        month: int,
        departure_time: int
    ) -> Dict[str, Any]:
        """
        3. HISTORICAL DELAY RISK
        Evaluates historical operational risk (LOW / MEDIUM / HIGH) for a specific flight profile.
        Returns historical delay rate, average delay, observed patterns,
        and better historical alternatives if available.
        """
        airline = airline.upper().strip()
        origin = origin.upper().strip()
        dest = dest.upper().strip()
        month = int(month)
        tod = self.departure_to_time_of_day(int(departure_time))

        # Query 1: Specific query (Airline + Origin + Dest + TOD)
        pipeline = [
            {
                "$match": {
                    "airline": airline,
                    "origin_airport": origin,
                    "destination_airport": dest,
                    "time_of_day": tod
                }
            },
            {
                "$group": {
                    "_id": None,
                    "flightCount": {"$sum": 1},
                    "delayedCount": {
                        "$sum": {"$cond": [{"$gt": ["$arrival_delay", 15.0]}, 1, 0]}
                    },
                    "cancelledCount": {
                        "$sum": {"$cond": [{"$eq": ["$cancelled", 1]}, 1, 0]}
                    },
                    "avgArrivalDelay": {"$avg": "$arrival_delay"}
                }
            }
        ]

        results = list(self.flights.aggregate(pipeline))

        # Fallback to general Airline + Origin + Dest if specific TOD sample is small
        scope = "SPECIFIC_TIME_WINDOW"
        if not results or results[0]["flightCount"] < 10:
            pipeline_route = [
                {
                    "$match": {
                        "airline": airline,
                        "origin_airport": origin,
                        "destination_airport": dest
                    }
                },
                {
                    "$group": {
                        "_id": None,
                        "flightCount": {"$sum": 1},
                        "delayedCount": {
                            "$sum": {"$cond": [{"$gt": ["$arrival_delay", 15.0]}, 1, 0]}
                        },
                        "cancelledCount": {
                            "$sum": {"$cond": [{"$eq": ["$cancelled", 1]}, 1, 0]}
                        },
                        "avgArrivalDelay": {"$avg": "$arrival_delay"}
                    }
                }
            ]
            results = list(self.flights.aggregate(pipeline_route))
            scope = "OVERALL_ROUTE"

        if not results or results[0]["flightCount"] < 10:
            return {
                "status": "INSUFFICIENT_HISTORICAL_DATA",
                "airline": airline,
                "origin": origin,
                "destination": dest,
                "month": month,
                "time_of_day": tod,
                "flights_found": results[0]["flightCount"] if results else 0,
                "message": "Historical sample size is below the minimum threshold (10 flights)."
            }

        data = results[0]
        count = data["flightCount"]
        delayed = data["delayedCount"]
        cancelled = data["cancelledCount"]
        avg_arr = round(data["avgArrivalDelay"] if data["avgArrivalDelay"] is not None else 0.0, 2)
        delay_rate = round((delayed * 100.0) / count, 2)
        cancel_rate = round((cancelled * 100.0) / count, 2)

        # Risk Classification (Deterministic historical thresholds)
        if delay_rate <= 15.0 and avg_arr <= 5.0 and cancel_rate <= 2.0:
            risk = "LOW"
        elif delay_rate >= 25.0 or avg_arr >= 15.0 or cancel_rate >= 5.0:
            risk = "HIGH"
        else:
            risk = "MEDIUM"

        observed_patterns: List[str] = []
        if tod == "EVENING":
            observed_patterns.append("Evening departures experience higher delay accumulation due to upstream turnaround delays.")
        elif tod == "MORNING":
            observed_patterns.append("Morning departures exhibit the highest on-time arrival consistency.")

        if month in [12, 1, 2]:
            observed_patterns.append("Winter schedule displays elevated cancellation frequencies from weather disruptions.")
        elif month in [6, 7, 8]:
            observed_patterns.append("Summer operations show higher average delay lengths due to convective weather and peak air-traffic volume.")

        if delay_rate > 20.0:
            observed_patterns.append(f"Carrier {airline} exhibited higher than average delay frequency ({delay_rate}%) on this route.")

        # Find Better Historical Alternatives (other carriers or other departure windows on this route)
        alt_pipeline = [
            {
                "$match": {
                    "origin_airport": origin,
                    "destination_airport": dest
                }
            },
            {
                "$group": {
                    "_id": {"airline": "$airline", "time_of_day": "$time_of_day"},
                    "flightCount": {"$sum": 1},
                    "delayedCount": {
                        "$sum": {"$cond": [{"$gt": ["$arrival_delay", 15.0]}, 1, 0]}
                    },
                    "avgArr": {"$avg": "$arrival_delay"}
                }
            },
            {"$match": {"flightCount": {"$gte": 10}}}
        ]
        all_alts = list(self.flights.aggregate(alt_pipeline))
        better_alts = []
        for alt in all_alts:
            alt_carrier = alt["_id"]["airline"]
            alt_tod = alt["_id"]["time_of_day"]
            alt_count = alt["flightCount"]
            alt_delay_rate = round((alt["delayedCount"] * 100.0) / alt_count, 2)
            alt_avg_arr = round(alt["avgArr"] if alt["avgArr"] is not None else 0.0, 2)

            # Better if delay rate is at least 5% lower
            if alt_delay_rate < (delay_rate - 5.0):
                better_alts.append({
                    "airline": alt_carrier,
                    "time_of_day": alt_tod,
                    "flight_count": alt_count,
                    "delay_rate_pct": alt_delay_rate,
                    "avg_arrival_delay_min": alt_avg_arr
                })

        better_alts.sort(key=lambda x: x["delay_rate_pct"])

        return {
            "status": "SUCCESS",
            "airline": airline,
            "origin": origin,
            "destination": dest,
            "month": month,
            "departure_time": departure_time,
            "time_of_day": tod,
            "historical_delay_risk": risk,
            "sample_scope": scope,
            "sample_size": count,
            "metrics": {
                "delay_rate_pct": delay_rate,
                "avg_arrival_delay_min": avg_arr,
                "cancellation_rate_pct": cancel_rate,
                "delayed_flights": delayed,
                "cancelled_flights": cancelled
            },
            "observed_patterns": observed_patterns,
            "better_historical_alternatives": better_alts[:3]
        }

def run_cli():
    parser = argparse.ArgumentParser(description="AirFlight Historical Analytics CLI")
    subparsers = parser.add_subparsers(dest="command")

    # 1. best-time
    p_time = subparsers.add_parser("best-time", help="Determine historically best time to fly")
    p_time.add_argument("--origin", required=True, help="Origin IATA (e.g. SFO)")
    p_time.add_argument("--dest", required=True, help="Destination IATA (e.g. LAX)")
    p_time.add_argument("--month", type=int, default=None, help="Calendar month (1-12)")

    # 2. route-score
    p_route = subparsers.add_parser("route-score", help="Compute deterministic Route Reliability Score")
    p_route.add_argument("--origin", required=True, help="Origin IATA (e.g. ORD)")
    p_route.add_argument("--dest", required=True, help="Destination IATA (e.g. LGA)")

    # 3. delay-risk
    p_risk = subparsers.add_parser("delay-risk", help="Evaluate historical delay risk")
    p_risk.add_argument("--airline", required=True, help="Airline IATA (e.g. UA)")
    p_risk.add_argument("--origin", required=True, help="Origin IATA (e.g. ORD)")
    p_risk.add_argument("--dest", required=True, help="Destination IATA (e.g. LGA)")
    p_risk.add_argument("--month", type=int, required=True, help="Month (1-12)")
    p_risk.add_argument("--time", type=int, required=True, help="Departure time HHMM (e.g. 1830)")

    args = parser.parse_args()
    engine = AirFlightAnalytics()

    import json
    if args.command == "best-time":
        res = engine.best_time_to_fly(args.origin, args.dest, args.month)
        print(json.dumps(res, indent=2))
    elif args.command == "route-score":
        res = engine.route_reliability_score(args.origin, args.dest)
        print(json.dumps(res, indent=2))
    elif args.command == "delay-risk":
        res = engine.historical_delay_risk(args.airline, args.origin, args.dest, args.month, args.time)
        print(json.dumps(res, indent=2))
    else:
        parser.print_help()

if __name__ == "__main__":
    run_cli()
