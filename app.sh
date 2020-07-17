#!/bin/bash

if [ "$1" = "" ]; then
  echo "Please select [start | stop | restart]"
  exit
fi

if [ $1 == "stop" ] || [ $1 == "restart" ]; then
  xargs -a app.pid kill && rm app.pid
fi

if [ $1 == "start" ] && [ -f "app.pid" ]; then
  echo "The app has started."
elif [ $1 == "start" ] || [ $1 == "restart" ]; then
  mvn -q spring-boot:run >> logs/app.log & echo $! > app.pid
fi