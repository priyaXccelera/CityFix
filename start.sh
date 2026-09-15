#!/bin/bash
cd "$(dirname "$0")"
export SERVER_FORWARD_HEADERS_STRATEGY=framework

RESOLVED_EUREKA_URL="http://localhost:8761/eureka"  # SHARED and already running — do not start it here.

# ---------------- user-service ----------------
USER_SERVICE_PORT="${USER_SERVICE_PORT:-25169}"
EXISTING_PID=$(pgrep -f -- "--server.port=$USER_SERVICE_PORT" || true)
if [ -n "$EXISTING_PID" ]; then
  echo "user-service: killing existing PID $EXISTING_PID on port $USER_SERVICE_PORT" >> user-service.log
  kill $EXISTING_PID 2>/dev/null || true
  for i in 1 2 3 4 5 6 7 8 9 10; do
    bash -c "exec 3<>/dev/tcp/127.0.0.1/$USER_SERVICE_PORT" 2>/dev/null || break
    sleep 1
  done
  if bash -c "exec 3<>/dev/tcp/127.0.0.1/$USER_SERVICE_PORT" 2>/dev/null; then
    echo "user-service: still bound after 10s, force killing" >> user-service.log
    kill -9 $EXISTING_PID 2>/dev/null || true
    sleep 1
  fi
fi
nohup java -jar user-service/build/libs/*.jar --server.port=$USER_SERVICE_PORT --eureka.client.service-url.defaultZone=$RESOLVED_EUREKA_URL >> user-service.log 2>&1 &
sleep 5

# ---------------- issue-service ----------------
ISSUE_SERVICE_PORT="${ISSUE_SERVICE_PORT:-21882}"
EXISTING_PID=$(pgrep -f -- "--server.port=$ISSUE_SERVICE_PORT" || true)
if [ -n "$EXISTING_PID" ]; then
  echo "issue-service: killing existing PID $EXISTING_PID on port $ISSUE_SERVICE_PORT" >> issue-service.log
  kill $EXISTING_PID 2>/dev/null || true
  for i in 1 2 3 4 5 6 7 8 9 10; do
    bash -c "exec 3<>/dev/tcp/127.0.0.1/$ISSUE_SERVICE_PORT" 2>/dev/null || break
    sleep 1
  done
  if bash -c "exec 3<>/dev/tcp/127.0.0.1/$ISSUE_SERVICE_PORT" 2>/dev/null; then
    echo "issue-service: still bound after 10s, force killing" >> issue-service.log
    kill -9 $EXISTING_PID 2>/dev/null || true
    sleep 1
  fi
fi
nohup java -jar issue-service/build/libs/*.jar --server.port=$ISSUE_SERVICE_PORT --eureka.client.service-url.defaultZone=$RESOLVED_EUREKA_URL >> issue-service.log 2>&1 &
sleep 5

# ---------------- admin-announcement-service ----------------
ADMIN_ANNOUNCEMENT_SERVICE_PORT="${ADMIN_ANNOUNCEMENT_SERVICE_PORT:-20349}"
EXISTING_PID=$(pgrep -f -- "--server.port=$ADMIN_ANNOUNCEMENT_SERVICE_PORT" || true)
if [ -n "$EXISTING_PID" ]; then
  echo "admin-announcement-service: killing existing PID $EXISTING_PID on port $ADMIN_ANNOUNCEMENT_SERVICE_PORT" >> admin-announcement-service.log
  kill $EXISTING_PID 2>/dev/null || true
  for i in 1 2 3 4 5 6 7 8 9 10; do
    bash -c "exec 3<>/dev/tcp/127.0.0.1/$ADMIN_ANNOUNCEMENT_SERVICE_PORT" 2>/dev/null || break
    sleep 1
  done
  if bash -c "exec 3<>/dev/tcp/127.0.0.1/$ADMIN_ANNOUNCEMENT_SERVICE_PORT" 2>/dev/null; then
    echo "admin-announcement-service: still bound after 10s, force killing" >> admin-announcement-service.log
    kill -9 $EXISTING_PID 2>/dev/null || true
    sleep 1
  fi
fi
nohup java -jar admin-announcement-service/build/libs/*.jar --server.port=$ADMIN_ANNOUNCEMENT_SERVICE_PORT --eureka.client.service-url.defaultZone=$RESOLVED_EUREKA_URL >> admin-announcement-service.log 2>&1 &
sleep 5

# ---------------- gateway-service (last, foreground) ----------------
GATEWAY_PORT="${SERVER_PORT:-27357}"
EXISTING_PID=$(pgrep -f -- "--server.port=$GATEWAY_PORT" || true)
if [ -n "$EXISTING_PID" ]; then
  echo "gateway-service: killing existing PID $EXISTING_PID on port ${GATEWAY_PORT}" >> gateway-service.log
  kill $EXISTING_PID 2>/dev/null || true
  for i in 1 2 3 4 5 6 7 8 9 10; do
    bash -c "exec 3<>/dev/tcp/127.0.0.1/$GATEWAY_PORT" 2>/dev/null || break
    sleep 1
  done
  if bash -c "exec 3<>/dev/tcp/127.0.0.1/$GATEWAY_PORT" 2>/dev/null; then
    echo "gateway-service: still bound after 10s, force killing" >> gateway-service.log
    kill -9 $EXISTING_PID 2>/dev/null || true
    sleep 1
  fi
fi
exec java -jar gateway-service/build/libs/*.jar --server.port=$GATEWAY_PORT --eureka.client.service-url.defaultZone=$RESOLVED_EUREKA_URL
