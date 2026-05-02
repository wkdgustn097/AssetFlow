# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**자산 관리 웹**

## Project Structure

```
AssetFlow/
├── src/
│   ├── main/
│   │   ├── java/com/assetflow/
│   │   │   ├── AssetFlowApplication.java   # 메인 진입점
│   │   │   ├── controller/                 # MVC 컨트롤러
│   │   │   └── service/                    # 비즈니스 로직
│   │   ├── resources/
│   │   │   └── application.properties
│   │   └── webapp/WEB-INF/views/           # JSP 파일
│   └── test/
│       └── java/com/assetflow/
├── build.gradle
└── settings.gradle
```

## Commands

```bash
./gradlew build        # Compile and package
./gradlew bootRun      # Run the application
./gradlew test         # Run all tests
./gradlew clean build  # Clean build
```

To run a single test class:
```bash
./gradlew test --tests "com.assetflow.AssetFlowApplicationTests"
```

## Architecture

Spring Boot application using Java 17. Main package: `com.assetflow`.

**Key dependencies:**
- **Spring Web MVC** — MVC 컨트롤러 + JSP 뷰
- **Spring Security** — authentication/authorization
- **MyBatis** — SQL ORM (mapper interfaces + XML/annotation-based SQL)
- **Lombok** — boilerplate reduction (`@Data`, `@Builder`, etc.)
- **Tomcat Jasper** — JSP 렌더링

**Notable configuration:** `DataSourceAutoConfiguration` is excluded from `@SpringBootApplication`, meaning the datasource must be configured manually.
