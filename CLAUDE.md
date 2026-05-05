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

---

## 작업 워크플로우 (Claude + Codex 협업)

사용자가 기능 구현을 요청하면 아래 순서로 진행한다.

### 1단계 — Claude 검토
- 요청을 분석해 구현 방향, 영향 받는 파일, 주의사항을 정리한다.
- 아키텍처 패턴(Controller → Service → Mapper → XML)을 준수하는지 확인한다.

### 2단계 — Codex 검토 (gpt-5.5)
- Claude가 정리한 접근법을 Codex에게 검토시킨다.
- 명령 형식:
  ```bash
  codex -m gpt-5.5 review <대상 파일 또는 --stdin으로 코드 전달>
  ```
- Codex의 피드백을 반영해 구현 계획을 확정한다.

### 3단계 — Codex 코딩 (o3-mini → 사용자 지정 시 변경)
- 확정된 계획을 Codex에게 코딩 지시한다.
- 명령 형식:
  ```bash
  codex -m codex-mini-latest --dangerously-bypass-approvals-and-sandbox exec "<상세 구현 지시>"
  ```
- 구현 후 반드시 `./gradlew compileJava`로 컴파일 성공 확인.
- 실패 시 에러를 분석해 수정한다.

### 모델 설정
| 용도 | 모델 |
|------|------|
| 검토 (review) | `gpt-5.5` |
| 코딩 (exec) | `codex-mini-latest` |

> 사용자가 "codex 5.3"으로 코딩 모델을 지정했으나, OpenAI 공식 모델명 확인 필요.
> 현재는 `codex-mini-latest` 사용. 변경 시 위 표를 업데이트한다.

### 주의사항
- Codex exec는 `--dangerously-bypass-approvals-and-sandbox` 없이는 파일 수정 불가.
- 기존 기능을 깨지 않도록 payday 미설정 시 기존 동작 100% 유지 등 하위 호환성 필수.
- MyBatis XML에서 부등호는 반드시 `&lt;` `&gt;` 로 escape.
