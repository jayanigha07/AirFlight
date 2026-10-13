#!/bin/bash
set -e

echo "=== Extracting Sqoop 1.4.7 ==="
tar -xzf /opt/downloads/sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz -C /opt
rm -rf /opt/sqoop
mv /opt/sqoop-1.4.7.bin__hadoop-2.6.0 /opt/sqoop
chown -R jayanigha_1602:jayanigha_1602 /opt/sqoop

echo "=== Installing MySQL JDBC Connector in Sqoop ==="
cp /opt/downloads/mysql-connector-j-8.0.33.jar /opt/sqoop/lib/
# Also link commons-lang if needed
cp /opt/hadoop/share/hadoop/common/lib/commons-lang-2.6.jar /opt/sqoop/lib/ 2>/dev/null || true

echo "=== Configuring sqoop-env.sh ==="
cp /opt/sqoop/conf/sqoop-env-template.sh /opt/sqoop/conf/sqoop-env.sh
cat << 'CONF' >> /opt/sqoop/conf/sqoop-env.sh
export HADOOP_COMMON_HOME=/opt/hadoop
export HADOOP_MAPRED_HOME=/opt/hadoop
export HBASE_HOME=/var/empty
export HIVE_HOME=/var/empty
export ZOOKEEPER_HOME=/var/empty
export ACCUMULO_HOME=/var/empty
CONF

echo "=== Adding Sqoop to Profile ==="
cat << 'PROFILE' >> /etc/profile.d/sqoop.sh
export SQOOP_HOME=/opt/sqoop
export PATH=$PATH:$SQOOP_HOME/bin
PROFILE
chmod +x /etc/profile.d/sqoop.sh

echo "Sqoop 1.4.7 installed and configured."
