# AirFlight: Environment Setup & Configuration Guide

This guide documents the exact steps, environment configurations, and configuration files required to set up the AirFlight Big Data stack on **Ubuntu 22.04 LTS (WSL2 or Bare Metal)**.

---

## 1. Prerequisites & System Requirements

- **OS:** Ubuntu 22.04.5 LTS
- **Memory:** Minimum 8 GB RAM (WSL2 allocated: ~7.7 GB RAM + 2 GB Swap)
- **Disk:** Minimum 20 GB free disk space
- **Shell:** Bash (Login shell `/etc/profile.d/00-bigdata.sh` configured)

---

## 2. Java 8 Installation

Hadoop 3.3.6, Hive 3.1.3, Pig 0.17.0, and Sqoop 1.4.7 require Java 8 (OpenJDK 1.8).

```bash
sudo apt-get update
sudo apt-get install -y openjdk-8-jdk openjdk-8-jre

# Verify
java -version
# Expected: openjdk version "1.8.0_..."
```

Set `/etc/profile.d/00-bigdata.sh`:
```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

---

## 3. SSH Setup (Passphrase-less for Hadoop Daemons)

```bash
sudo apt-get install -y ssh pdsh
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys

# Set PDSH execution type
echo "export PDSH_RCMD_TYPE=ssh" >> ~/.bashrc
```

---

## 4. Apache Hadoop 3.3.6 Configuration

Install Hadoop to `/opt/hadoop`:
```bash
wget https://archive.apache.org/dist/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
sudo tar -xzf hadoop-3.3.6.tar.gz -C /opt/
sudo mv /opt/hadoop-3.3.6 /opt/hadoop
sudo chown -R $USER:$USER /opt/hadoop
```

### Core Hadoop Configurations (`/opt/hadoop/etc/hadoop/`)

#### 1. `hadoop-env.sh`
```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_HOME=/opt/hadoop
export HADOOP_LOG_DIR=/opt/hadoop/logs
export HDFS_NAMENODE_USER=$USER
export HDFS_DATANODE_USER=$USER
export HDFS_SECONDARYNAMENODE_USER=$USER
export YARN_RESOURCEMANAGER_USER=$USER
export YARN_NODEMANAGER_USER=$USER
```

#### 2. `core-site.xml`
```xml
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
```

#### 3. `hdfs-site.xml`
```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:///home/jayanigha_1602/hdata/namenode</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:///home/jayanigha_1602/hdata/datanode</value>
    </property>
</configuration>
```

#### 4. `yarn-site.xml` (Tuned for ~7.7 GB RAM)
```xml
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>4096</value>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>4096</value>
    </property>
    <property>
        <name>yarn.scheduler.minimum-allocation-mb</name>
        <value>256</value>
    </property>
    <property>
        <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
    </property>
</configuration>
```

#### 5. `mapred-site.xml`
```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
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
    <property>
        <name>yarn.app.mapreduce.am.resource.mb</name>
        <value>512</value>
    </property>
    <property>
        <name>yarn.app.mapreduce.am.command-opts</name>
        <value>-Xmx384m</value>
    </property>
</configuration>
```

---

## 5. MySQL 8.0 Setup

```bash
sudo apt-get install -y mysql-server
sudo systemctl enable mysql
sudo systemctl start mysql

# Create databases and users
sudo mysql -e "
CREATE USER IF NOT EXISTS 'airflight'@'localhost' IDENTIFIED BY 'airflight123';
CREATE DATABASE IF NOT EXISTS airflight;
CREATE DATABASE IF NOT EXISTS metastore;
GRANT ALL PRIVILEGES ON airflight.* TO 'airflight'@'localhost';
GRANT ALL PRIVILEGES ON metastore.* TO 'airflight'@'localhost';
FLUSH PRIVILEGES;
"
```

---

## 6. Apache Sqoop 1.4.7 Setup

```bash
wget https://archive.apache.org/dist/sqoop/1.4.7/sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz
sudo tar -xzf sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz -C /opt/
sudo mv /opt/sqoop-1.4.7.bin__hadoop-2.6.0 /opt/sqoop
sudo chown -R $USER:$USER /opt/sqoop

