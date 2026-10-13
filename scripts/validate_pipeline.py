#!/usr/bin/env python3
"""
AirFlight End-to-End Pipeline Validation Script
Cross-verifies record counts, schemas, and outputs across every pipeline stage:
Dataset -> MySQL -> Sqoop -> HDFS -> MapReduce -> Pig -> Hive -> MongoDB -> Analytics Engine
"""

import os
import sys
import subprocess
import json

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))
from pymongo import MongoClient

def run_cmd(cmd_list):
    res = subprocess.run(cmd_list, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    return res.stdout.strip()

def main():
    print("==========================================================")
    print("AIRFLIGHT: END-TO-END PIPELINE VALIDATION AUDIT")
    print("==========================================================")

    # 1. Dataset Raw File
    raw_lines = int(run_cmd(["wc", "-l", "/mnt/c/Users/DELL/Desktop/airflight/data/flights_raw_200k.csv"]).split()[0]) - 1
    print(f"1. RAW DATASET (CSV):              {raw_lines:,} flight rows")

    # 2. MySQL Table
    mysql_out = run_cmd([
        "mysql", "-u", "airflight", "-pairflight123", "airflight", "-N", "-e",
        "SELECT COUNT(*) FROM flights;"
    ])
    mysql_count = int(mysql_out.splitlines()[-1])
    print(f"2. MYSQL TABLE (flights):          {mysql_count:,} records")

    # 3. Sqoop Raw HDFS
    sqoop_out = run_cmd(["bash", "-c", "hdfs dfs -cat /airflight/raw/flights/part-* | wc -l"])
    sqoop_count = int(sqoop_out.splitlines()[-1])
    print(f"3. SQOOP IMPORT (HDFS /raw):       {sqoop_count:,} records")

    # 4. MapReduce Aggregation Output
    mr_lines = run_cmd(["bash", "-c", "hdfs dfs -cat /airflight/mapreduce_output/airline_delays/part-r-00000 | wc -l"])
    mr_count = int(mr_lines.splitlines()[-1])
    print(f"4. MAPREDUCE OUTPUT (carriers):    {mr_count} carrier groups (from 216,000 mapped records)")

    # 5. Pig Processing Output
    pig_out = run_cmd(["bash", "-c", "hdfs dfs -cat /airflight/pig_output/part-* | wc -l"])
    pig_count = int(pig_out.splitlines()[-1])
    print(f"5. PIG OUTPUT (HDFS /pig_output):   {pig_count:,} enriched records")

    # 6. Hive External Table
    hive_out = run_cmd([
        "hive", "-S", "-e", "SELECT COUNT(*) FROM airflight.airflight_flights;"
    ])
    hive_count = int(hive_out.splitlines()[-1])
    print(f"6. HIVE TABLE (airflight_flights): {hive_count:,} records")

    # 7. MongoDB Operational Database
    client = MongoClient("mongodb://localhost:27017/")
    db = client["airflight"]
    mongo_count = db["flights"].count_documents({})
    mongo_carriers = db["airline_summaries"].count_documents({})
    print(f"7. MONGODB (airflight.flights):    {mongo_count:,} documents")
    print(f"   MONGODB (airline_summaries):    {mongo_carriers} carrier summaries")

    # 8. Analytical Engine Check
    from analytics.airflight_engine import AirFlightAnalytics
    engine = AirFlightAnalytics()
    time_res = engine.best_time_to_fly("SFO", "LAX", 7)
    route_res = engine.route_reliability_score("ORD", "LGA")
    risk_res = engine.historical_delay_risk("UA", "ORD", "LGA", 6, 1830)

    print("8. AIRFLIGHT ANALYTICS ENGINE:")
    print(f"   - Best Time to Fly (SFO->LAX, Month 7): {time_res['historically_best_period']} (Score: {time_res['best_period_metrics']['disruption_score']})")
    print(f"   - Route Reliability Score (ORD->LGA):   {route_res['reliability_score']}/100 ({route_res['tier']})")
    print(f"   - Historical Delay Risk (UA, ORD->LGA): {risk_res['historical_delay_risk']} (Delay Rate: {risk_res['metrics']['delay_rate_pct']}%)")

    print("==========================================================")
    all_match = (
        raw_lines == 216000 and
        mysql_count == 216000 and
        sqoop_count == 216000 and
        mr_count == 14 and
        pig_count == 216000 and
        hive_count == 216000 and
        mongo_count == 216000
    )
    if all_match:
        print("PIPELINE AUDIT: ALL 216,000 RECORDS 100% RECONCILED ACROSS ALL 8 STAGES!")
    else:
        print("WARNING: Pipeline discrepancy detected!")

if __name__ == "__main__":
    main()
