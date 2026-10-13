# AirFlight: Comprehensive Project Report & Technical Documentation

**Project Title:** AirFlight — Big Data-Based Historical Flight Reliability and Delay Decision-Support Application  
**Repository:** [https://github.com/jayanigha07/AirFlight](https://github.com/jayanigha07/AirFlight)  
**System Environment:** Ubuntu 22.04.5 LTS on WSL2 / Bare Metal | Java 8 (OpenJDK 1.8.0_504) | Hadoop 3.3.6 (HDFS & YARN) | MySQL 8.0.46 | Apache Sqoop 1.4.7 | Apache Pig 0.17.0 | Apache Hive 3.1.3 | MongoDB 7.0.43 | Python 3.10.12  
**Target Resource Budget:** Single-Node Distributed Setup tuned for ~7.7 GB RAM + 2 GB Swap  

---

## 1. Project Overview

### 1.1 Problem Statement
Commercial aviation travelers, flight dispatchers, and corporate travel planners frequently encounter unexpected flight delays and cancellations. While modern flight booking engines present pricing, flight duration, and seat availability, they provide virtually no empirical visibility into historical route reliability, schedule punctuality, or seasonal delay propagation patterns. Travelers routinely book high-risk evening departures or carriers with chronic delays without realizing that alternative departure windows on the exact same corridor offer significantly lower disruption rates.

### 1.2 Motivation
Flight delays cost the U.S. economy tens of billions of dollars annually in wasted passenger time, airline crew rescheduling, and missed connections. Flight delay dynamics are rarely random; they exhibit pronounced operational and meteorological patterns:
- Upstream turnaround delays accumulate throughout the operating day, making evening flights substantially more vulnerable than morning flights.
- Convective weather in summer generates widespread holding patterns and long ground delays.
- Winter weather induces spikes in outright flight cancellations due to de-icing queues and airfield closures.

By analyzing multi-month flight performance telemetry across major domestic hubs, it is possible to transform massive historical flight logs into actionable, transparent decision-support metrics.

### 1.3 Objectives
1. **Build an authentic, end-to-end Big Data pipeline** spanning structured relational staging (MySQL), bulk parallel ingestion (Apache Sqoop), distributed file storage (Apache Hadoop HDFS), distributed batch computing (native Java MapReduce & Apache Pig), structured analytical warehousing (Apache Hive), and operational NoSQL document serving (MongoDB).
2. **Execute 100% record-reconciled data transformations** on a verified corpus of **216,000 U.S. commercial flights** without data loss, artificial simulation, or fabrication.
3. **Design and implement deterministic descriptive analytics** for travel decision support:
   - **Best Time to Fly:** Multi-period departure window comparison ranked by an empirical Disruption Score.
   - **Route Reliability Score:** A mathematically bounded 0–100 index quantifying route punctuality, delay severity, and cancellation vulnerability.
   - **Historical Delay Risk Advisor:** A 5-parameter flight itinerary risk evaluator identifying operational patterns and safer historical alternatives.
4. **Deploy an uncluttered, responsive web application and REST API** exposing the analytics engine directly to end users.

### 1.4 Target Audience
- **Business Travelers & Commuters:** Seeking consistent, on-time arrivals for time-sensitive commitments.
- **Leisure Travelers:** Seeking to avoid peak cancellation periods and severe delay bottlenecks.
- **Flight Operations & Travel Coordinators:** Evaluating carrier corridor consistency for schedule optimization.

### 1.5 Fundamental Nature: Historical Decision Support vs. Machine Learning
> [!IMPORTANT]
> **AirFlight is strictly a historical descriptive analytics and decision-support application.**  
> It evaluates real, empirical airline on-time performance records collected by the U.S. Department of Transportation. **It does NOT use machine learning or predictive modeling to forecast future flight delays.**  
> All recommendations, reliability scores, and risk classifications represent historical performance benchmarks across documented operating conditions.

---

## 2. Dataset Specification & Profiling

### 2.1 Dataset Identity & Provenance
- **Dataset Name:** Airline On-Time Performance Data (2015)
- **Source:** Bureau of Transportation Statistics (BTS), U.S. Department of Transportation (DOT)
- **Calendar Year:** 2015 (Full 12 calendar months: January 1 to December 31, 2015)
- **Primary Source Format:** Parquet / CSV
- **Analytical Corpus Size:** **216,000 flight records**

### 2.2 Stratified Sampling Methodology
The full raw DOT dataset encompasses over 5.8 million flight records for 2015. To facilitate deep distributed execution, full-cluster batch computing, and multi-system reconciliation within a single-node WSL2 memory footprint (~7.7 GB RAM), a rigorous **stratified sampling strategy** was implemented via `scripts/prepare_dataset.py`:
$$\text{Sample Size} = 18{,}000 \text{ flights/month} \times 12 \text{ months} = 216{,}000 \text{ flights}$$
This stratified design guarantees equal statistical weight across all calendar seasons (Winter, Spring, Summer, Autumn) while preserving representative distributions across all 14 major commercial carriers and 322 origin/destination airport hubs.

### 2.3 Auxiliary Reference Tables
In addition to the operational flights dataset, AirFlight incorporates 3 normalized reference tables:
1. `airlines.csv` (14 records): Maps 2-letter IATA carrier codes (`AA`, `DL`, `UA`, `WN`, `B6`, `AS`, `NK`, `F9`, `VX`, `HA`, `EV`, `MQ`, `OO`, `US`) to full legal airline names.
2. `airports.csv` (322 records): Maps 3-letter IATA airport codes to airport names, cities, states, countries, latitudes, and longitudes.
3. `cancellation_codes.csv` (4 records): Formal DOT cancellation classifications:
   - `A`: Carrier / Airline Controlled (Maintenance, Crew, Aircraft Availability)
   - `B`: Severe Meteorological Conditions (Weather)
   - `C`: National Aviation System / NAS (Air Traffic Control volume, throughput limitations)
   - `D`: Security Breach / Terminal Evacuation

### 2.4 Core Schema & Field Specifications (31 Columns)
| Field | Type | Nullable | Aviation Meaning & Business Rules |
| :--- | :--- | :--- | :--- |
| `YEAR` | INT | No | Calendar year (2015). |
| `MONTH` | INT | No | Calendar month (1–12). Used for monthly and seasonal segmentation. |
| `DAY` | INT | No | Day of month (1–31). |
| `DAY_OF_WEEK` | INT | No | Day of week (1 = Monday, 7 = Sunday). |
| `AIRLINE` | VARCHAR(10) | No | 2-letter IATA carrier code. Foreign key to `airlines`. |
| `FLIGHT_NUMBER` | INT | No | Flight flight number assigned by carrier. |
| `TAIL_NUMBER` | VARCHAR(15) | Yes | Aircraft registration tail number. |
| `ORIGIN_AIRPORT` | VARCHAR(10) | No | 3-letter IATA departure airport code. |
| `DESTINATION_AIRPORT`| VARCHAR(10) | No | 3-letter IATA arrival airport code. |
| `SCHEDULED_DEPARTURE`| INT | No | Scheduled departure in 24-hr local military time (`HHMM`). |
| `DEPARTURE_TIME` | INT | Yes | Actual gate departure in `HHMM`. NULL for cancelled flights. |
| `DEPARTURE_DELAY` | DOUBLE | Yes | Actual minus scheduled departure in minutes. Negative = early departure. |
| `TAXI_OUT` | DOUBLE | Yes | Gate pushback to wheels-off taxi duration in minutes. |
| `WHEELS_OFF` | INT | Yes | Runway wheels-off time in `HHMM`. |
| `SCHEDULED_TIME` | DOUBLE | Yes | Scheduled flight duration in minutes. |
| `ELAPSED_TIME` | DOUBLE | Yes | Actual gate-to-gate duration in minutes. |
| `AIR_TIME` | DOUBLE | Yes | Airborne flight duration in minutes. |
| `DISTANCE` | INT | No | Great-circle distance between airport endpoints in statutory miles. |
| `WHEELS_ON` | INT | Yes | Runway touchdown time in `HHMM`. |
| `TAXI_IN` | DOUBLE | Yes | Touchdown to gate taxi duration in minutes. |
| `SCHEDULED_ARRIVAL` | INT | No | Scheduled gate arrival time in `HHMM`. |
| `ARRIVAL_TIME` | INT | Yes | Actual gate arrival time in `HHMM`. NULL for cancelled/diverted flights. |
| `ARRIVAL_DELAY` | DOUBLE | Yes | Primary punctuality metric. Actual minus scheduled arrival in minutes. |
| `DIVERTED` | INT | No | `1` if aircraft diverted to alternate airfield; `0` otherwise. |
| `CANCELLED` | INT | No | `1` if flight was cancelled prior to operation; `0` otherwise. |
| `CANCELLATION_REASON`| VARCHAR(10) | Yes | Reason code (`A`, `B`, `C`, `D`). Valid only when `CANCELLED = 1`. |
| `AIR_SYSTEM_DELAY` | DOUBLE | Yes | Delay attributed to FAA Air Traffic System. |
| `SECURITY_DELAY` | DOUBLE | Yes | Delay attributed to airport security re-screening or breach. |
| `AIRLINE_DELAY` | DOUBLE | Yes | Delay within control of carrier (maintenance, baggage, crew). |
| `LATE_AIRCRAFT_DELAY`| DOUBLE | Yes | Delay cascading from late arrival of incoming aircraft on prior leg. |
| `WEATHER_DELAY` | DOUBLE | Yes | Delay caused by severe meteorological conditions. |

### 2.5 Data Quality, Missing Values & Aviation Semantics
Empirical audit of the 216,000 records revealed essential aviation domain rules:
1. **Legitimate Nulls in Delays:** Exactly 6,517 flights had `DEPARTURE_DELAY` as null, matching the exact number of flights cancelled before gate departure. Exactly 7,419 flights had `ARRIVAL_DELAY` as null (6,517 cancelled flights + 902 diverted flights that never completed their scheduled route).
2. **Negative Delay Handling:** Negative delay values (e.g., `-12.0` minutes) indicate early arrivals or departures. In AirFlight, negative delays are preserved in arithmetic average delay calculations, while on-time classification strictly enforces the FAA standard: a flight is delayed if and only if `ARRIVAL_DELAY > 15.0` minutes.
3. **Causal Delay Columns:** As mandated by FAA reporting guidelines, causal delay columns (`AIR_SYSTEM_DELAY`, `WEATHER_DELAY`, etc.) are only recorded when `ARRIVAL_DELAY >= 15` minutes. For completed flights with minor or zero delays, these columns are legitimately null (81.73% of rows).

---

## 3. End-to-End System Architecture

AirFlight implements a specialized distributed batch and operational serving architecture:

```
+----------------------------------------------------------------------------------------------------+
|                                    AIRFLIGHT SYSTEM ARCHITECTURE                                   |
+----------------------------------------------------------------------------------------------------+

 [ RAW DATA SOURCE ]          U.S. DOT Bureau of Transportation Statistics (BTS)
        |                     216,000 Stratified Flight Records (2015)
        v
+-----------------------+     +----------------------------------------------------------------------+
| 1. INGESTION & STAGING| --> | MySQL 8.0 RDBMS                                                      |
|                       |     | - Normalized Tables: flights, airlines, airports, cancellation_codes |
+-----------------------+     +----------------------------------------------------------------------+
        |
        | Apache Sqoop 1.4.7 (2 Parallel Mappers on YARN)
        v
+-----------------------+     +----------------------------------------------------------------------+
| 2. DISTRIBUTED LAKE   | --> | Apache Hadoop HDFS 3.3.6                                             |
|    (Storage Layer)    |     | - /airflight/raw/flights (216,000 lines across 2 part files)         |
|                       |     | - /airflight/raw/{airlines, airports, cancellation_codes}            |
+-----------------------+     +----------------------------------------------------------------------+
        |                                                 |
        | [Java MapReduce on YARN]                        | [Apache Pig 0.17.0 on YARN]
        v                                                 v
+-----------------------+     +----------------------------------------------------------------------+
| 3. DISTRIBUTED BATCH  | --> | Java MapReduce Engine:                                               |
|    COMPUTING          |     | - Custom AirlineStatsWritable, Combiner, Reducer                     |
|                       |     | - Output: /airflight/mapreduce_output/airline_delays                 |
|                       |     |----------------------------------------------------------------------|
|                       |     | Apache Pig Latin Engine:                                             |
|                       |     | - Null conversion, validation, feature engineering                   |
|                       |     | - Output: /airflight/pig_output (216,000 Enriched TSV records)        |
+-----------------------+     +----------------------------------------------------------------------+
                                                          |
                                      +-------------------+-------------------+
                                      |                                       |
                                      v                                       v
+-----------------------+     +-------------------------------+   +----------------------------------+
| 4. WAREHOUSING &      | --> | Apache Hive 3.1.3 Warehouse   |   | MongoDB 7.0 NoSQL Serving Layer  |
|    OPERATIONAL SERVING|     | - External Tables over HDFS   |   | - 216,000 flight documents       |
|                       |     | - 9 Multi-dimensional SQL Qs  |   | - Compound Indexes for latency   |
|                       |     | - Output: /airflight/results/ |   | - Real-time Aggregation Pipeline |
+-----------------------+     +-------------------------------+   +----------------------------------+
                                                                              |
                                                                              v
+-----------------------+     +-------------------------------+   +----------------------------------+
| 5. APPLICATION & UI   | <-- | AirFlight REST API Server     | <-| AirFlight Analytics Engine       |
|    - Single-Page App  |     | (server.py on port 3000)      |   | 1. Best Time to Fly              |
|    - 4 Core Screens   |     | - JSON Endpoints              |   | 2. Route Reliability Score 0-100 |
|    - Tailwind CSS     |     | - Static File Delivery        |   | 3. Historical Delay Risk         |
+-----------------------+     +-------------------------------+   +----------------------------------+
```

### Stage-by-Stage Architectural Flow

| Stage | Purpose | Input | Processing | Output | Storage Location | Transition to Next Stage |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **1. MySQL 8.0** | Relational staging & data integrity | `data/*.csv` files | `LOAD DATA LOCAL INFILE` with null handling | Normalized relational tables | MySQL `airflight` database | Sqoop connects via JDBC `mysql-connector-j` |
| **2. Apache Sqoop** | High-throughput distributed ingestion | MySQL tables | 2 parallel mappers partitioned on `id` | Comma-delimited text with `\N` nulls | HDFS `/airflight/raw/flights` | Stored directly in HDFS for distributed engines |
| **3. Hadoop HDFS** | Distributed data lake storage | Sqoop import streams | Block-level storage (128MB blocks) | Replicated raw files | HDFS `/airflight/raw/*` | Read directly by MapReduce and Pig |
| **4. Java MapReduce** | Carrier performance aggregation | HDFS `/airflight/raw/flights` | Custom Mapper, Combiner, Reducer | Tabular delay metrics for 14 carriers | HDFS `/airflight/mapreduce_output/airline_delays` | Aggregates verified against Hive results |
| **5. Apache Pig** | ETL, data cleansing & feature derivation | HDFS `/airflight/raw/flights` | Pig Latin filtering, `season`, `time_of_day`, `delay_category` | 216,000 TSV records (19.3 MB) | HDFS `/airflight/pig_output` | Serves as external storage for Hive & MongoDB |
| **6. Apache Hive** | Data warehousing & dimensional analytics | HDFS `/airflight/pig_output` + `/airflight/raw/*` | External tables, joins, 9 multi-dimensional SQL queries | 9 persistent result tables | HDFS `/airflight/results/*` | Cross-validates batch metrics |
| **7. MongoDB 7.0** | Low-latency NoSQL operational serving | HDFS `/airflight/pig_output` | Streamed JSON loading, compound index creation | 216,000 documents + carrier summaries | MongoDB `airflight.flights` collection | Python analytics queries MongoDB directly |
| **8. Python Engine** | Deterministic decision-support algorithms | MongoDB collections | Aggregations, Disruption Score, Reliability Score | Structured Python dictionaries | In-memory analytics (`airflight_engine.py`) | Called by HTTP request handler in `server.py` |
| **9. REST API** | Web API & static web serving | HTTP GET/POST queries | Request routing, month/time normalization, JSON serialization | Standardized JSON responses | `server.py` on port 3000 | Frontend fetches via asynchronous JavaScript |
| **10. Frontend UI**| User decision-support interface | REST API endpoints | Dynamic DOM rendering, forms, visual status pills | Rendered HTML/CSS travel decision views | Browser client | User interaction |

---

## 4. Big Data Component Implementation

### 4.1 Relational Staging: MySQL 8.0
- **Database:** `airflight`
- **Schema:** Defined in `mysql/schema.sql`. Contains tables `airlines`, `airports`, `cancellation_codes`, and `flights`.
- **Loading:** `mysql/load_data.sql` executes `LOAD DATA LOCAL INFILE` converting empty string tokens into proper relational `NULL` values.
- **Indexes:** Indexed on `(airline)`, `(origin_airport)`, `(destination_airport)`, `(origin_airport, destination_airport)`, and `(month)`.

### 4.2 Bulk Ingestion: Apache Sqoop 1.4.7
- **Script:** `scripts/sqoop_import_flights.sh`
- **Driver:** MySQL Connector/J (`mysql-connector-j-8.0.33.jar`)
- **Execution:** Runs on YARN with 2 parallel mappers (`-m 2`). Partitions workload across the integer primary key `id` (`--split-by id`).
- **Null Serialization:** Explicitly configured `--null-string '\\N' --null-non-string '\\N'` to ensure missing values are consistently represented as `\N` in HDFS text files.
- **ORM Generation:** Sqoop automatically compiles Java ORM code bindings (`flights.java`, `airlines.java`, `airports.java`, `cancellation_codes.java`) in the workspace.

### 4.3 Distributed File Storage: Apache Hadoop 3.3.6 HDFS
- **Storage Namespace:**
  - `/airflight/raw/flights`: Raw CSV files from Sqoop (`part-m-00000`, `part-m-00001`, total 216,000 rows).
  - `/airflight/raw/{airlines, airports, cancellation_codes}`: Reference tables.
  - `/airflight/mapreduce_output/airline_delays`: Native MapReduce output.
  - `/airflight/pig_output`: Cleaned, typed, and enriched TSV dataset from Pig.
  - `/airflight/hive`: Hive metastore warehouse directory.
  - `/airflight/results/{airline_analysis, airport_analysis, ...}`: Hive analytical output directories.

### 4.4 Distributed Batch Processing: Java MapReduce
- **Source Package:** `com.airflight.mr` (`AirlineDelayStats.java`, `AirlineStatsWritable.java`)
- **Key-Value Architecture:**
  - **Mapper (`FlightDelayMapper`):** Reads raw Sqoop CSV tokens, parses airline, arrival delay, departure delay, and cancellation flags. Emits key `Text(airline)` and custom value `AirlineStatsWritable(1, delayed, cancelled, arrDelay, validArrDelay, depDelay, validDepDelay)`.
  - **Combiner (`FlightDelayCombiner`):** Executes in-memory mini-reduction within mapper JVMs, collapsing 216,000 mapper emissions down to just 27 spilled combiner records across network shuffle boundaries.
  - **Reducer (`FlightDelayReducer`):** Sums counts and delays per carrier, calculating average arrival delay, average departure delay, delay rate %, and cancellation rate %.
- **Output:** Formatted carrier statistics written to `/airflight/mapreduce_output/airline_delays/part-r-00000`.

### 4.5 Data Cleansing & Feature Engineering: Apache Pig 0.17.0
- **Script:** `pig/clean_flights.pig`
- **Execution Mode:** MapReduce on YARN
- **Processing Logic:**
  1. Loads raw comma-delimited tokens using `PigStorage(',')`.
  2. Filters out corrupted records, blank carrier keys, or invalid calendar dates (`month >= 1 AND month <= 12 AND year == 2015`).
  3. Projects derived features:
     - `season`: Mapped from month.
     - `time_of_day`: Mapped from scheduled departure military time.
     - `delay_category`: Categorized according to FAA delay tiers.
  4. Stores enriched records as tab-separated values into `/airflight/pig_output` using `PigStorage('\t')`. Exactly 216,000 records output.

### 4.6 Analytical Data Warehouse: Apache Hive 3.1.3
- **Script:** `hive/airflight_analysis.hql`
- **Metastore:** MySQL database `metastore` via JDBC.
- **External Table:** `airflight_flights` pointing directly to `/airflight/pig_output` with tab-delimited columns.
- **Analytical Reports:** Generates 9 comprehensive analytical tables persisted to HDFS `/airflight/results/`:
  1. `airline_analysis`: Carrier delay and cancellation rates joined with `airflight_airlines`.
  2. `airport_analysis`: Top 30 origin airports ranked by departure delays.
  3. `route_analysis`: Top 100 domestic routes evaluated for delay frequencies.
  4. `monthly_analysis`: 12-month performance trajectory.
  5. `seasonal_analysis`: Performance grouped across Winter, Spring, Summer, Autumn.
  6. `time_of_day_analysis`: Disruption metrics across Morning, Afternoon, Evening, Night.
  7. `delay_category_analysis`: Volume distribution across On-Time, Short, Medium, Severe, Cancelled.
  8. `cancellation_analysis`: Breakdown of cancellation reasons joined with `airflight_cancellation_codes`.
  9. `month_time_matrix`: Cross-tabulation of month and time-of-day.

### 4.7 Operational Serving Layer: MongoDB 7.0
- **Database & Collections:** Database `airflight`, collection `flights` (216,000 documents) and collection `airline_summaries` (14 documents).
- **Ingestion:** `scripts/load_mongo_from_hdfs.py` streams the Pig TSV dataset directly from HDFS into MongoDB in 9.37 seconds.
- **Compound Indexes:**
  - `{ origin_airport: 1, destination_airport: 1, month: 1 }`
  - `{ airline: 1, origin_airport: 1, destination_airport: 1 }`
  - `{ time_of_day: 1 }`
  - `{ delay_category: 1 }`
  - `{ month: 1 }`
- **Role in Application:** Enables sub-10ms operational queries and dynamic aggregation pipelines required by the web application.

---

## 5. Data Transformations & Feature Engineering

All transformations are strictly verified and standardized across the Pig, Hive, MongoDB, and Python layers:

### 5.1 Time-of-Day Windows
AirFlight partitions scheduled departures into 4 standard operational windows:
- **Morning:** `05:00 – 11:59` (`500 <= scheduled_departure <= 1159`)
- **Afternoon:** `12:00 – 16:59` (`1200 <= scheduled_departure <= 1659`)
- **Evening:** `17:00 – 21:59` (`1700 <= scheduled_departure <= 2159`)
- **Night / Red-Eye:** `22:00 – 04:59` (`scheduled_departure >= 2200 OR scheduled_departure < 500`)

### 5.2 Delay Severity Categories
Based on standard FAA operational reporting benchmarks:
- **`ON_TIME`:** Arrival delay $\le 15.0$ minutes (or early arrival).
- **`SHORT`:** Arrival delay between $16.0$ and $45.0$ minutes.
- **`MEDIUM`:** Arrival delay between $46.0$ and $120.0$ minutes.
- **`SEVERE`:** Arrival delay $> 120.0$ minutes.
- **`CANCELLED`:** Flight cancelled prior to operation (`cancelled == 1`).
- **`DIVERTED`:** Flight diverted to an alternate airport (`diverted == 1`).

### 5.3 Seasonal Categorization
- **`WINTER`:** December, January, February (Months 12, 1, 2)
- **`SPRING`:** March, April, May (Months 3, 4, 5)
- **`SUMMER`:** June, July, August (Months 6, 7, 8)
- **`AUTUMN`:** September, October, November (Months 9, 10, 11)

---

## 6. User Features & Analytical Formulations

All analytical capabilities are implemented in `analytics/airflight_engine.py` using MongoDB aggregation pipelines:

### 6.1 Feature 1: Best Time to Fly
- **Input Parameters:** Origin Airport (`origin`), Destination Airport (`dest`), Optional Month (`month` = 1–12 or All Months).
- **Technical Workflow:**
  1. Matches historical flights matching origin, destination, and month (if specified).
  2. Aggregates records by `time_of_day`.
  3. Enforces a **statistical guardrail**: requires at least 10 historical flights on the route (`MIN_SAMPLE_SIZE = 10`), returning `INSUFFICIENT_HISTORICAL_DATA` if sample size is inadequate.
  4. Computes the deterministic **Disruption Score** for each departure period:
     $$\text{Disruption Score} = (\text{DelayRate\%} \times 0.50) + (\max(0, \text{AvgArrDelay}) \times 0.30) + (\text{CancelRate\%} \times 0.20)$$
  5. The departure window with the lowest Disruption Score is designated as the **Historically Best Period**.

### 6.2 Feature 2: Route Reliability Score
- **Input Parameters:** Origin Airport (`origin`), Destination Airport (`dest`).
- **Technical Workflow:**
  1. Aggregates all historical flights operating on the corridor across all carriers and seasons.
  2. Guardrail threshold: requires at least 15 flights (`MIN_SAMPLE_SIZE = 15`).
  3. Calculates three sub-metrics normalized between 0 and 100:
     - **Punctuality Sub-Score ($P_{\text{delay}}$):**
       $$P_{\text{delay}} = \max(0, \min(100, 100 - \text{DelayRate\%}))$$
     - **Delay Severity Sub-Score ($N_{\text{delay}}$):**
       $$N_{\text{delay}} = \max\left(0, \min\left(100, 100 - \frac{\max(0, \text{AvgArrDelay})}{60.0} \times 100\right)\right)$$
     - **Cancellation Sub-Score ($P_{\text{cancel}}$):**
       $$P_{\text{cancel}} = \max(0, \min(100, 100 - (\text{CancelRate\%} \times 5.0)))$$
  4. Computes the composite weighted score:
     $$\text{Reliability Score} = \text{round}(0.50 \times P_{\text{delay}} + 0.30 \times N_{\text{delay}} + 0.20 \times P_{\text{cancel}}, 1)$$
  5. Categorizes the score into descriptive tiers:
     - **`EXCELLENT`:** $\ge 85.0$
     - **`GOOD`:** $75.0 – 84.9$
     - **`MODERATE`:** $60.0 – 74.9$
     - **`POOR`:** $< 60.0$

### 6.3 Feature 3: Historical Delay Risk Advisor
- **Input Parameters:** Airline Carrier (`airline`), Origin (`origin`), Destination (`dest`), Month (`month`), Scheduled Departure Time (`departure_time` in HHMM or window name).
- **Technical Workflow:**
  1. Maps departure time to the corresponding window (`MORNING`, `AFTERNOON`, `EVENING`, `NIGHT`).
  2. Queries historical records matching the exact 5-parameter flight profile. If the specific window sample is under 10 flights, falls back to the carrier's overall corridor volume with clear indication (`sample_scope = OVERALL_ROUTE`).
  3. Classifies operational risk using strict historical boundaries:
     - **`LOW`:** $\text{DelayRate\%} \le 15.0\%$ AND $\text{AvgArrDelay} \le 5.0\text{ min}$ AND $\text{CancelRate\%} \le 2.0\%$
     - **`HIGH`:** $\text{DelayRate\%} \ge 25.0\%$ OR $\text{AvgArrDelay} \ge 15.0\text{ min}$ OR $\text{CancelRate\%} \ge 5.0\%$
     - **`MEDIUM`:** All intermediate profiles.
  4. Generates data-driven **Observed Patterns** explaining the historical causes (e.g., evening turnaround delay cascades, summer thunderstorms, winter cancellations).
  5. Searches for **Better Historical Alternatives**: Identifies other departure windows or carriers operating on the same route that achieved at least a 5% lower historical delay rate with a minimum of 10 flights.

---

## 7. Frontend & REST API Implementation

### 7.1 Web Application Architecture
- **Tech Stack:** HTML5, Vanilla JavaScript, Tailwind CSS (CDN), Material Symbols, Google Fonts (Inter).
- **Architecture:** Responsive Single-Page Application (SPA) driven by client-side routing (`frontend/js/navigation.js`) and API integration (`frontend/js/api.js`, `frontend/js/app.js`), complemented by standalone subpages (`home.html`, `best-time-to-fly.html`, `route-reliability.html`, `historical-risk-advisor.html`).
- **Navigation Bar:** Clean, uncluttered layout:
  $$\text{AirFlight} \mid \text{Home} \mid \text{Best Time to Fly} \mid \text{Route Reliability} \mid \text{Historical Risk}$$
  Includes generous spacing on desktop and a collapsible mobile navigation drawer.
- **UX Design Principle:** Follows strict input-to-insight discipline: `Input Form -> Immediate Clear Answer -> 2–4 Key Metrics -> Useful Visualization/Comparison -> Actionable Details`.

### 7.2 REST API Specification (`server.py`)
Built on Python's native `http.server` running on port 3000.

| Method | Endpoint | Parameters | Purpose | Analytics Function Called |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/airports` | None | Returns list of major commercial hub airports | Hardcoded reference list |
| `GET` | `/api/airlines` | None | Returns list of major commercial carriers | Hardcoded reference list |
| `GET` / `POST` | `/api/best-time` | `origin` (str), `dest` (str), `month` (int/str, optional) | Computes historically best departure window | `AirFlightAnalytics.best_time_to_fly()` |
| `GET` / `POST` | `/api/route-reliability`| `origin` (str), `dest` (str), `month` (optional) | Computes 0–100 reliability score & best/worst periods | `AirFlightAnalytics.route_reliability_score()` |
| `GET` / `POST` | `/api/delay-risk` | `airline` (str), `origin` (str), `dest` (str), `month` (int/str), `time` (int/str) | Computes risk level, observed patterns & alternatives | `AirFlightAnalytics.historical_delay_risk()` |
| `GET` | `/*` | Static file path | Serves frontend assets (`index.html`, `js/*`, etc.) | Native file reader with MIME resolution |

---

## 8. Repository Structure

```
airflight/
├── .gitignore                   # Excludes raw large datasets (>100MB), bytecode, and temp files
├── ARCHITECTURE.md              # Technical architecture specification
├── DATA_DICTIONARY.md           # Field-by-field dataset definitions and types
├── DATA_QUALITY.md              # Empirical data profiling and null-handling rules
├── PROJECT_REPORT.md            # Comprehensive project documentation (This Document)
├── README.md                    # Concise project summary, quick-start, and GitHub overview
├── SETUP.md                     # Complete multi-service installation and configuration guide
├── TROUBLESHOOTING.md           # Resolved issues, memory tuning, and recovery steps
├── VALIDATION.md                # 8-stage record reconciliation and audit results
├── airlines.java                # Sqoop ORM binding generated for airlines table
├── airports.java                # Sqoop ORM binding generated for airports table
├── cancellation_codes.java      # Sqoop ORM binding generated for cancellation_codes table
├── flights.java                 # Sqoop ORM binding generated for flights table
├── server.py                    # AirFlight REST API & static web application server
├── sqoop_test_airlines.java     # Sqoop test ORM binding
├── test_months.py               # Automated verification script for month filtering
├── analytics/
│   └── airflight_engine.py      # Core descriptive analytics engine (MongoDB queries)
├── data/
│   ├── airlines.csv             # 14 commercial carriers reference data
│   ├── airports.csv             # 322 commercial airport endpoints reference data
│   ├── cancellation_codes.csv   # 4 DOT cancellation categories reference data
│   ├── dataset_profile.json     # Empirical profiling statistics
│   └── flights_raw_200k.csv     # 216,000 stratified flight records (24.13 MB)
├── data_raw/                    # Staging space for source files (flights.parquet excluded from git)
├── frontend/
│   ├── best-time-to-fly.html    # Standalone view for Best Time to Fly
│   ├── historical-risk-advisor.html # Standalone view for Historical Risk Advisor
│   ├── home.html                # Standalone landing page
│   ├── index.html               # Main Single-Page Application (SPA)
│   ├── route-reliability.html   # Standalone view for Route Reliability
│   └── js/
│       ├── api.js               # REST API client
│       ├── app.js               # Event handlers and UI view rendering logic
│       └── navigation.js        # Client-side router and mobile menu handler
├── hive/
│   └── airflight_analysis.hql   # Hive database, external tables, and 9 analytical queries
├── mapreduce/
│   ├── airflight-mapreduce.jar  # Pre-compiled JAR for Hadoop YARN execution
│   └── src/com/airflight/mr/
│       ├── AirlineDelayStats.java     # Java MapReduce driver, Mapper, Combiner, Reducer
│       └── AirlineStatsWritable.java  # Custom Hadoop Writable value class
├── mongodb/
│   └── aggregations.js          # MongoDB compound index creation and verification pipelines
├── mysql/
│   ├── load_data.sql            # Script loading CSVs into MySQL with null conversion
│   └── schema.sql               # Relational DDL definitions and index creation
├── pig/
│   └── clean_flights.pig        # Pig Latin ETL cleaning and feature engineering script
└── scripts/
    ├── download_ecosystem.sh    # Ecosystem tarball downloader
    ├── load_mongo_from_hdfs.py  # High-throughput streaming loader from HDFS to MongoDB
    ├── prepare_dataset.py       # Stratified 18,000/month dataset sampler
    ├── profile_dataset.py       # Empirical data profiling script
    ├── run_hive.sh              # Execution wrapper for Hive warehouse analytics
    ├── run_mapreduce.sh         # Compilation and execution wrapper for MapReduce on YARN
    ├── run_pig.sh               # Execution wrapper for Pig Latin on YARN
    ├── setup_global_env.sh      # Sets Big Data environment variables in /etc/profile.d
    ├── setup_hadoop_config.sh   # Configures core-site.xml, hdfs-site.xml, yarn-site.xml
    ├── setup_hadoop_profile.sh  # Sets Hadoop user profiles
    ├── setup_hive.sh            # Downloads, configures, and initializes Hive metastore
    ├── setup_pig.sh             # Configures Pig
    ├── setup_sqoop.sh           # Configures Sqoop with MySQL connector
    ├── show_profile.py          # Displays summary profiling metrics
    ├── sqoop_import_flights.sh  # Executes Sqoop parallel import from MySQL into HDFS
    ├── start_hadoop_all.sh      # Safe startup wrapper for all Hadoop/YARN daemons
    ├── test_hive.sh             # Quick verification script for Hive
    ├── test_pig.sh              # Quick verification script for Pig
    ├── test_sqoop_import.sh     # Quick verification script for Sqoop
    ├── tune_yarn_memory.sh      # Memory optimization script for ~7.7 GB WSL2 RAM
    └── validate_pipeline.py     # End-to-end 8-stage record reconciliation auditor
```

---

## 9. Pipeline Validation & Benchmark Verification

### 9.1 Cross-Stage Record Reconciliation (100% Match)
Executed live via `scripts/validate_pipeline.py`. Every stage was audited and reconciled:

```
==========================================================
AIRFLIGHT: END-TO-END PIPELINE VALIDATION AUDIT
==========================================================
1. RAW DATASET (CSV):              216,000 flight rows
2. MYSQL TABLE (flights):          216,000 records
3. SQOOP IMPORT (HDFS /raw):       216,000 records
4. MAPREDUCE OUTPUT (carriers):    14 carrier groups (from 216,000 mapped records)
5. PIG OUTPUT (HDFS /pig_output):   216,000 enriched records
6. HIVE TABLE (airflight_flights): 216,000 records
7. MONGODB (airflight.flights):    216,000 documents
   MONGODB (airline_summaries):    14 carrier summaries
8. AIRFLIGHT ANALYTICS ENGINE:
   - Best Time to Fly (SFO->LAX, Month 7): AFTERNOON (Score: 21.16)
   - Route Reliability Score (ORD->LGA):   70.3/100 (MODERATE)
   - Historical Delay Risk (UA, ORD->LGA): HIGH (Delay Rate: 24.32%)
==========================================================
PIPELINE AUDIT: ALL 216,000 RECORDS 100% RECONCILED ACROSS ALL 8 STAGES!
```

### 9.2 Verified Analytical Benchmarks

#### Benchmark 1: ORD $\rightarrow$ LGA Route Reliability Score
- **Route:** Chicago O'Hare (`ORD`) to New York LaGuardia (`LGA`)
- **Total Historical Sample Size:** **364 flights**
- **Delayed Flights:** 93 flights (**25.55%**)
- **Cancelled Flights:** 38 flights (**10.44%**)
- **Average Arrival Delay:** **12.97 minutes**
- **Sub-Score Calculations:**
  - $P_{\text{delay}} = 100 - 25.55 = 74.45$
  - $N_{\text{delay}} = 100 - (12.97 / 60.0 \times 100) = 78.39$
  - $P_{\text{cancel}} = 100 - (10.44 \times 5.0) = 47.80$
- **Composite Score:**
  $$\text{Score} = 0.50(74.45) + 0.30(78.39) + 0.20(47.80) = 37.23 + 23.52 + 9.56 = \mathbf{70.3} / 100$$
- **Reliability Tier:** **`MODERATE`**

#### Benchmark 2: SFO $\rightarrow$ LAX Best Time to Fly (July / Month 7)
- **Route:** San Francisco (`SFO`) to Los Angeles (`LAX`), Month 7 (July)
- **Total Route Flights Found:** 40 flights
- **Period Breakdown:**
  - **`AFTERNOON` (Winner):** 14 flights, 35.71% delay rate, 11.00 min avg delay, 0.00% cancellations. **Disruption Score: 21.16**
  - **`EVENING`:** 12 flights, 41.67% delay rate, 18.58 min avg delay, 0.00% cancellations. **Disruption Score: 26.41**
  - **`MORNING`:** 14 flights, 50.00% delay rate, 5.93 min avg delay, 0.00% cancellations. **Disruption Score: 26.78**
- **Outcome:** Afternoon is historically optimal, with the lowest Disruption Score (21.16).

#### Benchmark 3: UA ORD $\rightarrow$ LGA Historical Delay Risk (June, Evening)
- **Itinerary:** United Airlines (`UA`), `ORD` to `LGA`, Month 6 (June), Scheduled Departure `18:30` (Evening window)
- **Sample Size:** 37 historical flights
- **Metrics:** 24.32% delay rate, 23.23 min avg delay, 16.22% cancellation rate.
- **Assessed Risk Level:** **`HIGH`**
- **Identified Pattern:** Evening turnaround delay accumulation compounded by summer convective air-traffic congestion.
- **Better Historical Alternative:** `UA MORNING` (68 flights, 17.65% delay rate, 4.65 min avg delay).

#### Benchmark 4: Statistical Guardrail Fallback
- **Itinerary:** `BDL` $\rightarrow$ `ANC` (Bradley Intl to Anchorage), Month 1
- **Result:** Status `INSUFFICIENT_HISTORICAL_DATA`
- **Output:** Correctly alerts user that historical flight count (0 flights) is below the minimum confidence threshold (10 flights).

---

## 10. Technology Stack & Verified Versions

| Technology | Verified Version | Exact Role in AirFlight Project |
| :--- | :--- | :--- |
| **Java** | OpenJDK `1.8.0_504` | Runtime for Hadoop daemons, YARN, MapReduce, Pig, Hive, and Sqoop |
| **Apache Hadoop** | `3.3.6` | Distributed infrastructure: NameNode, DataNode, ResourceManager, NodeManager, JobHistoryServer |
| **Hadoop HDFS** | `3.3.6` | Distributed file system storing raw CSVs, intermediate TSVs, and analytical report directories |
| **Apache YARN** | `3.3.6` | Cluster compute resource manager governing MapReduce, Pig, and Hive execution pools |
| **MySQL** | `8.0.46` (Ubuntu) | Structured RDBMS for initial data staging and Hive Metastore relational backing |
| **Apache Sqoop** | `1.4.7` | Bulk data transfer bridging MySQL into HDFS using parallel map-only YARN jobs |
| **Apache Pig** | `0.17.0` | High-level data-flow engine performing ETL, null filtering, and feature derivation on YARN |
| **Apache Hive** | `3.1.3` | Analytical data warehouse executing 9 multi-dimensional SQL queries over HDFS external tables |
| **MongoDB** | `7.0.43` | Operational NoSQL document database providing low-latency serving and compound index querying |
| **Python** | `3.10.12` | Analytics engine (`airflight_engine.py`) and REST API web server (`server.py`) |
| **PyMongo** | `4.6.0+` | Python MongoDB driver executing aggregation pipelines and operational queries |
| **Frontend Stack**| HTML5 / JS / Tailwind CSS | Responsive, zero-build UI providing interactive travel decision-support screens |

---

## 11. Run Guide

### 11.1 Quick Demo (Application Serving Mode)
If the Big Data pipeline has already executed and populated MongoDB:

```bash
# 1. Start MongoDB (if not running)
sudo systemctl start mongod

# 2. Launch AirFlight Web & REST API Server
cd /mnt/c/Users/DELL/Desktop/airflight
python3 server.py 3000

# 3. Access in browser
# http://localhost:3000/
```

### 11.2 Full Pipeline Execution (From Ingestion to Serving)
To reproduce the complete Big Data pipeline from scratch:

```bash
# Step 1: Set Environment Variables
source /etc/profile.d/00-bigdata.sh

# Step 2: Start All Relational, NoSQL, and Hadoop Daemons
sudo systemctl start mysql
sudo systemctl start mongod
/mnt/c/Users/DELL/Desktop/airflight/scripts/start_hadoop_all.sh

# Step 3: Verify Running Services
jps
# Expected: NameNode, DataNode, SecondaryNameNode, ResourceManager, NodeManager, JobHistoryServer

# Step 4: Stage Raw Data in MySQL
mysql -u airflight -pairflight123 airflight < /mnt/c/Users/DELL/Desktop/airflight/mysql/schema.sql
mysql --local-infile=1 -u airflight -pairflight123 airflight < /mnt/c/Users/DELL/Desktop/airflight/mysql/load_data.sql

# Step 5: Ingest into HDFS via Apache Sqoop
/mnt/c/Users/DELL/Desktop/airflight/scripts/sqoop_import_flights.sh

# Step 6: Execute Java MapReduce on YARN
/mnt/c/Users/DELL/Desktop/airflight/scripts/run_mapreduce.sh

# Step 7: Execute Apache Pig ETL & Feature Engineering on YARN
/mnt/c/Users/DELL/Desktop/airflight/scripts/run_pig.sh

# Step 8: Execute Apache Hive Data Warehouse Analytics
/mnt/c/Users/DELL/Desktop/airflight/scripts/run_hive.sh

# Step 9: Ingest Processed Data into MongoDB
python3 /mnt/c/Users/DELL/Desktop/airflight/scripts/load_mongo_from_hdfs.py

# Step 10: Create MongoDB Indexes & Run Aggregation Verifications
mongosh < /mnt/c/Users/DELL/Desktop/airflight/mongodb/aggregations.js

# Step 11: Execute End-to-End Pipeline Validation
python3 /mnt/c/Users/DELL/Desktop/airflight/scripts/validate_pipeline.py

# Step 12: Start Web Application
python3 /mnt/c/Users/DELL/Desktop/airflight/server.py 3000
```

---

## 12. Conclusion & Architectural Summary

AirFlight demonstrates a complete, production-grade Big Data engineering lifecycle within a carefully constrained single-node environment (~7.7 GB RAM). By coordinating MySQL, Sqoop, HDFS, MapReduce, Pig, Hive, and MongoDB into a cohesive Lambda-inspired batch and serving architecture, the platform reconciles **216,000 real U.S. flight records with 100% mathematical fidelity across all 8 stages**. Downstream, the deterministic analytics engine and modern web frontend turn raw telemetry into intuitive, empirical travel decision support—demonstrating the full power of the modern Big Data ecosystem.
