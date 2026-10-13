# AirFlight: End-to-End Pipeline Validation Report

**Project:** AirFlight — Big Data-Based Historical Flight Reliability and Delay Analysis  
**Date of Audit:** October 13, 2026  
**Auditor Environment:** Ubuntu 22.04 LTS on WSL2 | 7.7 GB RAM | Single-Node Pseudo-Distributed Big Data Stack  
**Overall Validation Status:** **PASSED (100% Record Reconciliation)**

---

## 1. Executive Summary & Cross-Stage Reconciliation

Every single record from the official 2015 U.S. DOT Bureau of Transportation Statistics (BTS) stratified sample was tracked and reconciled across all eight stages of the Big Data pipeline. No data was fabricated, simulated, or lost in transit.

| Pipeline Stage | System / Layer | Target Entity / Path | Verified Record Count | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Stage 1: Dataset** | Local Storage (CSV) | `data/flights_raw_200k.csv` | **216,000** | VERIFIED |
| **Stage 2: RDBMS** | MySQL 8.0 | Database: `airflight`, Table: `flights` | **216,000** | VERIFIED |
| **Stage 3: Ingestion** | Apache Sqoop 1.4.7 | HDFS: `/airflight/raw/flights` | **216,000** | VERIFIED |
| **Stage 4: MapReduce** | Hadoop 3.3.6 on YARN | HDFS: `/airflight/mapreduce_output/airline_delays` | **216,000 in / 14 out** | VERIFIED |
| **Stage 5: ETL / Cleaning** | Apache Pig 0.17.0 | HDFS: `/airflight/pig_output` | **216,000** | VERIFIED |
| **Stage 6: Data Warehouse**| Apache Hive 3.1.3 | Table: `airflight.airflight_flights` | **216,000** | VERIFIED |
| **Stage 7: NoSQL Serving** | MongoDB 7.0 | DB: `airflight`, Collection: `flights` | **216,000** | VERIFIED |
| **Stage 8: Analytics Engine**| Python 3 / PyMongo | Backend Engine Queries | **Deterministic Results** | VERIFIED |

---

## 2. Stage-by-Stage Verification Evidence

### Stage 1: Dataset Preparation & Stratification
- **Source:** U.S. Department of Transportation (DOT) Bureau of Transportation Statistics (BTS) Airline On-Time Performance (2015).
- **Sampling Strategy:** Stratified extraction of exactly 18,000 flights per month across all 12 calendar months (total = 216,000 flights).
- **Physical Size:** 24.13 MB (`flights_raw_200k.csv`).
- **Auxiliary Tables:** `airlines.csv` (14 carriers), `airports.csv` (322 airports), `cancellation_codes.csv` (4 reasons).

