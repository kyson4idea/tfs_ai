#!/bin/bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
LIB_DIR=$DEPLOY_DIR/lib
CONF_DIR=$DEPLOY_DIR/conf
LOGS_DIR=`dirname $PWD`/logs
STDOUT_FILE=$LOGS_DIR/stdout.log
START_REPORT_FILE=$LOGS_DIR/shell.log
REPORT_FILE=$LOGS_DIR/report.flag
# process system properties to java
JAVA_PROPERTIES_OPTS="-Dsmy.started.reportfile=${REPORT_FILE}"

# JAVA_HOME in linux path
export JAVA_HOME="/usr/java/jdk1.8.0_201"
export PATH="/usr/java/jdk1.8.0_201/bin:$PATH"
#export JAVA_HOME
#export PATH

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

SERVER_NAME="tfs-boot"

if [ -z "$SERVER_NAME" ]; then
    SERVER_NAME=`hostname`
fi

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

JAVA_OPTS="-DappName=$SERVER_NAME -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Ddubbo.shutdown.hook=true"
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    addressPort=8200
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

JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
JAVA_MEM_SIZE_OPTS="-Xms2048m -Xmx2048m -XX:MetaspaceSize=2048m -XX:MaxMetaspaceSize=2048m"
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server $JAVA_MEM_SIZE_OPTS -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
else
    JAVA_MEM_OPTS=" -server $JAVA_MEM_SIZE_OPTS -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

echoReport "Starting the $SERVER_NAME ..."
reportTo "java -jar $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $JAVA_PROPERTIES_OPTS $SERVER_NAME.jar"
nohup java -jar $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $JAVA_PROPERTIES_OPTS $LIB_DIR/$SERVER_NAME.jar >$STDOUT_FILE &
sleep 1
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
#   echoReport "STDOUT: $STDOUT_FILE"
    exit 1
elif [ $CHECK_MAX_COUNT -le 0 ]; then
    echoReport "Flag file: '${REPORT_FILE}' does not exist, maybe have some error!"
    echoReport "PID: $APP_PID"
#   echoReport "STDOUT: $STDOUT_FILE"
    exit 0
else
    echoReport "Start App OK!"
    echoReport "PID: $APP_PID"
#   echoReport "STDOUT: $STDOUT_FILE"
    exit 0
fi

