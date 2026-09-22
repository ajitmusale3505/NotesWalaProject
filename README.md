# EduNest Backend — Industry-Ready Baseline

EduNest is a study-material marketplace/API for question papers, solved papers, notes, manuals, books, paid/free resources, subscriptions, entitlements, downloads, analytics and future AI/chat features.

## What was changed

### Security
- JWT access + refresh-token authentication.
- Refresh tokens are stored as SHA-256 hashes, not plaintext.
- Refresh-token rotation on refresh/login reduces replay risk.
- Access vs refresh JWT types are separated.
- JWT expiry/signature validation is performed before authentication.
- Client-supplied `userId` is never trusted for "my account" operations.
- Order, subscription, profile and entitlement flows use the authenticated JWT identity.
- Admin endpoints require `ROLE_ADMIN`.
- Contributor/admin file upload authorization is explicit.
- Storage keys are validated to prevent path/key traversal.
- Upload type and size validation is enforced.
- Internal storage keys are hidden from JSON responses.
- Payment verification uses constant-time HMAC-SHA256 signature comparison.
- Secrets and demo passwords are environment-driven.
- No plaintext passwords are shipped in the project.
- CORS is configurable instead of wildcard.
- Security response handling returns 401/403 JSON.
- Sensitive headers/body data are intentionally not logged.

### Architecture / maintainability
- Maven project added so STS can import it as a Spring Boot/Maven project.
- Centralized configuration in `application.yml`.
- Centralized exception handling.
- Request ID + API duration/status logging using SLF4J.
- `open-in-view=false` and service-level transactions.
- Lazy user/role relationship instead of eager loading.
- Database indexes added for common resource/catalog queries.
- Resource entity now has audit timestamps through `BaseEntity`.
- Removed hardcoded R2 bucket usage from resource business logic.
- Removed the old `db.json` plaintext-password database.
- Added a Spring Boot context smoke test.

## Main modules

- `auth` — registration, login, refresh, logout, current user
- `userprofile` — academic profile
- `university`, `college`, `collegebranch`, `branch`, `year`, `semester`, `subject` — academic catalog
- `category` — resource categories
- `resource` — study-material lifecycle and access
- `storage` — Cloudflare R2 file storage
- `order` — resource purchases
- `payment` — payment verification
- `subscription` — plans and subscriptions
- `entitlement` / `resourceentitlement` — access control
- `resourceanalytics` — resource analytics
- `feed` — resource feed
- `dashboard` — dashboard data
- `admin` — administrative flows
- `bootstrap` — bootstrap APIs

## STS import

1. Extract the ZIP.
2. In Spring Tool Suite: `File -> Import -> Maven -> Existing Maven Projects`.
3. Select the extracted `backend` folder.
4. Make sure Java 17+ is configured.
5. Update Maven project.
6. Run `EdunestApplication.java` as `Spring Boot App`.

## PostgreSQL

Create a database:

```sql
CREATE DATABASE edunest;
```

Environment variables:

```text
DB_URL=jdbc:postgresql://localhost:5432/edunest
DB_USERNAME=postgres
DB_PASSWORD=your_password

JWT_SECRET=generate-a-long-random-secret-at-least-32-bytes
JWT_ACCESS_EXPIRATION_MS=900000
JWT_REFRESH_EXPIRATION_MS=604800000

CORS_ALLOWED_ORIGINS=http://localhost:5173

R2_ACCESS_KEY=...
R2_SECRET_KEY=...
R2_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
R2_BUCKET=edunest

RAZORPAY_WEBHOOK_SECRET=...
```

For the current development baseline, `JPA_DDL_AUTO=update` is the default so the API can start without manually writing the complete initial schema.

For production, switch to a reviewed Flyway migration set and use:

```text
JPA_DDL_AUTO=validate
```

Do not use `update` in production.

## Demo users

Demo-user creation is disabled by default.

To intentionally create local demo users:

```text
SEED_DEMO_DATA=true
SEED_ADMIN_PASSWORD=<12+ character password>
SEED_USER_PASSWORD=<12+ character password>
SEED_CONTRIBUTOR_PASSWORD=<12+ character password>
```

Never commit these values to Git.

## Swagger

After startup:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`

Flow:

1. Register or log in.
2. Copy the returned access token.
3. Click `Authorize` in Swagger.
4. Enter `Bearer <access-token>`.
5. Call protected APIs.

## API working checkpoints

The backend now logs lines similar to:

```text
api_request requestId=... method=POST path=/auth/login status=200 durationMs=...
api_request requestId=... method=GET path=/public/resources status=200 durationMs=...
```

Use `X-Request-ID` to correlate frontend/API errors.

## Important production checkpoints

Before production, verify:

- PostgreSQL backups and restore process
- Flyway migration history
- JWT secret stored in a secrets manager
- R2 credentials stored outside source control
- Razorpay webhook/signature verification and webhook idempotency
- Payment webhook reconciliation
- Order/payment idempotency
- File virus scanning and PDF content validation
- Rate limiting for authentication and expensive endpoints
- Redis/cache strategy
- CDN strategy for public images/previews
- Search engine/index strategy for resource search
- Audit trail for admin actions
- Soft delete/versioning for paid resources
- Copyright/licensing policy for uploaded books/PDFs
- Object lifecycle/retention rules in R2
- Monitoring, alerting and centralized logs
- Automated integration and security tests
- Database constraints and reviewed indexes
- Backup/restore testing

## Known design boundary

This ZIP is a hardened backend baseline built from the code supplied in the original project. Payment gateway order creation/webhooks, AI provider integration, chat infrastructure, Redis rate limiting and production Flyway migrations still need provider-specific configuration/implementation because the supplied code did not contain complete implementations for those external systems.

