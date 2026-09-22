# EduNest Backend - Project Structure

This project follows the standard Maven/Spring Boot source layout.

```text
edunest-backend/
├── pom.xml
├── README.md
├── .env.example
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/edunest/backend/
│   │   │       ├── EdunestApplication.java
│   │   │       ├── common/
│   │   │       ├── config/
│   │   │       ├── modules/
│   │   │       └── security/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/
│       └── resources/
└── docs...
```

Do not put application Java files directly under `src/main`. Java files belong under `src/main/java` and their directory path must match their package.
