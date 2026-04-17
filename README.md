# The Elder Forge

A RESTful Spring Boot backend with JWT-based authentication, PostgreSQL, and Flyway migrations.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.13 |
| Security | Spring Security + JJWT 0.13 |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Mapping | MapStruct 1.6.3 |
| Boilerplate | Lombok |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Testcontainers |
| UUID | uuid-creator |

---

## Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL (or Docker for Testcontainers)

---

## Configuration

The application uses Spring profiles. The active profile is set to `dev` by default in `application.yaml`.

**1. Create your dev config file** by copying the example:

```bash
cp src/main/resources/application-dev.yaml.example src/main/resources/application-dev.yaml
```

**2. Fill in the required values:**

```yaml
# application-dev.yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/theelderforge
    username: your_db_username
    password: your_db_password
  security:
    jwt:
      secret: your_secret_key_at_least_32_characters_long
      expiration-access-ms: 1800000       # 30 minutes
      expiration-refresh-ms: 604800000   # 7 days
  cookies:
    secure: false  # false for development only
```

**3. Configure Flyway CLI** by copying the example:

```bash
cp flyway.conf.example flyway.conf
```

---

## Database Migrations

Flyway runs automatically on startup. Migration scripts are located at:

```
src/main/resources/db/migration/
```

To run migrations manually via the Maven plugin:

```bash
mvn flyway:migrate
```

---

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` by default.

**Swagger UI** is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Authentication Flow

The API uses a stateless JWT strategy with two token types:

- **Access Token** — short-lived, sent in the `Authorization: Bearer <token>` header
- **Refresh Token** — longer-lived, stored in an `HttpOnly` cookie named `refreshToken`

### Register a new user

```http
POST /api/v1/users
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepassword"
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

Returns an `accessToken` in the response body. The `refreshToken` is set automatically as an `HttpOnly` cookie.

### Refresh the access token

```http
POST /api/v1/auth/refresh
Cookie: refreshToken=<your_refresh_token>
```

Returns a new `accessToken`. If the refresh token is invalid or expired, the cookie is cleared and `401 Unauthorized` is returned.

### Accessing protected endpoints

```http
GET /api/v1/...
Authorization: Bearer <your_access_token>
```

---

## API Endpoints

| Method | Path                   | Auth Required | Description              |
|--------|------------------------|---------------|--------------------------|
| `POST` | `/api/v1/users`        | ❌             | Register a new user      |
| `POST` | `/api/v1/auth`         | ❌             | Login and obtain tokens  |
| `POST` | `/api/v1/auth/refresh` | ❌ (cookie)    | Refresh the access token |
| `GET`  | `/actuator/**`         | ❌             | Health & metrics         |
| Any    | Any other route        | ✅             | Requires valid JWT       |

---

## Running Tests

Tests use Testcontainers to spin up a real PostgreSQL instance, so Docker must be running.

```bash
mvn test
```

---

## Project Structure

```
src/main/java/com/enelrith/theelderforge/
├── TheelderforgeApplication.java
├── security/                  # JWT filter, service, auth controller & handlers
│   ├── dto/                   # AuthRequest, JwtResponse, AccessJwtResponse
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAccessDeniedHandler.java
│   └── SecurityConfig.java
├── user/                      # User entity, repository, service & controller
│   ├── dto/                   # RegisterUserRequest, UserDto
│   ├── User.java
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   ├── UserMapper.java
│   └── UserDetailsImpl.java
└── shared/
    ├── BaseEntity.java
    ├── ErrorResponse.java
    ├── GlobalExceptionHandler.java
    └── exception/             # NotFoundException, NotValidException, AlreadyExistsException
```

---

## Error Responses

All errors follow a consistent `ErrorResponse` structure:

```json
{
  "timestamp": "2026-04-17T10:00:00.000Z",
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

The `validationErrors` field is `null` for non-validation errors.