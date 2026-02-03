#!/bin/bash

APP_DIR="/home/ubuntu/app"
LOG_PATH="$APP_DIR/app.log"

echo "> 현재 시간: $(date)"

JAR_NAME=$(ls -tr $APP_DIR/*.jar | tail -n 1)

if [ -z "$JAR_NAME" ]; then
  echo "> 오류: 배포할 JAR 파일을 찾을 수 없습니다."
  exit 1
fi

echo "> 배포할 JAR: $JAR_NAME"

if [ -f "$APP_DIR/.env" ]; then
  echo "> .env 파일을 로드합니다."
  export $(cat $APP_DIR/.env | xargs)
fi

CURRENT_PID=$(pgrep -f "java -jar")

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> 실행 중인 애플리케이션 종료 (PID: $CURRENT_PID)"
  kill -15 $CURRENT_PID
  sleep 10
fi

REMAIN_PID=$(pgrep -f "java -jar")
if [ -n "$REMAIN_PID" ]; then
  echo "> 프로세스가 종료되지 않아 강제 종료합니다. (kill -9 $REMAIN_PID)"
  kill -9 $REMAIN_PID
  sleep 1
fi

echo "> 새 애플리케이션 배포"

chmod +x $JAR_NAME
nohup java -Xmx1024m -jar -Dspring.profiles.active=dev $JAR_NAME > $LOG_PATH 2>&1 &

echo "> 배포 완료. 로그를 확인하려면: tail -f $LOG_PATH"