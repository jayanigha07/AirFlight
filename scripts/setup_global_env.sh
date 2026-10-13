#!/bin/bash
set -e

cat << 'EOF' > /etc/profile.d/00-bigdata.sh
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_HOME=/opt/hadoop
export HADOOP_INSTALL=/opt/hadoop
export HADOOP_MAPRED_HOME=/opt/hadoop
export HADOOP_COMMON_HOME=/opt/hadoop
export HADOOP_HDFS_HOME=/opt/hadoop
export YARN_HOME=/opt/hadoop
export HADOOP_COMMON_LIB_NATIVE_DIR=/opt/hadoop/lib/native
export HADOOP_CONF_DIR=/opt/hadoop/etc/hadoop
export SQOOP_HOME=/opt/sqoop
export PIG_HOME=/opt/pig
export PIG_CLASSPATH=/opt/hadoop/etc/hadoop
export HIVE_HOME=/opt/hive
export PDSH_RCMD_TYPE=ssh
export PATH=$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$SQOOP_HOME/bin:$PIG_HOME/bin:$HIVE_HOME/bin:$PATH
EOF
chmod +x /etc/profile.d/00-bigdata.sh

# Add to top of /etc/bash.bashrc and /home/jayanigha_1602/.bashrc
sed -i '1i source /etc/profile.d/00-bigdata.sh' /home/jayanigha_1602/.bashrc 2>/dev/null || true
grep -q "00-bigdata.sh" /etc/bash.bashrc || sed -i '1i source /etc/profile.d/00-bigdata.sh' /etc/bash.bashrc

echo "Global Big Data environment configured."
