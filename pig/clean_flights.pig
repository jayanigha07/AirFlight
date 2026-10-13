-- AirFlight Apache Pig Data Cleaning and Feature Engineering
-- Reads Sqoop-imported raw flight records from HDFS
-- Applies null handling, invalid record filtering, and derives:
-- season, time_of_day, and delay_category.

-- Load raw flight records from HDFS
raw_flights = LOAD '/airflight/raw/flights' USING PigStorage(',') AS (
    air_system_delay:chararray,
    air_time:chararray,
    airline:chararray,
    airline_delay:chararray,
    arrival_delay:chararray,
    arrival_time:chararray,
    cancellation_reason:chararray,
    cancelled:int,
    day:int,
    day_of_week:int,
    departure_delay:chararray,
    departure_time:chararray,
    destination_airport:chararray,
    distance:int,
    diverted:int,
    elapsed_time:chararray,
    flight_number:int,
    id:int,
    late_aircraft_delay:chararray,
    month:int,
    origin_airport:chararray,
    scheduled_arrival:int,
    scheduled_departure:int,
    scheduled_time:chararray,
    security_delay:chararray,
    tail_number:chararray,
    taxi_in:chararray,
    taxi_out:chararray,
    weather_delay:chararray,
    wheels_off:chararray,
    wheels_on:chararray,
    year:int
);

-- 1. Filter invalid records (ensure non-null keys, valid calendar ranges)
valid_flights = FILTER raw_flights BY
    airline IS NOT NULL AND airline != '\\N' AND airline != '' AND
    origin_airport IS NOT NULL AND origin_airport != '\\N' AND origin_airport != '' AND
    destination_airport IS NOT NULL AND destination_airport != '\\N' AND destination_airport != '' AND
    month >= 1 AND month <= 12 AND
    day >= 1 AND day <= 31 AND
    year == 2015;

-- 2. Feature Engineering & Transformations:
-- Season:
--   WINTER: Dec (12), Jan (1), Feb (2)
--   SPRING: Mar (3), Apr (4), May (5)
--   SUMMER: Jun (6), Jul (7), Aug (8)
--   AUTUMN: Sep (9), Oct (10), Nov (11)
--
-- Time of Day (Explicit 24h ranges):
--   MORNING:   05:00 - 11:59 (500 <= dep <= 1159)
--   AFTERNOON: 12:00 - 16:59 (1200 <= dep <= 1659)
--   EVENING:   17:00 - 21:59 (1700 <= dep <= 2159)
--   NIGHT:     22:00 - 04:59 (dep >= 2200 OR dep < 500)
--
-- Delay Category:
--   CANCELLED: cancelled == 1
--   DIVERTED:  diverted == 1 AND cancelled == 0 AND arr_delay is null
--   ON_TIME:   arr_delay <= 15.0
--   SHORT:     arr_delay > 15.0 AND arr_delay <= 45.0
--   MEDIUM:    arr_delay > 45.0 AND arr_delay <= 120.0
--   SEVERE:    arr_delay > 120.0
--   UNKNOWN:   fallback

enriched_flights = FOREACH valid_flights GENERATE
    id,
    year,
    month,
    day,
    day_of_week,
    (
        (month == 12 OR month == 1 OR month == 2) ? 'WINTER' :
        ((month >= 3 AND month <= 5) ? 'SPRING' :
        ((month >= 6 AND month <= 8) ? 'SUMMER' : 'AUTUMN'))
    ) AS season:chararray,
    airline,
    flight_number,
    origin_airport,
    destination_airport,
    scheduled_departure,
    (
        (scheduled_departure >= 500 AND scheduled_departure <= 1159) ? 'MORNING' :
        ((scheduled_departure >= 1200 AND scheduled_departure <= 1659) ? 'AFTERNOON' :
        ((scheduled_departure >= 1700 AND scheduled_departure <= 2159) ? 'EVENING' : 'NIGHT'))
    ) AS time_of_day:chararray,
    (departure_delay == '\\N' ? null : (double)departure_delay) AS departure_delay:double,
    scheduled_arrival,
    (arrival_delay == '\\N' ? null : (double)arrival_delay) AS arrival_delay:double,
    (
        (cancelled == 1) ? 'CANCELLED' :
        (arrival_delay == '\\N' ? ((diverted == 1) ? 'DIVERTED' : 'UNKNOWN') :
        (((double)arrival_delay <= 15.0) ? 'ON_TIME' :
        (((double)arrival_delay <= 45.0) ? 'SHORT' :
        (((double)arrival_delay <= 120.0) ? 'MEDIUM' : 'SEVERE'))))
    ) AS delay_category:chararray,
    distance,
    cancelled,
    diverted,
    (cancellation_reason == '\\N' ? 'NONE' : cancellation_reason) AS cancellation_reason:chararray;

-- Store cleaned & categorized dataset into HDFS /airflight/pig_output
STORE enriched_flights INTO '/airflight/pig_output' USING PigStorage('\t');
