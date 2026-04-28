# The Elder Forge Backend

A Spring Boot REST API for creating, importing, managing, and sharing primarily Skyrim SE modlists.

The backend provides session-based authentication, PostgreSQL persistence, Flyway migrations, public/private modlist visibility, MO2 file imports, mod/plugin metadata linking, and public browsing endpoints for the Next.js frontend.

---

## Tech Stack

| Layer       | Technology                         |
|-------------|------------------------------------|
| Language    | Java 21                            |
| Framework   | Spring Boot 3.5.13                 |
| Security    | Spring Security sessions + CSRF    |
| Persistence | Spring Data JPA + PostgreSQL       |
| Migrations  | Flyway                             |
| Mapping     | MapStruct 1.6.3                    |
| Boilerplate | Lombok                             |
| API Docs    | SpringDoc OpenAPI / Swagger UI     |
| Testing     | JUnit 5 + Mockito + Testcontainers |
| UUID        | uuid-creator                       |

---

## Prerequisites

- Java 21+
- Maven Wrapper included in this repo
- PostgreSQL for local development
- Docker for the full Testcontainers-backed test suite

---

## Configuration

The default active profile is `dev`, configured in `src/main/resources/application.yaml`.

Create your local development config from the example:

```powershell
Copy-Item src/main/resources/application-dev.yaml.example src/main/resources/application-dev.yaml
```

Fill in the local database credentials:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/theelderforge
    username: your_db_username
    password: your_db_password
server:
  servlet:
    session:
      timeout: 30m
      cookie:
        http-only: true
        same-site: lax
        secure: false

app:
  security:
    allowed-origins:
      - http://localhost:3000
```

Optional Flyway CLI config can be created from:

```powershell
Copy-Item flyway.conf.example flyway.conf
```

---

## Database

Flyway runs automatically on application startup. Migration scripts live in:

```text
src/main/resources/db/migration/
```

To run migrations manually:

```powershell
.\mvnw.cmd flyway:migrate
```

The current schema includes users, modlists, categories, mods, and plugins

---

## Running Locally

Start PostgreSQL, then run:

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts on:

```text
http://localhost:8080
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

Health and actuator endpoints are available under:

```text
http://localhost:8080/actuator
```

---

## Authentication

The API uses Spring Security sessions. Login creates a server-side session and returns a `JSESSIONID` cookie. Mutating requests should include the CSRF token, except `POST /api/v1/auth` and `POST /api/v1/users`, which are intentionally excluded for login/register.

### Get CSRF Token

```http
GET /api/v1/auth/csrf
```

Response:

```json
{
  "headerName": "X-XSRF-TOKEN",
  "parameterName": "_csrf",
  "token": "csrf-token"
}
```

Browser clients also receive an `XSRF-TOKEN` cookie. Send the token in the `X-XSRF-TOKEN` header for protected writes.

### Register

```http
POST /api/v1/users
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepassword",
  "username": "username"
}
```

Response:

```json
{
  "id": "uuid",
  "createdAt": "2026-04-28T00:00:00Z",
  "email": "user@example.com",
  "username": "username"
}
```

### Login

```http
POST /api/v1/auth
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepassword"
}
```

Response:

```json
{
  "email": "user@example.com",
  "username": "username"
}
```

### Current Session

```http
GET /api/v1/auth
Cookie: JSESSIONID=<session>
```

Returns the authenticated user's email and username.

### Logout

```http
POST /api/v1/auth/logout
Cookie: JSESSIONID=<session>
X-XSRF-TOKEN: <csrf-token>
```

Returns `204 No Content`.

---

## Modlist API

| Method   | Path                                 | Auth         | Description                                                             |
|----------|--------------------------------------|--------------|-------------------------------------------------------------------------|
| `GET`    | `/api/v1/modlists?page=&name=`       | Public       | Browse public modlists with pagination and name search.                 |
| `GET`    | `/api/v1/modlists/{id}`              | Public/owner | Fetch a public modlist, or a private modlist owned by the session user. |
| `GET`    | `/api/v1/modlists/{id}/mods?name=`   | Public/owner | Search mods within a modlist.                                           |
| `GET`    | `/api/v1/modlists/user`              | Required     | Fetch the current user's modlists.                                      |
| `POST`   | `/api/v1/modlists`                   | Required     | Create a modlist.                                                       |
| `PATCH`  | `/api/v1/modlists/{id}/visibility`   | Required     | Toggle public/private visibility.                                       |
| `DELETE` | `/api/v1/modlists/{id}`              | Required     | Delete an owned modlist.                                                |
| `POST`   | `/api/v1/modlists/{id}/mods/file`    | Required     | Import `modlist.txt` from MO2.                                          |
| `POST`   | `/api/v1/modlists/{id}/plugins/file` | Required     | Import `loadorder.txt` from MO2.                                        |
| `POST`   | `/api/v1/modlists/{id}/meta`         | Required     | Import generated `mod_data.txt` metadata.                               |

### Create Modlist

```http
POST /api/v1/modlists
Content-Type: application/json
Cookie: JSESSIONID=<session>
X-XSRF-TOKEN: <csrf-token>

{
  "name": "My Skyrim Setup",
  "description": "Survival-focused load order.",
  "isPublic": false
}
```

### Update Visibility

```http
PATCH /api/v1/modlists/{id}/visibility
Content-Type: application/json
Cookie: JSESSIONID=<session>
X-XSRF-TOKEN: <csrf-token>

{
  "isPublic": true
}
```

### Upload MO2 Files

The upload endpoints accept `multipart/form-data` and require exact file names:

| Endpoint        | Form field      | Required file name |
|-----------------|-----------------|--------------------|
| `/mods/file`    | `modlistFile`   | `modlist.txt`      |
| `/plugins/file` | `loadOrderFile` | `loadorder.txt`    |
| `/meta`         | `modDataFile`   | `mod_data.txt`     |

---

## Error Responses

Errors use a shared `ErrorResponse` shape:

```json
{
  "timestamp": "2026-04-28T00:00:00.000Z",
  "status": 400,
  "message": "A validation error has occurred in one or more fields",
  "error": "Bad Request",
  "path": "/api/v1/users",
  "validationErrors": {
    "email": "Must be a valid email address",
    "password": "Password must be between 8 and 72 characters"
  }
}
```

`validationErrors` is `null` for non-validation errors.

---

## Project Structure

```text
src/main/java/com/enelrith/theelderforge/
  TheelderforgeApplication.java
  security/       Session auth, CSRF, CORS, login/logout endpoints
  user/           User entity, username/email registration, user DTOs
  modlist/        Modlist, mod, plugin, category entities and import logic
  shared/         Base entity, error response, exceptions, global handler

src/main/resources/
  application.yaml
  application-dev.yaml.example
  db/migration/   Flyway migrations
```
