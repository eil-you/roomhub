# 🏠 Roomhub (룸허브)

[![Roomhub CI](https://github.com/f-lab-edu/roomhub/actions/workflows/ci.yml/badge.svg)](https://github.com/f-lab-edu/roomhub/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=f-lab-edu_roomhub&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=f-lab-edu_roomhub)

> **Roomhub**는 스프링 부트(Spring Boot) 기반의 마이크로서비스 아키텍처(MSA)를 지향하는 방 예약 및 관리 플랫폼 프로젝트입니다.

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2.2
- **Language**: Java 21
- **Database**: PostgreSQL (予定), Redis (Caching/Token)
- **MSA**: Gradle Multi-module
- **Security**: Spring Security, OAuth2, JWT
- **DevOps**: Docker, GitHub Actions, SonarCloud

## 📂 Project Structure

- `module-api`: API 서비스 모듈 (Auth, User, Room 등)
- `module-common`: 핵심 비즈니스 로직 및 공통 유틸리티
- `module-infra`: 외부 인프라 연동 (Email, Storage 등)

## 🚀 Getting Started

```bash
# 레포지토리 클론
git clone https://github.com/f-lab-edu/roomhub.git

# 빌드 및 실행
./gradlew bootRun
```

## 🤝 Contributing

본 프로젝트의 기여 가이드는 [CONTRIBUTING.md](./CONTRIBUTING.md)를 확인해주세요.
커밋 메시지 규칙 및 브랜치 전략(Git-flow)에 대한 상세한 가이드가 포함되어 있습니다.