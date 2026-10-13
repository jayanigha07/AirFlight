#!/bin/bash
set -e

rm -f /usr/local/bin/hdfs /usr/local/bin/hadoop /usr/local/bin/yarn /usr/local/bin/mapred /usr/local/bin/start-* /usr/local/bin/stop-*

cat << 'EOF' > /etc/profile.d/hadoop.sh
export HADOOP_HOME=/opt/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export PDSH_RCMD_TYPE=ssh
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
EOF
chmod +x /etc/profile.d/hadoop.sh

# Also add to /etc/environment
grep -q "/opt/hadoop/bin" /etc/environment || echo 'PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/hadoop/bin:/opt/hadoop/sbin"' > /tmp/env_path && cat /tmp/env_path > /etc/environment
echo "Hadoop profile configured."
