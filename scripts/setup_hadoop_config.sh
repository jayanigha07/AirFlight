#!/bin/bash
set -e

CONF_DIR="/opt/hadoop/etc/hadoop"

mkdir -p /home/jayanigha_1602/hdata/tmp
mkdir -p /home/jayanigha_1602/hdata/dfs/name
mkdir -p /home/jayanigha_1602/hdata/dfs/data

# 1. hadoop-env.sh
sed -i 's|^# export JAVA_HOME=.*|export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64|' "$CONF_DIR/hadoop-env.sh"
grep -q "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64" "$CONF_DIR/hadoop-env.sh" || echo "export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64" >> "$CONF_DIR/hadoop-env.sh"

cat << 'ENVEOF' >> "$CONF_DIR/hadoop-env.sh"
export HADOOP_OPTS="-Djava.net.preferIPv4Stack=true"
export HADOOP_NAMENODE_OPTS="-Xmx512m -Djava.net.preferIPv4Stack=true"
export HADOOP_DATANODE_OPTS="-Xmx512m -Djava.net.preferIPv4Stack=true"
export YARN_RESOURCEMANAGER_OPTS="-Xmx512m"
export YARN_NODEMANAGER_OPTS="-Xmx512m"
ENVEOF

# 2. core-site.xml
cat << 'CORE' > "$CONF_DIR/core-site.xml"
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/home/jayanigha_1602/hdata/tmp</value>
    </property>
</configuration>
CORE

# 3. hdfs-site.xml
cat << 'HDFS' > "$CONF_DIR/hdfs-site.xml"
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:///home/jayanigha_1602/hdata/dfs/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:///home/jayanigha_1602/hdata/dfs/data</value>
    </property>
</configuration>
HDFS

# 4. mapred-site.xml
cat << 'MAPRED' > "$CONF_DIR/mapred-site.xml"
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapreduce.application.classpath</name>
        <value>/opt/hadoop/etc/hadoop:/opt/hadoop/share/hadoop/common/*:/opt/hadoop/share/hadoop/common/lib/*:/opt/hadoop/share/hadoop/hdfs/*:/opt/hadoop/share/hadoop/hdfs/lib/*:/opt/hadoop/share/hadoop/mapreduce/*:/opt/hadoop/share/hadoop/mapreduce/lib/*:/opt/hadoop/share/hadoop/yarn/*:/opt/hadoop/share/hadoop/yarn/lib/*</value>
    </property>
    <property>
        <name>mapreduce.map.memory.mb</name>
        <value>512</value>
    </property>
    <property>
        <name>mapreduce.reduce.memory.mb</name>
        <value>512</value>
    </property>
    <property>
        <name>mapreduce.map.java.opts</name>
        <value>-Xmx384m</value>
    </property>
    <property>
        <name>mapreduce.reduce.java.opts</name>
        <value>-Xmx384m</value>
    </property>
</configuration>
MAPRED

# 5. yarn-site.xml
cat << 'YARN' > "$CONF_DIR/yarn-site.xml"
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services.mapreduce_shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>
    <property>
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>2048</value>
    </property>
    <property>
        <name>yarn.scheduler.minimum-allocation-mb</name>
        <value>256</value>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>1024</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>localhost</value>
    </property>
    <property>
        <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
    </property>
    <property>
        <name>yarn.nodemanager.pmem-check-enabled</name>
        <value>false</value>
    </property>
</configuration>
YARN

# 6. Update user's .bashrc with Hadoop env vars
grep -q "HADOOP_HOME" /home/jayanigha_1602/.bashrc || cat << 'BASHRC' >> /home/jayanigha_1602/.bashrc

# Apache Hadoop Environment
export HADOOP_HOME=/opt/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin
BASHRC

echo "All Hadoop configuration files written successfully."
