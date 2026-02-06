#!/bin/bash

APP_DIR="/home/ubuntu/app"
LOG_PATH="$APP_DIR/app.log"

echo "> 현재 시간: $(date)"

JAR_NAME=$(ls -tr "$APP_DIR"/*.jar | tail -n 1)

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

echo "> PID로 실행 중인 프로세스 확인"
CURRENT_PID=$(pgrep -f "java -jar")

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> 실행 중인 애플리케이션 종료 (PID: $CURRENT_PID)"
  kill -15 $CURRENT_PID

  for i in {1..15}; do
    sleep 1
    PROCESS_CHECK=$(pgrep -f "java -jar")
    if [ -z "$PROCESS_CHECK" ]; then
      echo "> 애플리케이션이 정상적으로 종료되었습니다."
      break
    fi
  done
fi

echo "> 포트(8080) 점유 프로세스 확인 및 강제 종료"
fuser -k -n tcp 8080 || true
sleep 1

echo "> 구버전 JAR 파일 정리"
ls -d "$APP_DIR"/*.jar | grep -v "$JAR_NAME" | xargs rm -f

echo "> DB 상태 확인"
DB_STATUS=$(docker inspect -f '{{.State.Status}}' postgres-dev 2>/dev/null || echo "not_found")
if [ "$DB_STATUS" != "running" ]; then
    echo "> [ERROR] DB가 실행 중이 아닙니다. (상태: $DB_STATUS)"
    docker logs postgres-dev --tail 20
    exit 1
fi
sleep 10

echo "> 새 애플리케이션 배포: $JAR_NAME"

chmod +x "$JAR_NAME"
nohup java -Xmx512m -Dspring.profiles.active=dev -Duser.timezone=Asia/Seoul -jar "$JAR_NAME" > "$LOG_PATH" 2>&1 &

echo "> 배포 상태 확인"
sleep 10

for i in {1..20}; do
  RESPONSE_CODE=$(pgrep -f "java -jar")

  if [ -n "$RESPONSE_CODE" ]; then
    if grep -q "Started .* in .* seconds" "$LOG_PATH"; then
      echo "> 배포 성공! (PID: $RESPONSE_CODE)"
      exit 0
    fi

    if grep -iq "Application run failed" "$LOG_PATH" || grep -iq "Failed to start" "$LOG_PATH"; then
      echo "> [ERROR] 로그에서 실행 에러가 발견되었습니다."
      tail -n 20 "$LOG_PATH"
      exit 1
    fi

    echo "> 아직 실행 중... ($i/20)"
    sleep 3
  else
    echo "> 오류: 프로세스가 중간에 사라졌습니다."
    tail -n 30 "$LOG_PATH"
    exit 1
  fi

  if [ $i -eq 20 ]; then
    echo "> 오류: 애플리케이션이 약 70초 내에 실행되지 않았습니다. 로그를 확인하세요."
    exit 1
  fi
done

echo "> 로그를 확인하려면: tail -f $LOG_PATH"