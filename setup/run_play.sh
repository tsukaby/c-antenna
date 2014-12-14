#!/bin/sh

start(){
  sudo /usr/local/app/c-antenna/bin/c-antenna -Dconfig.resource=prod.conf -Dhttp.port=80 >/dev/null 2>&1 &
}

stop(){
  if [[ -e /usr/local/app/c-antenna/RUNNING_PID ]]; then
    sudo kill -9 `cat /usr/local/app/c-antenna/RUNNING_PID`;
  else
    echo NOT RUNNING;
  fi;
}

case "$1" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  *)
    echo $"Usage: $0 {start|stop}"
    exit 2
esac