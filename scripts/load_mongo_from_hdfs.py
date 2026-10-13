#!/usr/bin/env python3
"""
AirFlight: Load Processed Flight Records from HDFS into MongoDB
Reads Pig-processed data from HDFS (/airflight/pig_output),
ingests 216,000 documents into MongoDB with full type safety and null handling,
creates compound indexes, and executes verification aggregations.
"""

import subprocess
import sys
import time
from pymongo import MongoClient, ASCENDING

def main():
    print("==========================================================")
    print("AIRFLIGHT: MONGODB BULK INGESTION & AGGREGATION")
    print("==========================================================")

    # 1. Connect to MongoDB
    client = MongoClient("mongodb://localhost:27017/")
    db = client["airflight"]
    flights_col = db["flights"]

    print(">>> 1. Resetting 'flights' collection...")
    flights_col.drop()

    # 2. Stream data from HDFS /airflight/pig_output
    print(">>> 2. Streaming records from HDFS /airflight/pig_output...")
    cat_proc = subprocess.Popen(
        ["hdfs", "dfs", "-cat", "/airflight/pig_output/part-*"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )

    batch_size = 10000
    batch = []
    total_inserted = 0
    start_time = time.time()

    for line in cat_proc.stdout:
        line = line.strip()
        if not line:
            continue

        parts = line.split("\t")
        if len(parts) < 20:
            continue

        # TSV field schema from Pig:
        # 0: id, 1: year, 2: month, 3: day, 4: day_of_week, 5: season,
        # 6: airline, 7: flight_number, 8: origin_airport, 9: destination_airport,
        # 10: scheduled_departure, 11: time_of_day, 12: departure_delay,
        # 13: scheduled_arrival, 14: arrival_delay, 15: delay_category,
        # 16: distance, 17: cancelled, 18: diverted, 19: cancellation_reason

        def parse_int(v):
            if v == "" or v == "\\N" or v == "null":
                return None
            try:
                return int(v)
            except ValueError:
                return None

        def parse_float(v):
            if v == "" or v == "\\N" or v == "null":
                return None
            try:
                return float(v)
            except ValueError:
                return None

        doc = {
            "flight_id": parse_int(parts[0]),
            "year": parse_int(parts[1]),
            "month": parse_int(parts[2]),
            "day": parse_int(parts[3]),
            "day_of_week": parse_int(parts[4]),
            "season": parts[5],
            "airline": parts[6],
            "flight_number": parse_int(parts[7]),
            "origin_airport": parts[8],
            "destination_airport": parts[9],
            "scheduled_departure": parse_int(parts[10]),
            "time_of_day": parts[11],
            "departure_delay": parse_float(parts[12]),
            "scheduled_arrival": parse_int(parts[13]),
            "arrival_delay": parse_float(parts[14]),
            "delay_category": parts[15],
            "distance": parse_int(parts[16]),
            "cancelled": parse_int(parts[17]),
            "diverted": parse_int(parts[18]),
            "cancellation_reason": parts[19]
        }

        batch.append(doc)
        if len(batch) >= batch_size:
            flights_col.insert_many(batch)
            total_inserted += len(batch)
            print(f"   Inserted {total_inserted:,} documents...")
            batch = []

    if batch:
        flights_col.insert_many(batch)
        total_inserted += len(batch)

    cat_proc.wait()
    duration = time.time() - start_time
    print(f">>> 3. Bulk ingestion complete! Inserted {total_inserted:,} documents in {duration:.2f}s.")

    # 4. Create Indexes
    print(">>> 4. Creating indexes for low-latency queries...")
    flights_col.create_index([("origin_airport", ASCENDING), ("destination_airport", ASCENDING), ("month", ASCENDING)])
    flights_col.create_index([("airline", ASCENDING), ("origin_airport", ASCENDING), ("destination_airport", ASCENDING)])
    flights_col.create_index([("time_of_day", ASCENDING)])
    flights_col.create_index([("delay_category", ASCENDING)])
    flights_col.create_index([("month", ASCENDING)])
    print("   Indexes created successfully.")

    # 5. Verification Document Count
    count = flights_col.count_documents({})
    print(f"==========================================================")
    print(f"MONGODB COLLECTION COUNT: {count:,} (Expected: 216,000)")
    print(f"==========================================================")

    if count != 216000:
        print(f"ERROR: Expected 216,000 documents, found {count}!")
        sys.exit(1)

    print(">>> 6. Executing MongoDB aggregations via mongosh...")
    subprocess.run(["mongosh", "airflight", "/mnt/c/Users/DELL/Desktop/airflight/mongodb/aggregations.js"], check=True)

    print("SUCCESS: MongoDB ingestion, indexing and aggregations verified!")

if __name__ == "__main__":
    main()
