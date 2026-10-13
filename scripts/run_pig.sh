#!/bin/bash
set -e

echo "=========================================================="
echo "AIRFLIGHT: RUNNING APACHE PIG CLEANING ON MAPREDUCE"
echo "=========================================================="

source /etc/profile.d/00-bigdata.sh

echo ">>> 1. Cleaning previous Pig output in HDFS..."
hdfs dfs -rm -r -f /airflight/pig_output

echo ">>> 2. Executing Pig script in MapReduce execution mode..."
pig -x mapreduce /mnt/c/Users/DELL/Desktop/airflight/pig/clean_flights.pig

echo ">>> 3. Verifying Pig output in HDFS..."
hdfs dfs -ls /airflight/pig_output

PIG_COUNT=$(hdfs dfs -cat /airflight/pig_output/part-* | wc -l)
echo "=========================================================="
echo "PIG OUTPUT RECORD COUNT: $PIG_COUNT (Expected: 216000)"
echo "=========================================================="

echo ">>> Sample Pig Enriched Records:"
hdfs dfs -cat /airflight/pig_output/part-* | head -n 5

if [ "$PIG_COUNT" -gt 0 ]; then
    echo "SUCCESS: Pig processing completed and verified!"
else
    echo "ERROR: Pig produced 0 records!"
    exit 1
fi
