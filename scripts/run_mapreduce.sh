#!/bin/bash
set -e

echo "=========================================================="
echo "AIRFLIGHT: COMPILING AND RUNNING JAVA MAPREDUCE ON YARN"
echo "=========================================================="

source /etc/profile.d/00-bigdata.sh

WORK_DIR="/mnt/c/Users/DELL/Desktop/airflight/mapreduce"
SRC_DIR="$WORK_DIR/src/com/airflight/mr"
BIN_DIR="$WORK_DIR/bin"
JAR_FILE="$WORK_DIR/airflight-mapreduce.jar"

mkdir -p "$BIN_DIR"
rm -rf "$BIN_DIR"/*

echo ">>> 1. Compiling Java MapReduce classes..."
javac -cp "$($HADOOP_HOME/bin/hadoop classpath)" \
      -d "$BIN_DIR" \
      "$SRC_DIR/AirlineStatsWritable.java" \
      "$SRC_DIR/AirlineDelayStats.java"

echo ">>> 2. Packaging into JAR ($JAR_FILE)..."
jar -cvf "$JAR_FILE" -C "$BIN_DIR" .

echo ">>> 3. Ensuring HDFS input exists and cleaning previous output..."
hdfs dfs -ls /airflight/raw/flights
hdfs dfs -rm -r -f /airflight/mapreduce_output/airline_delays

echo ">>> 4. Submitting MapReduce Job to YARN..."
hadoop jar "$JAR_FILE" com.airflight.mr.AirlineDelayStats \
       /airflight/raw/flights \
       /airflight/mapreduce_output/airline_delays

echo ">>> 5. MapReduce Job completed! Verifying HDFS outputs..."
hdfs dfs -ls /airflight/mapreduce_output/airline_delays

echo "=========================================================="
echo "MAPREDUCE EXECUTION RESULTS (/airflight/mapreduce_output/airline_delays):"
echo "=========================================================="
hdfs dfs -cat /airflight/mapreduce_output/airline_delays/part-r-00000

echo "SUCCESS: MapReduce executed, verified and outputs captured!"
