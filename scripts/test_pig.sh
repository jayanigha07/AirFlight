#!/bin/bash
set -e

cat << 'PIGSCRIPT' > /tmp/test_pig.pig
airlines = LOAD '/test_sqoop_import' USING PigStorage(',') AS (code:chararray, name:chararray, country:chararray);
usa_airlines = FILTER airlines BY country == 'USA';
DUMP usa_airlines;
PIGSCRIPT

echo "=== Running Pig Test Script with MapReduce/HDFS ==="
/opt/pig/bin/pig -x mapreduce /tmp/test_pig.pig
echo "Pig verification successful!"
