#!/bin/bash
set -e

echo "=== Extracting Apache Pig 0.17.0 ==="
tar -xzf /opt/downloads/pig-0.17.0.tar.gz -C /opt
rm -rf /opt/pig
mv /opt/pig-0.17.0 /opt/pig
chown -R jayanigha_1602:jayanigha_1602 /opt/pig

echo "=== Adding Pig to System Profile ==="
cat << 'PROFILE' >> /etc/profile.d/pig.sh
export PIG_HOME=/opt/pig
export PATH=$PATH:$PIG_HOME/bin
export PIG_CLASSPATH=$HADOOP_CONF_DIR
PROFILE
chmod +x /etc/profile.d/pig.sh

echo "Pig 0.17.0 installed and configured."
