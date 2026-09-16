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

## Roles and account creation

CityFix supports exactly three roles: `USER`, `ADMIN`, and `SUPER_ADMIN`.

- A single `SUPER_ADMIN` is pre-seeded by `user-service` when its database is initialized. The
  seed is idempotent, so subsequent application starts preserve that same account rather than
  creating another one.
- Public registration always persists `role: USER`; the registration payload has no role field.
- `SUPER_ADMIN` is the only role allowed to create `ADMIN` accounts. There is no endpoint or
  public workflow to create a `SUPER_ADMIN`, which preserves the single pre-seeded
  `SUPER_ADMIN` account.
- All protected routes enforce authorization in the backend from the JWT role. `ADMIN` routes
  also allow `SUPER_ADMIN`; `SUPER_ADMIN-ONLY` routes reject both `ADMIN` and `USER` with
  `403 Forbidden`.

### Create Admin — SUPER_ADMIN-ONLY

`POST /user-service/api/v1/users/admins`

Requires an `Authorization: Bearer <super-admin-jwt>` header. A `USER` or regular `ADMIN` token
receives `403 Forbidden`; requests without a valid token are denied. The endpoint always creates
an account with `role: ADMIN` and cannot create a `SUPER_ADMIN`.

Request:

```json
{
  "name": "New Administrator",
  "email": "new.admin@cityfix.com",
  "password": "Password123"
}
```

Response (`201 Created`):

```json
{
  "id": 6,
  "name": "New Administrator",
  "email": "new.admin@cityfix.com",
  "role": "ADMIN",
  "address": null,
  "phone": null,
  "active": true
}
```

### Login role response

`POST /user-service/api/v1/auth/login` is the single login flow for all roles. Its successful
response includes `user.role` with the exact value `USER`, `ADMIN`, or `SUPER_ADMIN`, allowing
the frontend to direct the account to the appropriate experience.

## API authorization

All API routes below are prefixed by the service gateway path described above. `USER` means a
valid authenticated account (and is also usable by elevated roles); `ADMIN` means `ADMIN` or
`SUPER_ADMIN`.

| Service | Endpoint | Access |
| --- | --- | --- |
| User | `POST /api/v1/auth/register` | **PUBLIC** — always creates `USER` |
| User | `POST /api/v1/auth/login` | **PUBLIC** |
| User | `POST /api/v1/users/admins` | **SUPER_ADMIN-ONLY** |
| User | `GET /api/v1/users` | **ADMIN** |
| User | `GET /api/v1/users/{id}` | **ADMIN** |
| User | `PUT /api/v1/users/{id}/deactivate` | **ADMIN** for `USER` targets; **SUPER_ADMIN-ONLY** for `ADMIN` targets; `SUPER_ADMIN` targets cannot be deactivated |
| Issue | `POST /api/v1/departments` | **ADMIN** |
| Issue | `PUT /api/v1/departments/{id}` | **ADMIN** |
| Issue | `DELETE /api/v1/departments/{id}` | **ADMIN** |
| Issue | `GET /api/v1/departments`, `GET /api/v1/departments/{id}` | **USER** |
| Issue | `POST /api/v1/categories` | **ADMIN** |
| Issue | `PUT /api/v1/categories/{id}` | **ADMIN** |
| Issue | `DELETE /api/v1/categories/{id}` | **ADMIN** |
| Issue | `GET /api/v1/categories`, `GET /api/v1/categories/{id}` | **USER** |
| Issue | `POST /api/v1/issues` | **USER** |
| Issue | `GET /api/v1/issues`, `GET /api/v1/issues/{id}`, `GET /api/v1/issues/my` | **USER** |
| Issue | `PUT /api/v1/issues/{id}/assign`, `PUT /api/v1/issues/{id}/status`, `PUT /api/v1/issues/{id}/priority` | **ADMIN** |
| Issue | `GET /api/v1/issues/analytics` | **ADMIN** |
| Issue | `POST /api/v1/issues/{issueId}/comments`, `GET /api/v1/issues/{issueId}/comments` | **USER** |
| Issue | `POST /api/v1/issues/{issueId}/upvotes` | **USER** |
| Announcement | `POST /api/v1/announcements`, `PUT /api/v1/announcements/{id}`, `DELETE /api/v1/announcements/{id}` | **ADMIN** |
| Announcement | `GET /api/v1/announcements`, `GET /api/v1/announcements/{id}` | **USER** |
| Announcement | `GET /api/v1/analytics` | **ADMIN** |
