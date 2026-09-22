# Original Backend Review — Findings

The supplied ZIP was reviewed before refactoring.

## High-risk issues found

1. No `pom.xml`, Gradle build file, application configuration, or test configuration was included.
2. `db.json` contained plaintext passwords and was unrelated to the Spring Boot persistence layer.
3. JWT validation accepted any parseable token in `validateToken()` without checking expiration/type.
4. JWT signing used platform-default bytes instead of explicit UTF-8.
5. Refresh tokens were stored as plaintext in the database.
6. Refresh tokens were not rotated on refresh.
7. `/entitlements/**` used hardcoded `Long userId = 1L`.
8. Order creation trusted `userId` supplied by the client.
9. Subscription operations trusted `userId` supplied by the client.
10. User profile operations trusted path/body user IDs without ownership enforcement.
11. Resource access/view/download endpoints accepted a client `userId`.
12. Payment verification marked payments successful without cryptographic signature verification.
13. Resource storage logic used a hardcoded bucket name (`zenlearn-pdfs`) instead of the configured R2 bucket.
14. File upload names were constructed from `MultipartFile.getOriginalFilename()`.
15. File upload validation was too weak.
16. `System.out.println()` was used for token/logout diagnostics.
17. Several business errors used generic `RuntimeException`.
18. `open-in-view` was left at its default, which can hide lazy-loading problems and create accidental database access during serialization.
19. User role was EAGER-loaded.
20. Several resource queries used unbounded `findAll()` / list responses.
21. Download/view counters used read-modify-write logic that can lose increments under concurrent requests.
22. Subscription AI credits used a temporary `999999` unlimited value.
23. AI credit consumption was vulnerable to concurrent double-spend.
24. Data initializer contained hardcoded demo passwords.
25. Data initializer relied on hardcoded database IDs for branches/colleges/semesters.
26. Admin resource upload uploaded files before validating all referenced database entities, creating possible orphaned objects.
27. Admin upload accepted an optional slug even though the resource slug column is non-null.
28. Storage image URL extraction assumed a hardcoded `/api` prefix.
29. Subscription direct activation could grant paid access without a completed payment flow.
30. Resource/public responses exposed the internal R2 `fileKey`.

## Refactor decisions

- JWT access/refresh token separation and validation
- Hashed refresh-token persistence + rotation
- Authenticated identity as the source of truth
- Explicit authorization checks
- HMAC payment signature verification
- R2 key validation and upload restrictions
- Centralized exception handling
- Request ID + API timing/status logging
- Transaction boundaries with `open-in-view=false`
- Lazy relationships
- Atomic analytics/AI counters
- Pagination endpoints and query caps
- Environment-driven secrets
- Safer demo-data bootstrap
- Startup diagnostics without secret values
- Maven + test setup for STS import
