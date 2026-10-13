#!/bin/bash
set -e

echo "=== Running Hive Verification Query ==="
/opt/hive/bin/hive << 'EOF'
CREATE DATABASE IF NOT EXISTS airflight_test;
USE airflight_test;

DROP TABLE IF EXISTS sqoop_airlines;
CREATE EXTERNAL TABLE sqoop_airlines (
    carrier_code STRING,
    carrier_name STRING,
    country STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '/test_sqoop_import';

SELECT COUNT(*) AS total_records FROM sqoop_airlines;
SELECT * FROM sqoop_airlines;
EOF

echo "Hive verification successful!"
