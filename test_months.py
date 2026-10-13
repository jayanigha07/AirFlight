from analytics.airflight_engine import AirFlightAnalytics

engine = AirFlightAnalytics()

print("--- Testing Best Time to Fly by Month for ORD -> LGA ---")
for m in [None, 1, 2, 3, 6, 9, 12]:
    res = engine.best_time_to_fly("ORD", "LGA", month=m)
    st = res.get("status")
    cnt = res.get("total_route_flights") or res.get("total_flights_found")
    best = res.get("historically_best_period")
    rec = res.get("recommendation", "")
    print(f"Month {m}: status={st}, flights={cnt}, best_period={best}")
    if res.get("all_periods_comparison"):
        for p in res["all_periods_comparison"]:
            print(f"   {p['time_of_day']}: count={p['flight_count']}, delay_rate={p['delay_rate_pct']}%, avg_delay={p['avg_arrival_delay_min']}m")

print("\n--- Testing Historical Risk Advisor by Month for UA, ORD -> EWR, EVENING ---")
for m in [1, 2, 6, 9, 12]:
    res = engine.historical_delay_risk("UA", "ORD", "EWR", month=m, departure_time=1830)
    st = res.get("status")
    cnt = res.get("sample_size") or res.get("flights_found")
    risk = res.get("historical_delay_risk")
    print(f"Month {m}: status={st}, sample_size={cnt}, risk={risk}")
