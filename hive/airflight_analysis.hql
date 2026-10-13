-- AirFlight Comprehensive Apache Hive Analysis
-- Executes verified historical analytics on processed HDFS flight data

CREATE DATABASE IF NOT EXISTS airflight;
USE airflight;

SET hive.execution.engine=mr;
SET mapreduce.map.memory.mb=512;
SET mapreduce.reduce.memory.mb=512;
SET mapreduce.map.java.opts=-Xmx384m;
SET mapreduce.reduce.java.opts=-Xmx384m;

-- ==============================================================
-- 1. HIVE TABLE DEFINITIONS
-- ==============================================================

-- Main processed flights table over Pig output
DROP TABLE IF EXISTS airflight_flights;
CREATE EXTERNAL TABLE airflight_flights (
    id INT,
    year INT,
    month INT,
    day INT,
    day_of_week INT,
    season STRING,
    airline STRING,
    flight_number INT,
    origin_airport STRING,
    destination_airport STRING,
    scheduled_departure INT,
    time_of_day STRING,
    departure_delay DOUBLE,
    scheduled_arrival INT,
    arrival_delay DOUBLE,
    delay_category STRING,
    distance INT,
    cancelled INT,
    diverted INT,
    cancellation_reason STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
LOCATION '/airflight/pig_output';

-- Airlines reference table over Sqoop raw import
DROP TABLE IF EXISTS airflight_airlines;
CREATE EXTERNAL TABLE airflight_airlines (
    iata_code STRING,
    airline_name STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LOCATION '/airflight/raw/airlines';

-- Airports reference table over Sqoop raw import
DROP TABLE IF EXISTS airflight_airports;
CREATE EXTERNAL TABLE airflight_airports (
    iata_code STRING,
    airport_name STRING,
    city STRING,
    state STRING,
    country STRING,
    latitude DOUBLE,
    longitude DOUBLE
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LOCATION '/airflight/raw/airports';

-- Cancellation codes reference table over Sqoop raw import
DROP TABLE IF EXISTS airflight_cancellation_codes;
CREATE EXTERNAL TABLE airflight_cancellation_codes (
    cancellation_reason STRING,
    cancellation_description STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LOCATION '/airflight/raw/cancellation_codes';

-- ==============================================================
-- 2. VERIFIED ANALYTICAL QUERIES STORED IN /airflight/results
-- ==============================================================

-- Q1: Airline Performance Analysis
INSERT OVERWRITE DIRECTORY '/airflight/results/airline_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    f.airline,
    COALESCE(a.airline_name, f.airline) AS airline_name,
    COUNT(*) AS total_flights,
    SUM(CASE WHEN f.arrival_delay > 15.0 THEN 1 ELSE 0 END) AS delayed_flights,
    ROUND(SUM(CASE WHEN f.arrival_delay > 15.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS delay_rate_pct,
    ROUND(AVG(f.arrival_delay), 2) AS avg_arr_delay,
    ROUND(AVG(f.departure_delay), 2) AS avg_dep_delay,
    SUM(f.cancelled) AS cancelled_flights,
    ROUND(SUM(f.cancelled) * 100.0 / COUNT(*), 2) AS cancel_rate_pct
FROM airflight_flights f
LEFT JOIN airflight_airlines a ON f.airline = a.iata_code
GROUP BY f.airline, a.airline_name;

-- Q2: Airport Delay Ranking (Top 30 Origins)
INSERT OVERWRITE DIRECTORY '/airflight/results/airport_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    f.origin_airport,
    COALESCE(p.airport_name, f.origin_airport) AS airport_name,
    COALESCE(p.city, 'UNKNOWN') AS city,
    COALESCE(p.state, 'UNKNOWN') AS state,
    COUNT(*) AS total_departures,
    SUM(CASE WHEN f.departure_delay > 15.0 THEN 1 ELSE 0 END) AS delayed_departures,
    ROUND(SUM(CASE WHEN f.departure_delay > 15.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS dep_delay_rate_pct,
    ROUND(AVG(f.departure_delay), 2) AS avg_dep_delay
FROM airflight_flights f
LEFT JOIN airflight_airports p ON f.origin_airport = p.iata_code
GROUP BY f.origin_airport, p.airport_name, p.city, p.state
HAVING COUNT(*) >= 50
ORDER BY total_departures DESC
LIMIT 50;

-- Q3: Route Reliability Analysis (Top Routes)
INSERT OVERWRITE DIRECTORY '/airflight/results/route_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    f.origin_airport,
    f.destination_airport,
    CONCAT(f.origin_airport, '-', f.destination_airport) AS route,
    COUNT(*) AS total_flights,
    SUM(CASE WHEN f.arrival_delay > 15.0 THEN 1 ELSE 0 END) AS delayed_flights,
    ROUND(SUM(CASE WHEN f.arrival_delay > 15.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS delay_rate_pct,
    ROUND(AVG(f.arrival_delay), 2) AS avg_arr_delay,
    SUM(f.cancelled) AS cancelled_flights,
    ROUND(SUM(f.cancelled) * 100.0 / COUNT(*), 2) AS cancel_rate_pct
FROM airflight_flights f
GROUP BY f.origin_airport, f.destination_airport
HAVING COUNT(*) >= 20
ORDER BY total_flights DESC
LIMIT 100;

-- Q4: Monthly Flight Performance Trends
INSERT OVERWRITE DIRECTORY '/airflight/results/monthly_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    month,
    COUNT(*) AS total_flights,
    SUM(CASE WHEN arrival_delay > 15.0 THEN 1 ELSE 0 END) AS delayed_flights,
    ROUND(SUM(CASE WHEN arrival_delay > 15.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS delay_rate_pct,
    ROUND(AVG(arrival_delay), 2) AS avg_arr_delay,
    SUM(cancelled) AS cancelled_flights,
    ROUND(SUM(cancelled) * 100.0 / COUNT(*), 2) AS cancel_rate_pct
FROM airflight_flights
GROUP BY month
ORDER BY month ASC;

-- Q5: Seasonal Flight Performance Analysis
INSERT OVERWRITE DIRECTORY '/airflight/results/seasonal_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    season,
    COUNT(*) AS total_flights,
    SUM(CASE WHEN arrival_delay > 15.0 THEN 1 ELSE 0 END) AS delayed_flights,
    ROUND(SUM(CASE WHEN arrival_delay > 15.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS delay_rate_pct,
    ROUND(AVG(arrival_delay), 2) AS avg_arr_delay,
    SUM(cancelled) AS cancelled_flights,
    ROUND(SUM(cancelled) * 100.0 / COUNT(*), 2) AS cancel_rate_pct
FROM airflight_flights
GROUP BY season;

-- Q6: Time-of-Day Risk Analysis
INSERT OVERWRITE DIRECTORY '/airflight/results/time_of_day_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    time_of_day,
    COUNT(*) AS total_flights,
    SUM(CASE WHEN arrival_delay > 15.0 THEN 1 ELSE 0 END) AS delayed_flights,
    ROUND(SUM(CASE WHEN arrival_delay > 15.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS delay_rate_pct,
    ROUND(AVG(arrival_delay), 2) AS avg_arr_delay,
    ROUND(AVG(departure_delay), 2) AS avg_dep_delay,
    ROUND(SUM(cancelled) * 100.0 / COUNT(*), 2) AS cancel_rate_pct
FROM airflight_flights
GROUP BY time_of_day;

-- Q7: Delay Category Distribution
INSERT OVERWRITE DIRECTORY '/airflight/results/delay_category_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    delay_category,
    COUNT(*) AS flight_count,
    ROUND(COUNT(*) * 100.0 / 216000, 2) AS percentage
FROM airflight_flights
GROUP BY delay_category;

-- Q8: Cancellation Reason Breakdown
INSERT OVERWRITE DIRECTORY '/airflight/results/cancellation_analysis'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    f.cancellation_reason,
    COALESCE(c.cancellation_description, 'Unknown / Not Cancelled') AS cancellation_description,
    COUNT(*) AS count_cancelled,
    ROUND(COUNT(*) * 100.0 / 6727, 2) AS pct_of_cancelled
FROM airflight_flights f
LEFT JOIN airflight_cancellation_codes c ON f.cancellation_reason = c.cancellation_reason
WHERE f.cancelled = 1
GROUP BY f.cancellation_reason, c.cancellation_description;

-- Q9: Month + Time-of-Day Matrix
INSERT OVERWRITE DIRECTORY '/airflight/results/month_time_matrix'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
SELECT 
    month,
    time_of_day,
    COUNT(*) AS total_flights,
    ROUND(SUM(CASE WHEN arrival_delay > 15.0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS delay_rate_pct,
    ROUND(AVG(arrival_delay), 2) AS avg_arr_delay
FROM airflight_flights
GROUP BY month, time_of_day
ORDER BY month ASC, time_of_day;
