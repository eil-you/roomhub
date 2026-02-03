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

- `module-api`: API 서비스 모듈 (User, Reservation 등)
- `module-common`: 핵심 비즈니스 로직, 공용 예외 처리 및 공통 모델
- `module-infra`: 외부 인프라 연동 (Discovery, Gateway)

## ✨ Key Features & Architectural Decisions

### 1. MSA (Microservice Architecture)
- **Service Discovery (Eureka)**: 각 서비스의 위치를 동적으로 관리하여 확장성을 확보했습니다.
- **API Gateway**: 단일 진입점을 통해 라우팅 및 횡단 관심사(인증/인가)를 처리합니다.

### 2. 분산 락(Distributed Lock)을 통한 정합성 보장
- **Redisson 기반 구현**: `reservation-service`에서 동시 예약 요청 시 발생할 수 있는 데이터 정합성 문제(Overbooking)를 해결하기 위해 Redis 분산 락을 도입했습니다.
- **TryLock 패턴**: 락 획득 대기 시간과 만료 시간을 설정하여 데드락(Deadlock)을 방지하고 시스템 안정성을 높였습니다.

### 3. 멀티 모듈 아키텍처 (Multi-Module)
- 계층 간 의존성을 엄격히 격리하기 위해 `module-common`에 공용 도메인을 최소화하고, 각 서비스가 독립적으로 독립적인 DB와 로직을 갖도록 설계했습니다.

### 4. 도커라이징(Dockerizing)
- `docker-compose`를 통해 DB(MySQL), Redis, Discovery Service 및 각 Microservice를 한 번에 기동할 수 있는 환경을 구축했습니다.

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