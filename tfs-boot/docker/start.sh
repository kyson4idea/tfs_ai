#!/bin/bash

# SIGTERM-handler
# 拦截处理函数
term_handler() {
  today=`date --date='0 days ago' "+%Y-%m-%d %H:%M:%S"`
  #输出信息至日志 日志挂载目录根据应用而定
  echo "$today stop system" >> /app/smy-tfs/jetty/logs/gracefulPublish.log
  KILL_PS=""
  for PID in $APP_PID ; do
    kill $PID >/dev/null &
    KILL_PS="$! $KILL_PS"
    #输出信息至日志 日志挂载目录根据应用而定
    echo "kill system" >> /app/smy-tfs/jetty/logs/gracefulPublish.log
  done

  # sleep 10s to recycle vm
  sleep 10
  for kPID in $KILL_PS; do
    wait $kPID
    #输出信息至日志 日志挂载目录根据应用而定
    echo "sleep 10s to recycle vm" >> /app/smy-tfs/jetty/logs/gracefulPublish.log
  done

  # force kill
  for kPID in $PIDS; do
    kill -9 $kPID >/dev/null 2>&1
    #输出信息至日志 日志挂载目录根据应用而定
    echo "force kill" >> /app/smy-tfs/jetty/logs/gracefulPublish.log
  done
  #输出信息至日志 日志挂载目录根据应用而定
  echo "system already stop" >> /app/smy-tfs/jetty/logs/gracefulPublish.log
  exit 143;
}

# 添加拦截标记
trap 'kill ${!}; term_handler' SIGTERM

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf
LOGS_DIR=$DEPLOY_DIR/logs
STDOUT_FILE=$LOGS_DIR/stdout.log
START_REPORT_FILE=$LOGS_DIR/shell.log
REPORT_FILE=$LOGS_DIR/report.flag
# process system properties to java
JAVA_PROPERTIES_OPTS="-Dsmy.started.reportfile=${REPORT_FILE}"

# JAVA_HOME in linux path
# export JAVA_HOME=/usr/java/jdk1.8.0_201/
export PATH=$JAVA_HOME/bin:$PATH

# echo to $START_REPORT_FILE
reportTo()
{
   echo $* >> "$START_REPORT_FILE"
}
reportJavaVersion()
{
   java -version >> "$START_REPORT_FILE" 2>&1
}
# echo to stdout and echo to $START_REPORT_FILE
echoReport()
{
   echo $* | tee -a "$START_REPORT_FILE"
}

#替换配置中心环境变量值
scm_conf_file=/app/scm/config/service.properties
if [ -f "$scm_conf_file" ];then
        dos2unix $scm_conf_file
fi

if [ $apollo_url ] && [ $idc ];then
        sed -i "s/apollo.meta=.*/apollo.meta=${apollo_url}/g" $scm_conf_file
        sed -i "s/idc=.*/idc=${idc}/g" $scm_conf_file
fi

#SERVER_NAME=`sed '/^app.process.name/!d;s/.*=//' conf/dubbo.properties | tr -d '\r'`
SERVER_NAME=tfs-boot
#if [ -z "$SERVER_NAME" ]; then
#    SERVER_NAME=`hostname`
#fi

reportTo -e "\n================ Time: `date '+%Y-%m-%d %H:%M:%S'` ================"
reportJavaVersion

APP_PID=`ps -ef -ww | grep "java" | grep " -DappName=$SERVER_NAME " | awk '{print $2}'`
if [ -n "$APP_PID" ]; then
    echoReport "INFO: The $SERVER_NAME already started!"
    echoReport "PID: $APP_PID"
    exit 0
fi

if [ ! -d "$LOGS_DIR" ]; then
    mkdir -p "$LOGS_DIR"
fi

if [ -e "${REPORT_FILE}" ]; then
    rm -rf "${REPORT_FILE}"
fi

LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls -1 "$LIB_DIR" | grep -E "\.jar$" | awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

JAVA_OPTS="-DappName=$SERVER_NAME -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Ddubbo.shutdown.hook=true"
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    addressPort=8000
    if [ -n "$2" ]; then
       addressPort=$2
    fi
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${addressPort},server=y,suspend=n "
fi
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    if [ -n "$2" ]; then
       JAVA_JMX_OPTS="$JAVA_JMX_OPTS -Djava.rmi.server.hostname=$2"
    fi
    if [ -n "$3" ]; then
       JAVA_JMX_OPTS="$JAVA_JMX_OPTS -Dcom.sun.management.jmxremote.port=$3"
    fi
    JAVA_JMX_OPTS="$JAVA_JMX_OPTS -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false"
fi

#JAVA_MEM_OPTS=""
#BITS=`java -version 2>&1 | grep -i 64-bit`
#JAVA_MEM_SIZE_OPTS="-Xmx2048m -Xms2048m"
#if [ -n "$BITS" ]; then
#    JAVA_MEM_OPTS=" -server $JAVA_MEM_SIZE_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
#else
#    JAVA_MEM_OPTS=" -server $JAVA_MEM_SIZE_OPTS -XX:+UseParallelGC "
#fi

JAVA_GC_OPTS="-XX:+PrintGCDateStamps -Xloggc:$LOGS_DIR/gc.log -XX:+PrintGCDetails  -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=$LOGS_DIR"

echoReport "Starting the $SERVER_NAME ..."
reportTo "java -jar $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $JAVA_PROPERTIES_OPTS $SERVER_NAME.jar"
nohup java -jar $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $JAVA_PROPERTIES_OPTS /app/smy-tfs/jetty/tfs-boot/lib/tfs-boot.jar >/dev/null 2>&1 &
APP_PID=`ps -ef -ww | grep "java" | grep " -DappName=$SERVER_NAME " | awk '{print $2}'`

if [ -z "$APP_PID" ]; then
    echoReport "START APP FAIL!"
#   echoReport "STDOUT: $STDOUT_FILE"
    exit 1
fi

# 最长检测 1 分钟，可能的结果是标志文件不存在，这个要开发人员自己检查进程启动是否有问题.
CHECK_MAX_COUNT=12
COUNT=0
echo -e "Checking proccess[${APP_PID}]..\c"
while [ $CHECK_MAX_COUNT -gt 0 ]; do
    echo -e ".\c"
    sleep 5
    COUNT=`ps -p $APP_PID | grep -v "PID" | wc -l`
    if [ $COUNT -le 0 ]; then
        break
    fi
    # 标志文件存在，则直接跳出检查
    if [ -e "${REPORT_FILE}" ]; then
        break
    fi
    ((CHECK_MAX_COUNT--))
done


if [ $COUNT -le 0 ]; then
    echoReport "Start App Failed!"
    echo 2 >/app/$USER/flag
    exit 1
else
    echoReport "Start App OK!"
    echo 1 >/app/$USER/flag
    echoReport "PID: $APP_PID"
    for (( ; ; ))
    do
        #等待信号触发
        tail -f /dev/null & wait ${!}
    done
    exit 0
fi

