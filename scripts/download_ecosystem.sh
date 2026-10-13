#!/bin/bash
set -e

mkdir -p /opt/downloads
cd /opt/downloads

echo "1. Downloading MySQL Connector/J..."
wget -q -c https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar

echo "2. Downloading Sqoop 1.4.7..."
wget -q -c https://archive.apache.org/dist/sqoop/1.4.7/sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz

echo "3. Downloading Pig 0.17.0..."
wget -q -c https://archive.apache.org/dist/pig/pig-0.17.0/pig-0.17.0.tar.gz

echo "4. Downloading Hive 3.1.3..."
wget -q -c https://archive.apache.org/dist/hive/hive-3.1.3/apache-hive-3.1.3-bin.tar.gz

echo "All ecosystem archives downloaded successfully."
ls -lh
