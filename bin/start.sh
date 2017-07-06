#!/bin/bash

#./start_server.sh --env=sep

ENVIRONMENT="dev"

if [ $# = 0 ]; then
   echo "no arguments provided."
   exit 1
fi

while [ "$1" != "" ]; do
    PARAM=`echo $1 | awk -F= '{print $1}'`
    VALUE=`echo $1 | awk -F= '{print $2}'`
    case ${PARAM} in
        -e | --env)
            ENVIRONMENT=${VALUE}
            ;;
        *)
            echo "ERROR: unknown parameter \"$PARAM\""
            usage
            exit 1
            ;;
    esac
    shift
done

bin=`dirname "$0"`
BASEDIR=`cd "$bin/.."; pwd`

export BASEDIR

CLASSPATH=${BASEDIR}/conf/${ENVIRONMENT}/:${BASEDIR}/conf/common/:${BASEDIR}/lib/*

nowday=`date +%Y%m%d_%H%M%S`
mkdir -p logs
GC_LOG_DIR=logs/gclogs
test -d $GC_LOG_DIR || mkdir $GC_LOG_DIR
NUM=`ls -latr logs/gclogs | wc -l`
if [ ${NUM} -gt 7 ]; then
    cd logs/gclogs
    DELETENUM=$((NUM-7))
    ls -latr | awk '{print $9}' | head -n ${DELETENUM} | xargs rm -rf {}
    cd ../..
fi

GCARGS='-Xms1024M -Xmx1024M'
if [ "$ENVIRONMENT" == "prod" ]; then
    GCARGS='-Xms4096M -Xmx4096M'
fi

echo "starting gamble-proxy server..."

java -Dfile.encoding=UTF-8 -Dyt.env=${ENVIRONMENT} ${GCARGS} -XX:+UseG1GC -XX:+PrintGCDetails -Xloggc:${GC_LOG_DIR}/gc.log.${nowday} -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs/gamble-proxy.dump -classpath ${CLASSPATH} com.lottery.gamble.proxy.StartApplication > logs/console.log 2>&1 &

PID=$!
echo ${PID} > logs/app.pid
echo "pid is ${PID}"