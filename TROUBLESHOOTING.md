# AirFlight: Troubleshooting Guide & Resolved Gotchas

This document records the exact technical issues encountered during the build and execution of the AirFlight Big Data stack on Ubuntu 22.04 LTS (WSL2), along with the root causes and permanent solutions.

---

## 1. WSL2 Daemon Termination & Session SIGHUP

### Issue
When starting Hadoop daemons via `start-dfs.sh` and `start-yarn.sh` through short-lived terminal sessions or IDE tool runners, NameNode, DataNode, or NodeManager would immediately terminate after the subshell closed.
### Root Cause
WSL2 sends `SIGHUP` to process groups spawned from child subshells unless explicitly detached with `nohup` or managed by systemd. Additionally, `pdsh` defaulted to `rsh` rather than `ssh`, causing remote execution failures.
### Resolution
Created [scripts/start_hadoop_all.sh](file:///c:/Users/DELL/Desktop/airflight/scripts/start_hadoop_all.sh):
```bash
export PDSH_RCMD_TYPE=ssh
nohup /opt/hadoop/bin/hdfs --daemon start namenode > /dev/null 2>&1 &
nohup /opt/hadoop/bin/hdfs --daemon start datanode > /dev/null 2>&1 &
nohup /opt/hadoop/bin/hdfs --daemon start secondarynamenode > /dev/null 2>&1 &
nohup /opt/hadoop/bin/yarn --daemon start resourcemanager > /dev/null 2>&1 &
nohup /opt/hadoop/bin/yarn --daemon start nodemanager > /dev/null 2>&1 &
nohup /opt/hadoop/bin/mapred --daemon start historyserver > /dev/null 2>&1 &
```

---

## 2. YARN Container Memory Rejection & AM Allocation Failure

### Issue
MapReduce jobs submitted to YARN (including Sqoop and Hive jobs) remained stuck in `ACCEPTED` or failed with:
`Application application_... submitted by user ... exceeds maximum allocation threshold (1024 MB)`.
### Root Cause
Default YARN NodeManager settings allocated only 1024 MB to the total resource pool, while default MapReduce ApplicationMaster requested 1536 MB.
### Resolution
Updated `/opt/hadoop/etc/hadoop/yarn-site.xml` and `mapred-site.xml`:
- Set `yarn.nodemanager.resource.memory-mb` to `4096`.
- Set `yarn.scheduler.maximum-allocation-mb` to `4096`.
- Set `yarn.app.mapreduce.am.resource.mb` to `512` (`-Xmx384m`).
- Set `mapreduce.map.memory.mb` and `mapreduce.reduce.memory.mb` to `512` (`-Xmx384m`).

---

## 3. Hive 3.1.3 Guava Dependency Conflict

### Issue
Running `schematool -initSchema` or launching `hive` failed with:
`java.lang.NoSuchMethodError: com.google.common.base.Preconditions.checkArgument(ZLjava/lang/String;Ljava/lang/Object;)V`.
### Root Cause
Apache Hive 3.1.3 ships with Google Guava version 19.0 (`guava-19.0.jar`), whereas Hadoop 3.3.6 uses Guava version 27.0 (`guava-27.0-jre.jar`). When Hive launched on the Hadoop classpath, classloader incompatibility caused method resolution failures.
### Resolution
Replaced Hive's outdated Guava jar with Hadoop's modern jar:
```bash
rm -f /opt/hive/lib/guava-19.0.jar
cp /opt/hadoop/share/hadoop/common/lib/guava-27.0-jre.jar /opt/hive/lib/
```

---

## 4. Apache Sqoop 1.4.7 ClassNotFound & JDBC Driver

### Issue 1
Sqoop import threw:
`java.lang.NoClassDefFoundError: org/apache/commons/lang/StringUtils`.
### Root Cause
Hadoop 3 removed legacy `commons-lang-2.6.jar` from its common classpath, which Sqoop 1.4.7 depends upon.
### Resolution
Copied `commons-lang-2.6.jar` from the Hadoop YARN library into `/opt/sqoop/lib/`:
```bash
cp /opt/hadoop/share/hadoop/yarn/lib/commons-lang-2.6.jar /opt/sqoop/lib/
```

### Issue 2
Sqoop failed with:
`Could not load db driver class: com.mysql.jdbc.Driver`.
### Root Cause
Sqoop requires the MySQL JDBC connector jar inside `/opt/sqoop/lib/`.
### Resolution
Installed `mysql-connector-j-8.0.33.jar` into `/opt/sqoop/lib/` and `/opt/hive/lib/`.

---

## 5. MySQL 8.0 `LOAD DATA LOCAL INFILE` Permission

### Issue
MySQL returned:
`ERROR 1148 (42000): The used command is not allowed with this MySQL version` or `ERROR 3948: Loading local data is disabled`.
### Root Cause
MySQL 8.0 disables local file loading by default on both the server and client.
### Resolution
1. Set server variable: `SET GLOBAL local_infile = 1;`
2. Executed client with flag: `mysql --local-infile=1 -u airflight -pairflight123 ...`

---

## 6. Null Semantics Across Pipeline Transitions

### Issue
Flight cancellation and delay columns contain missing values for completed flights (e.g. `cancellation_reason` is NULL for 97% of flights, while `arrival_delay` is NULL for cancelled flights). If exported improperly, these become the string literal `"null"` or fail downstream type conversions.
### Resolution
1. **MySQL to Sqoop:** Configured `--null-string '\\N' --null-non-string '\\N'` in Sqoop, standardizing nulls as `\N`.
2. **Pig Latin:** Loaded columns as `chararray`, filtering out invalid rows and converting with `(arrival_delay == '\\N' ? null : (double)arrival_delay)`.
3. **Hive External Tables:** Mapped external table columns directly to the TSV fields produced by Pig, which outputs standard Hive-compatible empty tab separations for nulls.
4. **MongoDB:** Handled in [scripts/load_mongo_from_hdfs.py](file:///c:/Users/DELL/Desktop/airflight/scripts/load_mongo_from_hdfs.py) by parsing empty strings or `\N` explicitly as Python `None`, ensuring clean BSON `null` storage.

---

## 7. Fast Diagnostic & Service Recovery Checklist

If any Big Data service behaves unexpectedly, execute the following commands in sequence:

```bash
# 1. Check all running Java processes
jps
# Expected: NameNode, DataNode, SecondaryNameNode, ResourceManager, NodeManager, JobHistoryServer

# 2. Check relational and NoSQL daemons
sudo systemctl status mysql --no-pager
sudo systemctl status mongod --no-pager

# 3. Check HDFS health
hdfs dfsadmin -report

# 4. Check YARN cluster status
yarn node -list

# 5. Check free memory
free -m
```
