# Swagger API Test Checklist

Use this order after starting the backend.

## 1. Public catalog
- `GET /universities`
- `GET /academic-years`
- `GET /branches`
- `GET /semesters`
- `GET /subjects`
- `GET /subscription-plans`
- `GET /public/resources/page?page=0&size=20`

Expected: HTTP 200.

## 2. Register
`POST /auth/register`

Example:
```json
{
  "fullName": "Test Student",
  "email": "student@example.com",
  "password": "StrongPassword@123"
}
```

Checkpoint:
- 201 response
- BCrypt password stored
- access token returned
- refresh token returned
- no plaintext password in response

## 3. Authorize Swagger
Click `Authorize` and enter:

```text
Bearer <accessToken>
```

## 4. Current user
`GET /auth/me`

Expected: authenticated user's ID/email/role only.

## 5. Profile security
Try requesting another user's profile.

Expected: 403 unless the authenticated user is ADMIN.

## 6. Resource upload
Admin:
`POST /admin/resources/upload`

Use multipart form data.

Checkpoint:
- file type validated
- 25 MB limit enforced
- metadata validated
- DB references checked before R2 upload
- request log prints requestId/status/duration
- R2 object key is generated server-side

## 7. Public resource pagination
`GET /public/resources/page?page=0&size=20`

Maximum page size enforced by service: 50.

## 8. Order
`POST /api/orders`

```json
{
  "resourceId": 1
}
```

The server overwrites any supplied `userId` with the JWT user ID.

## 9. Payment verification
Set `RAZORPAY_WEBHOOK_SECRET` before testing.

The signature must be:

```text
HMAC_SHA256(providerOrderId + "|" + providerPaymentId, RAZORPAY_WEBHOOK_SECRET)
```

The backend rejects invalid signatures.

## 10. Resource access
- `GET /resources/{id}/access`
- `GET /resources/{id}/view`
- `GET /resources/{id}/download`

The backend derives the user from JWT. No userId parameter is trusted.

## 11. AI credits
`GET /entitlements/ai/access`

`POST /entitlements/ai/consume`

AI credits are decremented atomically to reduce concurrent double-spend.

## 12. Error checkpoints
Verify:
- invalid JWT -> 401
- missing JWT on protected API -> 401
- insufficient role -> 403
- invalid JSON -> 400
- validation failure -> 400
- unknown resource -> 404
- unexpected server exception -> 500 without internal details

## 13. Request tracing
Every request receives:

```text
X-Request-ID
```

The same ID appears in the API log line for correlation.