# Copy MySQL Connector and commons-lang
sudo cp /opt/hadoop/share/hadoop/yarn/lib/commons-lang-2.6.jar /opt/sqoop/lib/
sudo wget -O /opt/sqoop/lib/mysql-connector-j-8.0.33.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
```

---

## 7. Apache Pig 0.17.0 Setup

```bash
wget https://archive.apache.org/dist/pig/pig-0.17.0/pig-0.17.0.tar.gz
sudo tar -xzf pig-0.17.0.tar.gz -C /opt/
sudo mv /opt/pig-0.17.0 /opt/pig
sudo chown -R $USER:$USER /opt/pig
```

---

## 8. Apache Hive 3.1.3 Setup & Metastore Initialization

```bash
wget https://archive.apache.org/dist/hive/hive-3.1.3/apache-hive-3.1.3-bin.tar.gz
sudo tar -xzf apache-hive-3.1.3-bin.tar.gz -C /opt/
sudo mv /opt/apache-hive-3.1.3-bin /opt/hive
sudo chown -R $USER:$USER /opt/hive

# Critical Guava conflict resolution
rm -f /opt/hive/lib/guava-19.0.jar
cp /opt/hadoop/share/hadoop/common/lib/guava-27.0-jre.jar /opt/hive/lib/

# Add MySQL driver to Hive
cp /opt/sqoop/lib/mysql-connector-j-8.0.33.jar /opt/hive/lib/

# Configure Hive Metastore in /opt/hive/conf/hive-site.xml:
# (Connection URL: jdbc:mysql://localhost:3306/metastore, User: airflight)

# Initialize Metastore schema
/opt/hive/bin/schematool -dbType mysql -initSchema
```

---

## 9. MongoDB 7.0 Setup

```bash
sudo apt-get install -y gnupg curl
curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor
echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list
sudo apt-get update
sudo apt-get install -y mongodb-org python3-pymongo
sudo systemctl enable mongod
sudo systemctl start mongod
```

---

## 10. Global Environment Configuration (`/etc/profile.d/00-bigdata.sh`)

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_HOME=/opt/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HIVE_HOME=/opt/hive
export PIG_HOME=/opt/pig
export SQOOP_HOME=/opt/sqoop

export PATH=$PATH:$JAVA_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HIVE_HOME/bin:$PIG_HOME/bin:$SQOOP_HOME/bin
```

---

## 11. Complete Pipeline Execution Commands

```bash
# 1. Start all Hadoop and YARN daemons
/mnt/c/Users/DELL/Desktop/airflight/scripts/start_hadoop_all.sh

# 2. Stage real dataset in MySQL
mysql -u airflight -pairflight123 airflight < /mnt/c/Users/DELL/Desktop/airflight/mysql/schema.sql
mysql --local-infile=1 -u airflight -pairflight123 airflight < /mnt/c/Users/DELL/Desktop/airflight/mysql/load_data.sql

# 3. Import MySQL tables into HDFS via Sqoop
/mnt/c/Users/DELL/Desktop/airflight/scripts/sqoop_import_flights.sh

# 4. Compile and Run Java MapReduce on YARN
/mnt/c/Users/DELL/Desktop/airflight/scripts/run_mapreduce.sh

# 5. Run Pig ETL cleaning pipeline
/mnt/c/Users/DELL/Desktop/airflight/scripts/run_pig.sh

# 6. Run Hive SQL warehouse analytics
/mnt/c/Users/DELL/Desktop/airflight/scripts/run_hive.sh

# 7. Ingest into MongoDB
/mnt/c/Users/DELL/Desktop/airflight/scripts/load_mongo_from_hdfs.py

# 8. Run end-to-end pipeline validation
python3 /mnt/c/Users/DELL/Desktop/airflight/scripts/validate_pipeline.py
```