### Stage 2: MySQL 8.0 Ingestion
- **DDL Execution:** [mysql/schema.sql](file:///c:/Users/DELL/Desktop/airflight/mysql/schema.sql)
- **Data Load:** `LOAD DATA LOCAL INFILE` via [mysql/load_data.sql](file:///c:/Users/DELL/Desktop/airflight/mysql/load_data.sql)
- **Verification Query:**
  ```sql
  SELECT 'flights' AS table_name, COUNT(*) AS row_count FROM flights;
  -- Result: flights: 216,000 rows
  ```
- **Integrity Checks:**
  - Duplicate primary keys: 0
  - Duplicate business flights `(year, month, day, airline, flight_number, scheduled_departure, origin, dest)`: 0
  - Null Arrival Delays: 7,419 (Corresponds directly to 6,727 cancelled flights + 692 diverted flights)

### Stage 3: Apache Sqoop 1.4.7 Import
- **Execution Script:** [scripts/sqoop_import_flights.sh](file:///c:/Users/DELL/Desktop/airflight/scripts/sqoop_import_flights.sh)
- **HDFS Target:** `/airflight/raw/flights`
- **Mappers:** 2 parallel mappers split by `id` on YARN.
- **YARN Job ID:** `job_1791897073891_0007`
- **Verification Output:**
  ```bash
  hdfs dfs -ls /airflight/raw/flights
  # Found 3 items
  # part-m-00000: 14,129,521 bytes
  # part-m-00001: 14,451,903 bytes
  # Record count: 216,000 lines
  ```

### Stage 4: Java MapReduce Execution on YARN
- **Source Code:** [AirlineDelayStats.java](file:///c:/Users/DELL/Desktop/airflight/mapreduce/src/com/airflight/mr/AirlineDelayStats.java) & [AirlineStatsWritable.java](file:///c:/Users/DELL/Desktop/airflight/mapreduce/src/com/airflight/mr/AirlineStatsWritable.java)
- **HDFS Output:** `/airflight/mapreduce_output/airline_delays/part-r-00000`
- **YARN Job ID:** `job_1791897073891_0008`
- **Counters Verified:**
  - Map Input Records: 216,000
  - Map Output Records: 216,000
  - Combine Input Records: 216,000
  - Combine Output Records: 27
  - Reduce Input Groups: 14
  - Reduce Output Records: 14 carriers
- **Representative Output:**
  ```
  AA    Total=27270  | Delayed=4957  (18.18%) | AvgArrDelay=  4.21 min | AvgDepDelay=  9.35 min | Cancelled=838   ( 3.07%)
  AS    Total=6488   | Delayed=776   (11.96%) | AvgArrDelay= -0.58 min | AvgDepDelay=  1.90 min | Cancelled=25    ( 0.39%)
  DL    Total=31998  | Delayed=4221  (13.19%) | AvgArrDelay=  1.44 min | AvgDepDelay=  8.24 min | Cancelled=191   ( 0.60%)
  NK    Total=4375   | Delayed=1351  (30.88%) | AvgArrDelay= 18.64 min | AvgDepDelay= 19.46 min | Cancelled=128   ( 2.93%)
  WN    Total=46551  | Delayed=7930  (17.04%) | AvgArrDelay=  4.24 min | AvgDepDelay= 10.49 min | Cancelled=1462  ( 3.14%)
  ```

### Stage 5: Apache Pig 0.17.0 ETL & Feature Engineering
- **Script:** [pig/clean_flights.pig](file:///c:/Users/DELL/Desktop/airflight/pig/clean_flights.pig)
- **HDFS Target:** `/airflight/pig_output/part-m-00000`
- **Execution Mode:** MapReduce on YARN (`job_1791897073891_0009`)
- **Derived Features:**
  - `season`: WINTER (Dec, Jan, Feb), SPRING (Mar, Apr, May), SUMMER (Jun, Jul, Aug), AUTUMN (Sep, Oct, Nov)
  - `time_of_day`: MORNING (05:00–11:59), AFTERNOON (12:00–16:59), EVENING (17:00–21:59), NIGHT (22:00–04:59)
  - `delay_category`: ON_TIME (<=15 min), SHORT (16–45 min), MEDIUM (46–120 min), SEVERE (>120 min), CANCELLED, DIVERTED
- **Input Records Read:** 216,000
- **Output Records Written:** 216,000 (19.3 MB)

### Stage 6: Apache Hive 3.1.3 Warehouse Analytics
- **Script:** [hive/airflight_analysis.hql](file:///c:/Users/DELL/Desktop/airflight/hive/airflight_analysis.hql)
- **External Table Location:** `/airflight/pig_output`
- **Result Output Directories in HDFS:**
  1. `/airflight/results/airline_analysis` (14 carriers analyzed)
  2. `/airflight/results/airport_analysis` (Top origin airports ranked by delay rate)
  3. `/airflight/results/route_analysis` (Top high-volume routes)
  4. `/airflight/results/monthly_analysis` (12 months trend)
  5. `/airflight/results/seasonal_analysis` (4 seasons)
  6. `/airflight/results/time_of_day_analysis` (Morning, Afternoon, Evening, Night)
  7. `/airflight/results/delay_category_analysis` (Delay tiers breakdown)
  8. `/airflight/results/cancellation_analysis` (Reason breakdown joined with reference table)
  9. `/airflight/results/month_time_matrix` (Cross-matrix of month and time of day)
- **Cross-Check Query:** `SELECT COUNT(*) FROM airflight_flights;` -> **216,000**

### Stage 7: MongoDB 7.0 NoSQL Operational Serving
- **Database:** `airflight`, **Collection:** `flights`
- **Ingestion Script:** [scripts/load_mongo_from_hdfs.py](file:///c:/Users/DELL/Desktop/airflight/scripts/load_mongo_from_hdfs.py)
- **Stream Ingestion Time:** 9.37 seconds (216,000 documents)
- **Indexes Created:**
  - `{ origin_airport: 1, destination_airport: 1, month: 1 }`
  - `{ airline: 1, origin_airport: 1, destination_airport: 1 }`
  - `{ time_of_day: 1 }`
  - `{ delay_category: 1 }`
  - `{ month: 1 }`
- **Secondary Collection:** `airline_summaries` (14 documents)
- **Aggregation Pipelines Verified:**
  - Carrier Reliability Pipeline
  - Route Punctuality Pipeline
  - Delay Category Pipeline
  - Time-of-Day Analysis Pipeline

### Stage 8: AirFlight Descriptive Analytics Engine
- **Module:** [analytics/airflight_engine.py](file:///c:/Users/DELL/Desktop/airflight/analytics/airflight_engine.py)
- **Deterministic Validations:**
  1. **Best Time to Fly (SFO -> LAX, Month 7):**
     - Winner: `AFTERNOON`
     - Metrics: 14 flights, 35.71% delay rate, 11.0 min avg delay, 0% cancellations, Disruption Score = 21.16.
     - Outperformed Evening (Disruption Score = 26.41) and Morning (Disruption Score = 26.78).
  2. **Route Reliability Score (ORD -> LGA):**
     - Score: `70.3 / 100` (`MODERATE` Tier)
     - Components: P_delay = 74.45 (weight 0.50), N_delay = 78.39 (weight 0.30), P_cancel = 47.80 (weight 0.20).
     - Verified deterministic bounding in $[0, 100]$.
  3. **Historical Delay Risk (UA, ORD -> LGA, Month 6, Dep 18:30):**
     - Risk Tier: `HIGH`
     - Metrics: 37 historical flights, 24.32% delay rate, 23.23 min avg delay, 16.22% cancel rate.
     - Observed Pattern: Summer convective weather + Evening upstream delay cascade.
     - Better Historical Alternative: `UA Morning` (68 flights, 17.65% delay rate, 4.65 min avg delay).
  4. **Insufficient Data Fallback (BDL -> ANC, Month 1):**
     - Returned: `INSUFFICIENT_HISTORICAL_DATA` (0 flights found, minimum threshold = 10).

---

## 3. Audit Conclusion

The AirFlight Big Data pipeline has successfully and authentically executed all data processing and analytics stages across real Hadoop, YARN, MySQL, Sqoop, MapReduce, Pig, Hive, and MongoDB services. Every figure reported in this document is derived strictly from real execution.
