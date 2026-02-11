# CouchPing 기여 가이드 (Contributing Guide)

CouchPing 프로젝트에 기여해 주셔서 감사합니다. 아래 가이드를 따라 주시기 바랍니다.

## 기여 절차

1. **이슈 생성**: 작업 전 [Issue](https://github.com/f-lab-edu/couchping/issues)를 생성하여 논의해 주세요.
2. **브랜치 생성**: 이슈 번호를 포함한 브랜치를 생성하여 작업해 주세요.
   - 예: `feature/12-user-login`
3. **코드 작성**: [기술 원칙](#기술-원칙)과 [커밋 메시지 규칙](#커밋-메시지-규칙-conventional-commits)을 준수해 주세요.
4. **테스트**: 모든 테스트가 통과하는지 확인해 주세요 (`./gradlew test`).
5. **PR 생성**: [Pull Request Template](.github/pull_request_template.md)을 사용하여 PR을 생성해 주세요.

---

## 브랜치 전략 (Git-flow)

이 프로젝트는 **Git-flow** 전략을 따릅니다.

- **main**: 배포 가능한 상태의 브랜치입니다. `release` 또는 `hotfix` 브랜치에서 병합합니다.
- **release/**: 배포를 위한 준비 브랜치입니다. `develop`에서 분기하며, QA 후 `main`과 `develop`으로 병합합니다.
- **develop**: 다음 배포를 위한 개발 브랜치입니다. 기능 개발이 이루어집니다.
- **feature/**: 새로운 기능을 개발하는 브랜치입니다. `develop`에서 분기하며 완료 후 `develop`으로 병합합니다.
- **hotfix/**: 배포 중인 버전(main)에서 발생한 버그를 긴급 수정하는 브랜치입니다.

---

## 커밋 메시지 규칙 (Conventional Commits)

커밋 메시지는 아래 형식을 따릅니다.

```
<type>: <description> (#issue-number)
```

### 타입 (Type)
- **feat**: 새로운 기능 추가
- **fix**: 버그 수정
- **docs**: 문서 수정 (README.md, API 문서 등)
- **style**: 코드 포맷팅, 세미콜론 누락 등 (비즈니스 로직 변경 없음)
- **refactor**: 코드 리팩토링
- **test**: 테스트 코드 추가/수정
- **chore**: 빌드 업무 수정, 패키지 매니저 수정 등 (소스 코드 변경 없음)

---

## 기술 원칙 (Technical Principles)

CouchPing은 **멀티 모듈** 및 **MSA** 구조를 지향합니다.

### 1. 모듈 구조
- **`module-api`**: 클라이언트 요청을 받는 모듈입니다. Controller, DTO, API 관련 Service가 위치합니다.
- **`module-common`**: 공통으로 사용되는 엔티티, 예외 처리(ErrorCode), 유틸리티 등이 위치합니다.
- **`module-infra`**: 외부 인프라스트럭처(Redis, Email, S3, 외부 API 등) 구현체가 위치합니다.

### 2. 코드 스타일 (Java 21)
- **Modern Java**: Java 21의 기능을 적극 활용합니다 (Record, Pattern Matching, Stream API 등).
- **Style Guide**: 기본적으로 Google Java Style Guide를 따르며, IntelliJ 기본 포맷터를 사용합니다.
- **Lombok**: `@Getter`, `@NoArgsConstructor`, `@Builder` 등을 주로 사용합니다. 무분별한 `@Setter` 사용은 지양합니다.

### 3. 개발 원칙
- **단위 테스트**: 핵심 비즈니스 로직은 반드시 단위 테스트를 작성해야 합니다.
- **계층 분리**: 외부 의존성(DB, API)과 비즈니스 로직은 `module-api`와 `module-infra` 등으로 분리하여 관리합니다.
- **리뷰**: 모든 PR은 CI 통과 및 1명 이상의 리뷰어 승인을 받아야 머지 가능합니다.

---

## 체크리스트
- [ ] `./gradlew build` 실행 시 에러가 발생하지 않는가?
- [ ] 코드 컨벤션을 준수하였는가?
- [ ] 불필요한 `System.out.println`이나 주석이 남아있지 않는가?
- [ ] 의존성 변경 시 적절한 모듈에 추가하였는가?
