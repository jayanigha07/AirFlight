# AirFlight: System Architecture & Technical Design

**AirFlight** implements a classical Lambda-inspired Batch & Serving architecture tailored for high-volume historical flight telemetry. The platform is architected to operate efficiently on single-node distributed infrastructure within constrained memory environments (~7.7 GB RAM) while maintaining full architectural fidelity with production Big Data environments.

---

## 1. Architectural Blueprint

The architecture is partitioned into five distinct layers:
1. **Ingestion & Staging Layer** (MySQL 8.0 & Apache Sqoop 1.4.7)
2. **Distributed Storage Lake** (Hadoop 3.3.6 HDFS)
3. **Distributed Batch Processing Layer** (Java MapReduce on YARN & Apache Pig 0.17.0)
4. **Analytical Data Warehouse** (Apache Hive 3.1.3 on YARN)
5. **NoSQL Operational Serving & Analytics Layer** (MongoDB 7.0 & Python 3 Analytics Engine)

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
|    (Storage Layer)    |     | - /airflight/raw/flights (Raw CSV files)                             |
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
|                       |     | - Null handling, invalid record filtering                            |
|                       |     | - Derived features: season, time_of_day, delay_category              |
|                       |     | - Output: /airflight/pig_output (Enriched TSV)                       |
+-----------------------+     +----------------------------------------------------------------------+
                                                          |
                                      +-------------------+-------------------+
                                      |                                       |
                                      v                                       v
+-----------------------+     +-------------------------------+   +----------------------------------+
| 4. WAREHOUSING &      | --> | Apache Hive 3.1.3 Warehouse   |   | MongoDB 7.0 NoSQL Serving Layer  |
|    OPERATIONAL SERVING|     | - External Tables over HDFS   |   | - 216,000 flight documents       |
|                       |     | - 9 Verified Multi-dim SQL Qs |   | - Compound Indexes for latency   |
|                       |     | - Output: /airflight/results/ |   | - Real-time Aggregation Pipeline |
+-----------------------+     +-------------------------------+   +----------------------------------+
                                                                              |
                                                                              v
+-----------------------+                                         +----------------------------------+
| 5. DESCRIPTIVE        | <---------------------------------------| AirFlight Analytics Engine       |
|    ANALYTICS ENGINE   |                                         | 1. Best Time to Fly              |
|    (Backend CLI / API)|                                         | 2. Route Reliability Score 0-100 |
+-----------------------+                                         | 3. Historical Delay Risk         |
                                                                  +----------------------------------+
