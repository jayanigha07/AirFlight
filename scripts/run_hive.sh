#!/bin/bash
set -e

echo "=========================================================="
echo "AIRFLIGHT: RUNNING APACHE HIVE COMPREHENSIVE ANALYTICS"
echo "=========================================================="

source /etc/profile.d/00-bigdata.sh

echo ">>> 1. Cleaning previous result directories in HDFS..."
hdfs dfs -rm -r -f /airflight/results/*

echo ">>> 2. Executing Hive HQL script..."
hive -f /mnt/c/Users/DELL/Desktop/airflight/hive/airflight_analysis.hql

echo ">>> 3. Verifying HDFS results in /airflight/results..."
hdfs dfs -ls /airflight/results

echo "=========================================================="
echo "HIVE RESULT SUMMARY: AIRLINE ANALYSIS"
echo "=========================================================="
hdfs dfs -cat /airflight/results/airline_analysis/* | head -n 14

echo "=========================================================="
echo "HIVE RESULT SUMMARY: SEASONAL ANALYSIS"
echo "=========================================================="
hdfs dfs -cat /airflight/results/seasonal_analysis/*

echo "=========================================================="
echo "HIVE RESULT SUMMARY: TIME-OF-DAY RISK"
echo "=========================================================="
hdfs dfs -cat /airflight/results/time_of_day_analysis/*

echo "=========================================================="
echo "HIVE RESULT SUMMARY: DELAY CATEGORY DISTRIBUTION"
echo "=========================================================="
hdfs dfs -cat /airflight/results/delay_category_analysis/*

echo "SUCCESS: Apache Hive analytics executed and verified!"
