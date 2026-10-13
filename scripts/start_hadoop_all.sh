#!/bin/bash
export PDSH_RCMD_TYPE=ssh
export HADOOP_HOME=/opt/hadoop
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

# Ensure HDFS daemons
nohup /opt/hadoop/bin/hdfs --daemon start namenode >/dev/null 2>&1 &
nohup /opt/hadoop/bin/hdfs --daemon start datanode >/dev/null 2>&1 &
nohup /opt/hadoop/bin/hdfs --daemon start secondarynamenode >/dev/null 2>&1 &

# Ensure YARN daemons
nohup /opt/hadoop/bin/yarn --daemon start resourcemanager >/dev/null 2>&1 &
nohup /opt/hadoop/bin/yarn --daemon start nodemanager >/dev/null 2>&1 &

sleep 4
jps