```

---

## 2. Layer-by-Layer Technical Specifications

### Layer 1: Ingestion & Relational Staging
- **MySQL 8.0:** Acts as the entry gate for structured transactional flight schedules.
- **Table Partitioning & Indexing:** Tables are indexed by `airline`, `origin_airport`, `destination_airport`, and `month`.
- **Apache Sqoop 1.4.7:** Bridges the relational boundary into HDFS using JDBC streaming:
  - Generates Java ORM bindings (`flights.java`).
  - Employs bounded integer splitting (`SELECT MIN(id), MAX(id) FROM flights`) into 2 equal splits of 107,999 records each.
  - Generates standard comma-delimited HDFS raw files with explicit `\N` null strings.

### Layer 2: Distributed Storage (HDFS Lake)
- **Directory Hierarchy:**
  - `/airflight/raw`: Raw Sqoop imports (`flights`, `airlines`, `airports`, `cancellation_codes`).
  - `/airflight/cleaned`: Reserved staging space.
  - `/airflight/pig_output`: Cleaned, typed, and enriched TSV output from Apache Pig.
  - `/airflight/mapreduce_output`: Native MapReduce job outputs.
  - `/airflight/hive`: Hive warehouse managed directory.
  - `/airflight/results`: Final persisted Hive analytical tables.

### Layer 3: Batch Computing & Distributed Transformations
- **Native Java MapReduce:**
  - Implements `com.airflight.mr.AirlineDelayStats` using Hadoop MR v2.
  - **Mapper:** Reads Sqoop raw tokens, performs line verification, parses arrival and departure delays, and emits `(airline, AirlineStatsWritable)`.
  - **Combiner:** Local mini-reducer running in mapper JVM reducing network shuffle traffic from 216,000 records down to 27 spilled records.
  - **Reducer:** Sums metrics per airline and computes final arrival delay averages, departure delay averages, delay percentages, and cancellation rates.
- **Apache Pig 0.17.0:**
  - Operates in MapReduce mode on YARN.
  - Filters malformed calendar dates or missing carrier keys.
  - Projects high-value operational features:
    - `season`: Mapped from calendar month.
    - `time_of_day`: Mapped from scheduled departure in military format (Morning, Afternoon, Evening, Night).
    - `delay_category`: Categorizes delays according to FAA thresholds (On-Time, Short, Medium, Severe, Cancelled, Diverted).

### Layer 4: Warehousing & NoSQL Serving
- **Apache Hive 3.1.3:**
  - Operates directly over `/airflight/pig_output` via external table metadata schemas.
  - Joins dimensional entities with raw reference tables (`airflight_airlines`, `airports`, `cancellation_codes`).
  - Executes comprehensive aggregations and overwrites report datasets directly into `/airflight/results/`.
- **MongoDB 7.0:**
  - Operates as the high-speed operational serving store (NoSQL).
  - Holds 216,000 documents with compound indexing:
    - `{ origin_airport: 1, destination_airport: 1, month: 1 }`
    - `{ airline: 1, origin_airport: 1, destination_airport: 1 }`
    - `{ time_of_day: 1 }`
    - `{ delay_category: 1 }`
  - Facilitates sub-10ms operational queries for downstream analytics.

### Layer 5: AirFlight Analytics Engine
- **Module:** Python 3 (`analytics/airflight_engine.py`).
- **Design Pattern:** Decoupled, stateless analytical service.
- **Features:**
  1. *Best Time to Fly:* Ranks departure periods using a weighted Disruption Score.
  2. *Route Reliability Score:* Deterministic 0–100 score with strict mathematical bounds.
  3. *Historical Delay Risk:* Deterministic LOW/MEDIUM/HIGH classification with pattern discovery and alternative route suggestions.
  4. *Guardrail Protection:* Implements strict sample size thresholds (`MIN_SAMPLE_SIZE = 10`), falling back to `INSUFFICIENT_HISTORICAL_DATA` when statistical confidence is low.

---

## 3. Memory & Resource Optimization for WSL2 (~7.7 GB RAM)

Running a full Big Data ecosystem concurrently (NameNode, DataNode, SecondaryNameNode, ResourceManager, NodeManager, JobHistoryServer, MySQL, Hive Metastore, and MongoDB) requires conservative memory governance:

```
Total Physical Memory: ~7,881 MB (7.7 GB)
Swap Memory:           2,048 MB (2.0 GB)
Current Idle Footprint: ~3,004 MB (38% utilization)
Available Headroom:    ~4,681 MB (62% free for YARN & OS buffers)
```

### Component Memory Allocations
| Component | Heap Max (`-Xmx`) | Process Memory Budget |
| :--- | :--- | :--- |
| Hadoop NameNode | `-Xmx512m` | ~600 MB |
| Hadoop DataNode | `-Xmx512m` | ~550 MB |
| YARN ResourceManager | `-Xmx512m` | ~600 MB |
| YARN NodeManager | `-Xmx512m` | ~550 MB |
| YARN Container Max Allocation | N/A | `4,096 MB` total pool |
| Map Container Allocation | `-Xmx384m` | `512 MB` container |
| Reduce Container Allocation | `-Xmx384m` | `512 MB` container |
| MySQL 8.0 Daemon | `innodb_buffer_pool=256M` | ~450 MB |
| MongoDB 7.0 Daemon | `wiredTiger=512M` | ~650 MB |
| Hive CLI / Metastore | `-Xmx256m` | ~400 MB |

This configuration ensures zero memory pressure, eliminates Linux OOM-killer interventions, and avoids swap thrashing during intensive MapReduce shuffle phases.
