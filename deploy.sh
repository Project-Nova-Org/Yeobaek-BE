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
  set -a
  source "$APP_DIR/.env"
  set +a
fi

CURRENT_PID=$(pgrep -f "$APP_DIR/.*\.jar" || true)

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> 실행 중인 애플리케이션 종료 (PID: $CURRENT_PID)"
  kill -15 $CURRENT_PID

  for i in {1..15}; do
    sleep 1
    PROCESS_CHECK=$(pgrep -f "$APP_DIR/.*\.jar" || true)
    if [ -z "$PROCESS_CHECK" ]; then
      echo "> 애플리케이션이 정상적으로 종료되었습니다."
      break
    fi
  done
fi

REMAIN_PID=$(pgrep -f "$APP_DIR/.*\.jar" || true)

if [ -n "$REMAIN_PID" ]; then
  echo "> 프로세스가 종료되지 않아 강제 종료합니다. (kill -9 $REMAIN_PID)"
  kill -9 $REMAIN_PID
  sleep 1
fi

echo "> 구버전 JAR 파일 정리"
ls -d $APP_DIR/*.jar | grep -v "$JAR_NAME" | xargs rm -f

echo "> 새 애플리케이션 배포: $JAR_NAME"
chmod +x $JAR_NAME
nohup java -Xmx1024m -Dspring.profiles.active=dev -Duser.timezone=Asia/Seoul -jar $JAR_NAME > $LOG_PATH 2>&1 &

echo "> 배포 상태 확인"
sleep 5

for i in {1..10}; do
  RESPONSE_CODE=$(pgrep -f "$APP_DIR/.*\.jar" || true)

  if [ -n "$RESPONSE_CODE" ]; then
    echo "> 배포 성공! (PID: $RESPONSE_CODE)"
    break
  else
    echo "> 아직 실행 중 ($i/10)"
    sleep 2
  fi

  if [ $i -eq 10 ]; then
    echo "> 오류: 애플리케이션이 20초 내에 실행되지 않았습니다. 로그를 확인하세요."
    echo "> 로그 경로: $LOG_PATH"
    exit 1
  fi
done

echo "> 로그를 확인하려면: tail -f $LOG_PATH"