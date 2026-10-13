-- AirFlight MySQL Database Schema
-- Designed from US DOT / BTS 2015 Airline On-Time Performance Dataset

CREATE DATABASE IF NOT EXISTS airflight;
USE airflight;

-- 1. Airlines Reference Table
DROP TABLE IF EXISTS airlines;
CREATE TABLE airlines (
    iata_code VARCHAR(10) PRIMARY KEY,
    airline_name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Airports Reference Table
DROP TABLE IF EXISTS airports;
CREATE TABLE airports (
    iata_code VARCHAR(10) PRIMARY KEY,
    airport_name VARCHAR(150),
    city VARCHAR(100),
    state VARCHAR(10),
    country VARCHAR(10),
    latitude DOUBLE,
    longitude DOUBLE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Cancellation Codes Reference Table
DROP TABLE IF EXISTS cancellation_codes;
CREATE TABLE cancellation_codes (
    cancellation_reason VARCHAR(5) PRIMARY KEY,
    cancellation_description VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Flights Operational Table
DROP TABLE IF EXISTS flights;
CREATE TABLE flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL,
    month INT NOT NULL,
    day INT NOT NULL,
    day_of_week INT NOT NULL,
    airline VARCHAR(10) NOT NULL,
    flight_number INT NOT NULL,
    tail_number VARCHAR(15),
    origin_airport VARCHAR(10) NOT NULL,
    destination_airport VARCHAR(10) NOT NULL,
    scheduled_departure INT NOT NULL,
    departure_time INT,
    departure_delay DOUBLE,
    taxi_out DOUBLE,
    wheels_off INT,
    scheduled_time DOUBLE,
    elapsed_time DOUBLE,
    air_time DOUBLE,
    distance INT NOT NULL,
    wheels_on INT,
    taxi_in DOUBLE,
    scheduled_arrival INT NOT NULL,
    arrival_time INT,
    arrival_delay DOUBLE,
    diverted INT NOT NULL DEFAULT 0,
    cancelled INT NOT NULL DEFAULT 0,
    cancellation_reason VARCHAR(10),
    air_system_delay DOUBLE,
    security_delay DOUBLE,
    airline_delay DOUBLE,
    late_aircraft_delay DOUBLE,
    weather_delay DOUBLE,
    INDEX idx_airline (airline),
    INDEX idx_origin (origin_airport),
    INDEX idx_dest (destination_airport),
    INDEX idx_route (origin_airport, destination_airport),
    INDEX idx_month (month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
