#!/bin/bash
set -e

echo "=== 1. Preparing MySQL Table ==="
mysql -u airflight -pairflight123 << 'EOF'
USE airflight;
DROP TABLE IF EXISTS sqoop_test_airlines;
CREATE TABLE sqoop_test_airlines (
    carrier_code VARCHAR(10) PRIMARY KEY,
    carrier_name VARCHAR(100),
    country VARCHAR(50)
);
INSERT INTO sqoop_test_airlines VALUES
('AA', 'American Airlines', 'USA'),
('DL', 'Delta Air Lines', 'USA'),
('UA', 'United Airlines', 'USA');
SELECT * FROM sqoop_test_airlines;
EOF

echo "=== 2. Cleaning HDFS target directory ==="
hdfs dfs -rm -r -f /test_sqoop_import

echo "=== 3. Executing Sqoop Import ==="
/opt/sqoop/bin/sqoop import \
  --connect "jdbc:mysql://localhost:3306/airflight?useSSL=false" \
  --username airflight \
  --password airflight123 \
  --table sqoop_test_airlines \
  --target-dir /test_sqoop_import \
  -m 1

echo "=== 4. Verifying Imported HDFS Data ==="
hdfs dfs -cat /test_sqoop_import/part-m-00000
echo "Sqoop verification successful!"
