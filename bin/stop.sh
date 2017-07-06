#!/bin/bash

bin1=`dirname "$0"`
bin1=`cd "$bin1"; pwd`
BASEDIR1=`cd "${bin1}/.."; pwd`

function killpid()
{
  if [ ! -z "$*" ] ; then
	echo "kill $*"
    kill $*
  fi
}

function forceKillPid(){
   if [ ! -z "$*" ] ; then
      echo "force kill $*"
      kill -9 $*
    fi
}

#PID=`jps -l | grep 'StartApplication'| awk '{print $1}'`
PID=`cat logs/app.pid`
IS_PID=`ps -p $PID |grep $PID | wc -l`
if [ $IS_PID -eq 0 ]; then
    exit 1
fi
if [ -n ${PID} ]; then
    killpid ${PID}
    sleep 3
    IS_PID1=`ps -p $PID |grep $PID | wc -l`
    if [ $IS_PID1 -eq 1 ] ; then
       forceKillPid ${PID}
    fi
    rm -rf logs/app.pid
fi