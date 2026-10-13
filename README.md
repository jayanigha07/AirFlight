# AirFlight: Big Data Historical Flight Reliability & Delay Decision-Support Application

[![Platform](https://img.shields.io/badge/Platform-Ubuntu%2022.04%20LTS%20(WSL2)-orange.svg)](https://ubuntu.com)
[![Hadoop](https://img.shields.io/badge/Hadoop-3.3.6-blue.svg)](https://hadoop.apache.org)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0.43-green.svg)](https://www.mongodb.com)
[![Dataset](https://img.shields.io/badge/BTS%20Records-216%2C000%20(100%25%20Verified)-purple.svg)](https://www.transtats.bts.gov)
[![Status](https://img.shields.io/badge/Pipeline%20Reconciliation-PASSED-brightgreen.svg)](#10-pipeline-validation--audit)

> **Detailed Technical Report:** For academic submission, comprehensive architecture diagrams, formula derivations, and deep technical details, please refer to [PROJECT_REPORT.md](PROJECT_REPORT.md).

---

## 1. Project Overview

**AirFlight** is an authentic, production-grade Big Data engineering and travel decision-support web application. Operating on a single-node distributed Big Data cluster (Java 8 + Hadoop 3.3.6 + HDFS + YARN + MySQL 8.0 + Sqoop 1.4.7 + Pig 0.17 + Hive 3.1.3 + MongoDB 7.0), the platform ingests, cleans, warehouses, aggregates, and serves historical flight operational records from the **U.S. Department of Transportation (DOT) Bureau of Transportation Statistics (BTS)**.

Unlike generic dashboards or speculative machine learning predictors, AirFlight is designed as an **empirical, historical flight reliability decision-support application**:
- It evaluates **real historical flight records** across commercial carriers and airport corridors.
- **It does NOT predict future flight delays using ML or synthetic estimation.** All metrics reflect verified historical performance under real operating conditions.
- It enforces strict statistical guardrails (`MIN_SAMPLE_SIZE = 10`), falling back to `INSUFFICIENT_HISTORICAL_DATA` when sample confidence is low.

---

## 2. Key User Features

1. **Best Time to Fly:**
   - Evaluates historical departure windows (Morning, Afternoon, Evening, Night) for any airport pair.
   - Supports filtering by specific calendar month (`January`–`December`) or `All months`.
   - Computes an empirical **Disruption Score** to identify the historically optimal departure time.
2. **Route Reliability Score (0–100):**
   - Computes a deterministic 0–100 score based on punctuality, normalized delay duration, and cancellation rate.
   - Classifies corridors into `EXCELLENT`, `GOOD`, `MODERATE`, or `POOR` tiers.
   - Highlights best and most challenging departure windows on the route.
3. **Historical Delay Risk Advisor:**
   - Evaluates specific flight itineraries (Carrier + Origin + Destination + Month + Departure Window).
   - Classifies risk as `LOW`, `MEDIUM`, or `HIGH`.
   - Explains historical causes (e.g., evening delay accumulation, convective summer thunderstorms, winter cancellations).
   - Searches for **Better Historical Alternatives** (alternative carriers or departure periods on the same route with lower delay rates).

---

## 3. End-to-End Architecture

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

---

## 4. Technology Stack

| Technology | Verified Version | Role in AirFlight Project |
| :--- | :--- | :--- |
| **Java** | OpenJDK `1.8.0_504` | Runtime for Hadoop daemons, YARN, MapReduce, Pig, Hive, and Sqoop |
| **Apache Hadoop** | `3.3.6` | Distributed cluster: NameNode, DataNode, ResourceManager, NodeManager |
| **Hadoop HDFS** | `3.3.6` | Distributed storage: raw CSVs, intermediate TSVs, analytical reports |
| **Apache YARN** | `3.3.6` | Workload scheduler tuned for WSL2 memory budget (~7.7 GB RAM) |
| **MySQL** | `8.0.46` | Structured relational staging and Hive Metastore backend |
| **Apache Sqoop** | `1.4.7` | High-throughput parallel JDBC import from MySQL into HDFS |
| **Apache Pig** | `0.17.0` | Pig Latin ETL cleaning, null handling, and feature engineering |
| **Apache Hive** | `3.1.3` | Analytical data warehouse executing 9 multi-dimensional SQL queries |
| **MongoDB** | `7.0.43` | Operational NoSQL store with compound indexes for low-latency queries |
| **Python** | `3.10.12` | Analytics engine (`airflight_engine.py`) and API server (`server.py`) |
| **Frontend** | HTML5 / Vanilla JS / Tailwind | Modern travel decision-support application with 4 views |

---

## 5. Verified Dataset Specifications

- **Source:** U.S. DOT Bureau of Transportation Statistics (BTS) Airline On-Time Performance (2015).
- **Sampling Strategy:** Stratified multi-month extraction:
  $$\text{Sample Size} = 18{,}000 \text{ flights/month} \times 12 \text{ months} = 216{,}000 \text{ flights}$$
- **Auxiliary Reference Tables:**
  - `airlines.csv` (14 major commercial carriers)
  - `airports.csv` (322 airport stations with geospatial coordinates)
  - `cancellation_codes.csv` (4 standardized cancellation causes: Carrier, Weather, NAS, Security)
- **Derived Dimensions:**
  - `season`: WINTER (Dec, Jan, Feb), SPRING (Mar, Apr, May), SUMMER (Jun, Jul, Aug), AUTUMN (Sep, Oct, Nov).
  - `time_of_day`: MORNING (05:00–11:59), AFTERNOON (12:00–16:59), EVENING (17:00–21:59), NIGHT (22:00–04:59).
  - `delay_category`: ON_TIME ($\le 15$m), SHORT (16–45m), MEDIUM (46–120m), SEVERE ($> 120$m), CANCELLED, DIVERTED.

---

## 6. Big Data Pipeline Stages

1. **MySQL Staging:** Normalized relational tables loaded via `LOAD DATA LOCAL INFILE` with null conversion.
2. **Sqoop Ingestion:** Parallel batch ingestion to HDFS `/airflight/raw/flights` across 2 mappers on YARN. Generates Java ORM bindings (`flights.java`).
3. **MapReduce Computation:** `com.airflight.mr.AirlineDelayStats` aggregates delay statistics per carrier on YARN using a custom Combiner.
4. **Pig Latin ETL:** `pig/clean_flights.pig` cleans records, resolves nulls, projects derived features, and outputs 216,000 TSV records to `/airflight/pig_output`.
5. **Hive Warehousing:** `hive/airflight_analysis.hql` defines external tables over HDFS and persists 9 analytical reports to `/airflight/results/`.
6. **MongoDB Serving:** `scripts/load_mongo_from_hdfs.py` streams 216,000 documents into MongoDB with compound indexes for fast operational retrieval.
7. **Python Analytics Engine:** `analytics/airflight_engine.py` executes aggregation pipelines for Best Time to Fly, Route Reliability Score, and Delay Risk.
8. **REST API & Web UI:** `server.py` delivers JSON endpoints and serves the frontend application on `http://localhost:3000`.

---

## 7. Repository Directory Structure

```
airflight/
├── .gitignore                   # Excludes raw large datasets (>100MB), bytecode, and temp files
├── ARCHITECTURE.md              # Technical architecture specification
├── DATA_DICTIONARY.md           # Field-by-field dataset definitions and types
├── DATA_QUALITY.md              # Empirical data profiling and null-handling rules
├── PROJECT_REPORT.md            # Comprehensive project documentation
├── README.md                    # Project overview & quick-start guide (This Document)
├── SETUP.md                     # Complete multi-service installation and configuration guide
├── TROUBLESHOOTING.md           # Encountered issues and exact solutions
├── VALIDATION.md                # 8-stage record reconciliation and audit results
├── airlines.java                # Sqoop ORM binding generated for airlines table
├── airports.java                # Sqoop ORM binding generated for airports table
├── cancellation_codes.java      # Sqoop ORM binding generated for cancellation_codes table
├── flights.java                 # Sqoop ORM binding generated for flights table
├── server.py                    # AirFlight REST API & static web application server
├── sqoop_test_airlines.java     # Sqoop test ORM binding
├── test_months.py               # Month-filtering test script
├── analytics/
│   └── airflight_engine.py      # Core descriptive analytics engine (MongoDB queries)
├── data/
│   ├── airlines.csv             # 14 commercial carriers reference data
│   ├── airports.csv             # 322 commercial airport endpoints reference data
│   ├── cancellation_codes.csv   # 4 DOT cancellation categories reference data
│   ├── dataset_profile.json     # Empirical profiling statistics
│   └── flights_raw_200k.csv     # 216,000 stratified flight records (24.13 MB)
├── data_raw/                    # Raw source folder (large parquet excluded from git)
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
│   └── clean_flights.pig        # Pig Latin cleaning & feature engineering script
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

## 8. Quick Start Guide

### Quick Application Demo (Pre-Loaded Database Mode)
```bash
# 1. Start MongoDB
sudo systemctl start mongod

# 2. Run the AirFlight Web Server
python3 server.py 3000

# 3. Open in Browser:
# http://localhost:3000/
```

### Full Pipeline Reproduction
```bash
# 1. Load Big Data Environment
source /etc/profile.d/00-bigdata.sh

# 2. Start Services
sudo systemctl start mysql mongod
./scripts/start_hadoop_all.sh

# 3. Run Pipeline Stages
mysql -u airflight -pairflight123 airflight < mysql/schema.sql
mysql --local-infile=1 -u airflight -pairflight123 airflight < mysql/load_data.sql
./scripts/sqoop_import_flights.sh
./scripts/run_mapreduce.sh
./scripts/run_pig.sh
./scripts/run_hive.sh
python3 ./scripts/load_mongo_from_hdfs.py
mongosh < mongodb/aggregations.js

# 4. Audit Pipeline
python3 ./scripts/validate_pipeline.py

# 5. Launch Application
python3 server.py 3000
```

---

## 9. Verified Analytical Benchmarks

### Benchmark 1: ORD $\rightarrow$ LGA Route Reliability Score
- **Corridor:** Chicago O'Hare (`ORD`) to New York LaGuardia (`LGA`)
- **Reliability Score:** **70.3 / 100** (`MODERATE` Tier)
- **Total Flights Analyzed:** **364 flights**
- **Delay Rate:** **25.55%** (93 delayed flights)
- **Average Arrival Delay:** **12.97 minutes**
- **Cancellation Rate:** **10.44%** (38 cancelled flights)
- **Score Breakdown:** $P_{\text{delay}} = 74.45$, $N_{\text{delay}} = 78.39$, $P_{\text{cancel}} = 47.80$
- **Composite Formula:** $\text{Score} = 0.50(74.45) + 0.30(78.39) + 0.20(47.80) = \mathbf{70.3}$

### Benchmark 2: SFO $\rightarrow$ LAX Best Time to Fly (July)
- **Corridor:** San Francisco (`SFO`) to Los Angeles (`LAX`), Month 7 (July)
- **Historically Best Period:** **`AFTERNOON`**
- **Metrics:** 14 flights, 35.71% delay rate, 11.00 min avg delay, 0% cancellations (**Disruption Score: 21.16**).
- **Comparison:** Outperformed Evening (Disruption Score: 26.41) and Morning (Disruption Score: 26.78).

### Benchmark 3: UA ORD $\rightarrow$ LGA Historical Risk (June, Evening)
- **Flight Profile:** United Airlines (`UA`), `ORD` to `LGA`, Month 6 (June), Scheduled Departure `18:30` (Evening)
- **Assessed Risk Level:** **`HIGH`** (37 flights, 24.32% delay rate, 23.23 min avg delay, 16.22% cancel rate).
- **Better Historical Alternative:** `UA MORNING` (68 flights, 17.65% delay rate, 4.65 min avg delay).

---

## 10. Pipeline Validation & Audit

Record reconciliation was validated using `scripts/validate_pipeline.py`:

| Pipeline Stage | System / Technology | Verified Record Count | Audit Status |
| :--- | :--- | :--- | :--- |
| **Stage 1: Raw Dataset** | CSV (`flights_raw_200k.csv`) | **216,000** | VERIFIED |
| **Stage 2: RDBMS Staging** | MySQL 8.0 (`airflight.flights`) | **216,000** | VERIFIED |
| **Stage 3: Distributed Ingestion** | Apache Sqoop (HDFS `/raw`) | **216,000** | VERIFIED |
| **Stage 4: Batch Computing** | Java MapReduce (HDFS `/mr_output`) | **216,000 in / 14 out** | VERIFIED |
| **Stage 5: ETL Cleansing** | Apache Pig (HDFS `/pig_output`) | **216,000** | VERIFIED |
| **Stage 6: Data Warehouse** | Apache Hive (`airflight_flights`) | **216,000** | VERIFIED |
| **Stage 7: NoSQL Serving** | MongoDB (`airflight.flights`) | **216,000** | VERIFIED |
| **Stage 8: Analytics Engine** | Python Analytics Backend | **Deterministic Results** | VERIFIED |

---

## 11. Limitations & Assumptions

1. **Historical Grounding:** Analysis is grounded in the 2015 BTS dataset to maintain temporal consistency across all 12 calendar months.
2. **Descriptive, Not Predictive:** AirFlight calculates historical probabilities and delay distributions. It does not predict real-time tactical ATC ground stops or unexpected aircraft mechanical issues.
3. **Statistical Guardrails:** Airport corridors with fewer than 10 flights return `INSUFFICIENT_HISTORICAL_DATA` to prevent misleading extrapolations.

---

## 12. Future Improvements

1. **Multi-Year Trend Analysis:** Ingesting additional BTS yearly tranches (e.g. 2016–2024) to evaluate longitudinal carrier improvement.
2. **Aircraft Sub-Type Stratification:** Segmenting delays by aircraft airframe (e.g. Boeing 737 vs. Airbus A320 vs. regional jets).
3. **Real-Time METAR Ingestion:** Integrating live airport meteorological terminal reports alongside historical benchmarks.

---

## 13. Comprehensive Project Report

For the complete technical report including full architectural specifications, data dictionaries, Hive query details, MapReduce classes, and memory optimization profiles, see:

👉 **[PROJECT_REPORT.md](PROJECT_REPORT.md)**
