#!/bin/sh
#export APP_NAME=""
#export APP_PORT=""
#export JAR_FILE="$APP_NAME/target/$APP_NAME-2.0.0.jar"
#bash deploy.sh $1 $2

#default use: -server -Xms256m -Xmx512m --server.port=$APP_PORT --spring.cloud.config.profile=dev

cd "$(dirname "$0")"

if [[ "$1" = "" ]]; then
  echo "Usage: sh xxx.sh [ (deploy | stop | start | restart) [seconds] ]"
  echo "Default use deploy 10, deploy means copy and restart from target directory, stop waiting for 10 seconds by default.";
fi

if [[ "$2" = "" ]]; then
  SLEEP="10";
else
  SLEEP=$2
fi

if [ -z "$JAVA_OPTS" ]; then
  JAVA_OPTS="-server -Xms256m -Xmx512m";
fi

if [ -z "$SPRING_OPTS" ]; then
  SPRING_OPTS="--server.port=$APP_PORT";
fi

SERVICE_NAME="$APP_NAME-$APP_PORT"
#echo "Using JAVA_HOME:  $JAVA_HOME"

if [[ $APP_NAME = "" || $APP_PORT = "" ]]; then
  exit 1
fi

PID=`ps aux|grep $SERVICE_NAME |grep 'java ' |grep -v grep|awk '{print $2}'`
if [[ "$1" = "" || $1 = "stop" || $1 = "restart" || $1 = "deploy" ]] && [ -n "$PID" ]; then
  echo "$(date +"%F %T,%3N") stopping $SERVICE_NAME, PID: $PID."
  kill $PID
  while [ $SLEEP -ge 0 ]; do
    PID=`ps aux|grep $SERVICE_NAME |grep 'java ' |grep -v grep|awk '{print $2}'`
    if [ $? -eq 0 ] && [ ! $PID ]; then
      echo "$(date +"%F %T,%3N") $SERVICE_NAME has stopped."
      break
    fi
    if [ $SLEEP -gt 0 ]; then
      sleep 1
      wait
    fi
    SLEEP=`expr $SLEEP - 1 `
  done
  
  PID=`ps aux|grep $SERVICE_NAME |grep 'java ' |grep -v grep|awk '{print $2}'`
  if [ -n "$PID" ]; then
    echo "$(date +"%F %T,%3N") $SERVICE_NAME did not stop in time. Execute the force kill command:"
    echo "$(date +"%F %T,%3N") kill -3 $PID"
    kill -3 $PID
    wait
    echo "$(date +"%F %T,%3N") kill -9 $PID"
    kill -9 $PID
  fi
  PID=`ps aux|grep $SERVICE_NAME |grep 'java ' |grep -v grep|awk '{print $2}'`
  if [ -n "$PID" ]; then
    echo "$(date +"%F %T,%3N") $SERVICE_NAME did not stop in time. Please check the reason!"
    exit 1
  fi
fi

if [[ $1 = "stop" ]]; then
  exit 0
fi

PID=`ps aux|grep java|grep $SERVICE_NAME |grep 'java ' |grep -v grep|awk '{print $2}'`
if [ -n "$PID" ]; then
  echo "$(date +"%F %T,%3N") $SERVICE_NAME has started. PID: $PID"
  exit 1
fi

if [[ "$1" = "" || $1 = "deploy" ]]; then
  mkdir -p ./jars ./logs
  rm -rf  ./jars/$SERVICE_NAME.jar
  cp -rf  $JAR_FILE ./jars/$SERVICE_NAME.jar
fi

if [[ "$1" = "" || $1 = "deploy" || $1 = "start" || $1 = "restart" ]]; then
  echo "java $JAVA_OPTS -jar ./jars/$SERVICE_NAME.jar $SPRING_OPTS >> ./logs/$SERVICE_NAME.log 2>&1 &"
  nohup java $JAVA_OPTS -jar ./jars/$SERVICE_NAME.jar $SPRING_OPTS >> ./logs/$SERVICE_NAME.log 2>&1 &
  
  if [[ "$2" = "" ]]; then
    echo "tail -F ./logs/$SERVICE_NAME.log"
    tail -F ./logs/$SERVICE_NAME.log
  fi
fi