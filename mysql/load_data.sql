-- AirFlight MySQL Data Ingestion Script
-- Loads real public BTS flight datasets into MySQL

USE airflight;

SET GLOBAL local_infile = 1;

-- 1. Load Airlines
LOAD DATA LOCAL INFILE '/mnt/c/Users/DELL/Desktop/airflight/data/airlines.csv'
INTO TABLE airlines
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(iata_code, airline_name);

-- 2. Load Airports
LOAD DATA LOCAL INFILE '/mnt/c/Users/DELL/Desktop/airflight/data/airports.csv'
INTO TABLE airports
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(iata_code, airport_name, city, state, country, @lat, @lon)
SET latitude = NULLIF(@lat, ''),
    longitude = NULLIF(@lon, '');

-- 3. Load Cancellation Codes
LOAD DATA LOCAL INFILE '/mnt/c/Users/DELL/Desktop/airflight/data/cancellation_codes.csv'
INTO TABLE cancellation_codes
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(cancellation_reason, cancellation_description);

-- 4. Load 216,000 Flights
LOAD DATA LOCAL INFILE '/mnt/c/Users/DELL/Desktop/airflight/data/flights_raw_200k.csv'
INTO TABLE flights
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(
    year, month, day, day_of_week, airline, flight_number,
    @tail_number, origin_airport, destination_airport, scheduled_departure,
    @dep_time, @dep_delay, @taxi_out, @wheels_off, @sched_time,
    @elapsed_time, @air_time, distance, @wheels_on, @taxi_in,
    scheduled_arrival, @arr_time, @arr_delay, diverted, cancelled,
    @canc_reason, @air_sys_delay, @sec_delay, @air_delay, @late_air_delay, @wx_delay
)
SET
    tail_number = NULLIF(@tail_number, ''),
    departure_time = NULLIF(@dep_time, ''),
    departure_delay = NULLIF(@dep_delay, ''),
    taxi_out = NULLIF(@taxi_out, ''),
    wheels_off = NULLIF(@wheels_off, ''),
    scheduled_time = NULLIF(@sched_time, ''),
    elapsed_time = NULLIF(@elapsed_time, ''),
    air_time = NULLIF(@air_time, ''),
    wheels_on = NULLIF(@wheels_on, ''),
    taxi_in = NULLIF(@taxi_in, ''),
    arrival_time = NULLIF(@arr_time, ''),
    arrival_delay = NULLIF(@arr_delay, ''),
    cancellation_reason = NULLIF(@canc_reason, ''),
    air_system_delay = NULLIF(@air_sys_delay, ''),
    security_delay = NULLIF(@sec_delay, ''),
    airline_delay = NULLIF(@air_delay, ''),
    late_aircraft_delay = NULLIF(@late_air_delay, ''),
    weather_delay = NULLIF(@wx_delay, '');

-- Verification summary
SELECT 'airlines' AS table_name, COUNT(*) AS row_count FROM airlines
UNION ALL
SELECT 'airports', COUNT(*) FROM airports
UNION ALL
SELECT 'cancellation_codes', COUNT(*) FROM cancellation_codes
UNION ALL
SELECT 'flights', COUNT(*) FROM flights;
