#!/bin/bash
set -e

echo "=========================================================="
echo "AIRFLIGHT: SQOOP IMPORT (MySQL -> HDFS /airflight/raw)"
echo "=========================================================="

# Ensure environment
source /etc/profile.d/00-bigdata.sh

echo ">>> Cleaning existing raw targets in HDFS..."
hdfs dfs -rm -r -f /airflight/raw/flights
hdfs dfs -rm -r -f /airflight/raw/airlines
hdfs dfs -rm -r -f /airflight/raw/airports
hdfs dfs -rm -r -f /airflight/raw/cancellation_codes

echo ">>> 1. Importing reference table: airlines..."
/opt/sqoop/bin/sqoop import \
  --connect "jdbc:mysql://localhost:3306/airflight?useSSL=false" \
  --username airflight \
  --password airflight123 \
  --table airlines \
  --target-dir /airflight/raw/airlines \
  --fields-terminated-by ',' \
  -m 1

echo ">>> 2. Importing reference table: airports..."
/opt/sqoop/bin/sqoop import \
  --connect "jdbc:mysql://localhost:3306/airflight?useSSL=false" \
  --username airflight \
  --password airflight123 \
  --table airports \
  --target-dir /airflight/raw/airports \
  --fields-terminated-by ',' \
  -m 1

echo ">>> 3. Importing reference table: cancellation_codes..."
/opt/sqoop/bin/sqoop import \
  --connect "jdbc:mysql://localhost:3306/airflight?useSSL=false" \
  --username airflight \
  --password airflight123 \
  --table cancellation_codes \
  --target-dir /airflight/raw/cancellation_codes \
  --fields-terminated-by ',' \
  -m 1

echo ">>> 4. Importing main operational dataset: flights (216,000 records)..."
/opt/sqoop/bin/sqoop import \
  --connect "jdbc:mysql://localhost:3306/airflight?useSSL=false" \
  --username airflight \
  --password airflight123 \
  --table flights \
  --target-dir /airflight/raw/flights \
  --fields-terminated-by ',' \
  --null-string '\\N' \
  --null-non-string '\\N' \
  --split-by id \
  -m 2

echo ">>> 5. Verifying HDFS files and record counts..."
hdfs dfs -ls /airflight/raw/flights
AIRLINES_COUNT=$(hdfs dfs -cat /airflight/raw/airlines/part-* | wc -l)
AIRPORTS_COUNT=$(hdfs dfs -cat /airflight/raw/airports/part-* | wc -l)
CANC_COUNT=$(hdfs dfs -cat /airflight/raw/cancellation_codes/part-* | wc -l)
FLIGHTS_COUNT=$(hdfs dfs -cat /airflight/raw/flights/part-* | wc -l)

echo "=========================================================="
echo "SQOOP IMPORT SUMMARY:"
echo "Airlines Count (HDFS):           $AIRLINES_COUNT (Expected: 14)"
echo "Airports Count (HDFS):           $AIRPORTS_COUNT (Expected: 322)"
echo "Cancellation Codes Count (HDFS): $CANC_COUNT (Expected: 4)"
echo "Flights Count (HDFS):            $FLIGHTS_COUNT (Expected: 216000)"
echo "=========================================================="

if [ "$FLIGHTS_COUNT" -eq 216000 ]; then
    echo "SUCCESS: Sqoop import verified with exactly 216,000 records in HDFS!"
else
    echo "ERROR: Flights count mismatch! Got $FLIGHTS_COUNT, expected 216000."
    exit 1
fi
