@echo off
cd /d "%~dp0"
set SERVER_FORWARD_HEADERS_STRATEGY=framework
set RESOLVED_EUREKA_URL=http://localhost:8761/eureka

echo Starting user-service on 25169...
start /B java -jar user-service\build\libs\user-service.jar --server.port=25169 --eureka.client.service-url.defaultZone=%RESOLVED_EUREKA_URL% >> user-service.log 2>&1
timeout /t 5 /nobreak > nul

echo Starting issue-service on 21882...
start /B java -jar issue-service\build\libs\issue-service.jar --server.port=21882 --eureka.client.service-url.defaultZone=%RESOLVED_EUREKA_URL% >> issue-service.log 2>&1
timeout /t 5 /nobreak > nul

echo Starting admin-announcement-service on 20349...
start /B java -jar admin-announcement-service\build\libs\admin-announcement-service.jar --server.port=20349 --eureka.client.service-url.defaultZone=%RESOLVED_EUREKA_URL% >> admin-announcement-service.log 2>&1
timeout /t 5 /nobreak > nul

echo Starting gateway-service on 27357 (foreground)...
java -jar gateway-service\build\libs\gateway-service.jar --server.port=27357 --eureka.client.service-url.defaultZone=%RESOLVED_EUREKA_URL%
