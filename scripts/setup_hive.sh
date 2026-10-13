#!/bin/bash
set -e

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_HOME=/opt/hadoop
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:/opt/hive/bin

echo "=== Extracting Apache Hive 3.1.3 ==="
if [ ! -d "/opt/hive" ]; then
    tar -xzf /opt/downloads/apache-hive-3.1.3-bin.tar.gz -C /opt
    mv /opt/apache-hive-3.1.3-bin /opt/hive
fi

echo "=== Fixing Guava Version Incompatibility ==="
rm -f /opt/hive/lib/guava-19.0.jar
cp /opt/hadoop/share/hadoop/common/lib/guava-27.0-jre.jar /opt/hive/lib/

echo "=== Installing MySQL JDBC Connector in Hive ==="
cp /opt/downloads/mysql-connector-j-8.0.33.jar /opt/hive/lib/

echo "=== Configuring hive-site.xml ==="
cat << 'XML' > /opt/hive/conf/hive-site.xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://localhost:3306/metastore?createDatabaseIfNotExist=true&amp;useSSL=false&amp;allowPublicKeyRetrieval=true&amp;serverTimezone=UTC</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.cj.jdbc.Driver</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>hive</value>
    </property>
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>hive123</value>
    </property>
    <property>
        <name>hive.metastore.warehouse.dir</name>
        <value>/user/hive/warehouse</value>
    </property>
    <property>
        <name>hive.metastore.schema.verification</name>
        <value>false</value>
    </property>
    <property>
        <name>hive.exec.scratchdir</name>
        <value>/tmp/hive</value>
    </property>
</configuration>
XML

chown -R jayanigha_1602:jayanigha_1602 /opt/hive

echo "=== Adding Hive to System Profile ==="
cat << 'PROFILE' > /etc/profile.d/hive.sh
export HIVE_HOME=/opt/hive
export PATH=$PATH:$HIVE_HOME/bin
PROFILE
chmod +x /etc/profile.d/hive.sh

echo "=== Creating Hive Directories in HDFS as jayanigha_1602 ==="
su - jayanigha_1602 -c "
/opt/hadoop/bin/hdfs dfs -mkdir -p /tmp/hive
/opt/hadoop/bin/hdfs dfs -mkdir -p /user/hive/warehouse
/opt/hadoop/bin/hdfs dfs -chmod -R 777 /tmp
/opt/hadoop/bin/hdfs dfs -chmod -R 777 /user/hive/warehouse
"

echo "=== Initializing Hive Metastore in MySQL ==="
su - jayanigha_1602 -c "/opt/hive/bin/schematool -dbType mysql -initSchema" || echo "Schema already initialized"

echo "Hive 3.1.3 installed and configured successfully."
