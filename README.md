# app

Multi-module Java + Spring Boot + Spring Cloud microservices project.

- **groupId**: `com.example`
- **Modules**:
- **eureka-server** (EUREKA_SERVER) — http://localhost:24357
- **gateway-service** (GATEWAY) — http://localhost:27357
- **admin-announcement-service** (BUSINESS_SERVICE) — http://localhost:20349
- **issue-service** (BUSINESS_SERVICE) — http://localhost:21882
- **user-service** (BUSINESS_SERVICE) — http://localhost:25169

## Build

```bash
./gradlew build -x test
```

## Run (in separate terminals, in this order)

```bash
./gradlew :eureka-server:bootRun   # port 24357
./gradlew :admin-announcement-service:bootRun   # port 20349
./gradlew :issue-service:bootRun   # port 21882
./gradlew :user-service:bootRun   # port 25169
./gradlew :gateway-service:bootRun   # port 27357
```

## Access the API

**Call the services through the gateway — that's the intended entry point,
not each service's own port.** The gateway (gateway-service, port 27357)
discovers every registered service via Eureka and routes to it by lower-cased
service name:

- **admin-announcement-service**: `http://localhost:27357/admin-announcement-service/api/v1/...`
- **issue-service**: `http://localhost:27357/issue-service/api/v1/...`
- **user-service**: `http://localhost:27357/user-service/api/v1/...`

- **Aggregated Swagger docs**: `http://localhost:27357/docs`

Every business service's own `http://localhost:<port>` listed above under
Modules is reachable directly too (and Docker Compose publishes it), but
that's there for local debugging one module in isolation — a real client, or
anything calling more than one service, should go through the gateway so
routing, discovery and any cross-cutting gateway config stay in one place.
