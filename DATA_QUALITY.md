# AirFlight — Data Quality & Integrity Report

## 1. Executive Summary

This document formalizes the empirical data quality audit conducted on the 216,000 flight records from the **2015 US DOT / BTS Airline On-Time Performance dataset**.
The dataset was profiled prior to schema creation to understand the statistical distribution, domain validity, missing value semantics, and domain-specific anomalies.

---

## 2. Empirical Quality Metrics

- **Total Inspected Flight Records:** 216,000
- **Total Columns:** 31
- **Temporal Distribution:** Exactly 18,000 flights per calendar month across all 12 months (Jan–Dec 2015).
- **Duplicate Records:** 0 exact duplicates across `(YEAR, MONTH, DAY, AIRLINE, FLIGHT_NUMBER, SCHEDULED_DEPARTURE)`.

---

## 3. Missing Value Breakdown & Semantics

| Column | Null Count | Null % | Domain Meaning & Handling Strategy |
| :--- | :--- | :--- | :--- |
| `DEPARTURE_TIME` | 6,517 | 3.02% | **Legitimate Null:** Flight was cancelled before departure. Cannot impute. |
| `DEPARTURE_DELAY` | 6,517 | 3.02% | **Legitimate Null:** Corresponds exactly to cancelled flights. Excluded from delay calculations. |
| `ARRIVAL_TIME` | 6,853 | 3.17% | **Legitimate Null:** Flights cancelled or diverted before landing. |
| `ARRIVAL_DELAY` | 7,419 | 3.43% | **Legitimate Null:** Includes cancelled flights (6,517) plus diverted flights (902) that never arrived at scheduled destination. Excluded from on-time arrival metrics. |
| `TAIL_NUMBER` | 1,268 | 0.59% | Aircraft assignment missing at time of cancellation or data collection. |
| `CANCELLATION_REASON` | 209,273 | 96.89% | **Legitimate Null:** Only populated when `CANCELLED = 1`. For all completed flights, NULL is correct and expected. |
| `DELAY CAUSES` (5 cols) | 176,540 | 81.73% | **Legitimate Null:** As per FAA and DOT reporting standards, causal breakdowns are mandatory **only when `ARRIVAL_DELAY >= 15` minutes**. Flights with `< 15` min delay or on-time flights have NULL delay causes. |

---

## 4. Domain-Specific Business Rules

### A. Negative Delays
- In aviation analytics, **negative delays are valid and expected**.
- A value of `-11.0` in `DEPARTURE_DELAY` indicates the flight departed 11 minutes ahead of schedule.
- A value of `-22.0` in `ARRIVAL_DELAY` indicates the aircraft arrived 22 minutes early.
- **Rule:** Negative delays are **preserved as-is** for arithmetic average delay calculations. For delay classification (`is_delayed`), any flight with `ARRIVAL_DELAY > 15` minutes is classified as delayed in accordance with the standard FAA on-time threshold.

### B. Time Formatting (`HHMM`)
- Scheduled and actual times are stored as 4-character local military time strings (`0005`, `1430`, `2354`).
- Midnight is represented as `0000` or `2400`.
- To establish consistent hour-of-day categories in Pig and Hive, departure hour is extracted using integer division `CAST(SCHEDULED_DEPARTURE / 100 AS INT)` (or string substring `SUBSTRING(time, 1, 2)`).

### C. Cancellation Consistency
- Flights where `CANCELLED = 1` must NOT be counted in average delay averages because they never completed the flight.
- **Cancellation Rate** is computed separately as `(Total Cancelled / Total Scheduled) * 100`.

---

## 5. Filtering and Transformation Policy in AirFlight Pipeline

1. **MySQL / Sqoop Layer:** Raw data is preserved without destructive truncation.
2. **MapReduce Layer:** Only non-null flight delays are accumulated in delay statistics counters.
3. **Pig Layer:** Explicitly tags records with derived fields:
   - `is_delayed`: `1` if `ARRIVAL_DELAY > 15`, else `0`.
   - `delay_category`: `'ON_TIME'` (<=15m), `'SHORT'` (16-45m), `'MEDIUM'` (46-120m), `'SEVERE'` (>120m), `'CANCELLED'` (if CANCELLED=1).
   - `season`: `'WINTER'` (Dec, Jan, Feb), `'SPRING'` (Mar, Apr, May), `'SUMMER'` (Jun, Jul, Aug), `'AUTUMN'` (Sep, Oct, Nov).
   - `time_category`: `'MORNING'` (05:00-11:59), `'AFTERNOON'` (12:00-16:59), `'EVENING'` (17:00-21:59), `'NIGHT'` (22:00-04:59).
