# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./gradlew build        # Compile and package
./gradlew bootRun      # Run the application
./gradlew test         # Run all tests
./gradlew clean build  # Clean build
```

To run a single test class:
```bash
./gradlew test --tests "com.example.demo.DemoApplicationTests"
```

## Architecture

Spring Boot 4.0.4 application using Java 17. Main package: `com.example.demo`.

**Key dependencies:**
- **Spring Web MVC** — REST endpoints
- **Spring Security** — authentication/authorization
- **MyBatis 4.0.1** — SQL ORM (mapper interfaces + XML/annotation-based SQL)
- **Lombok** — boilerplate reduction (`@Data`, `@Builder`, etc.)

**Notable configuration:** `DataSourceAutoConfiguration` is excluded from `@SpringBootApplication`, meaning the datasource must be configured manually (no auto-detected `spring.datasource.*` setup).

The application is currently a skeleton — no controllers, services, or repositories exist yet. `application.properties` only sets `spring.application.name=demo`.