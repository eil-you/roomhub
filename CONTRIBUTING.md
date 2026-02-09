# Roomhub 기여 가이드 (Contributing Guide)

안녕하세요! Roomhub 프로젝트에 관심을 가져주셔서 감사합니다. 이 문서는 일관된 코드 품질을 유지하고 효율적인 협업을 위한 기술적 가이드라인을 담고 있습니다.

## 🚀 기여 프로세스

1. **이슈 생성**: 새로운 작업(기능 추가, 버그 수정 등)을 시작하기 전 [Issue](https://github.com/f-lab-edu/roomhub/issues)를 먼저 생성합니다.
2. **브랜치 생성**: 생성된 이슈 번호를 포함하여 브랜치를 생성합니다.
   - 예: `feature/12-user-login`
3. **코드 작성**: 아래의 [개발 원칙](#-개발-원칙)을 준수하며 코드를 작성합니다.
4. **테스트**: 로컬에서 모든 테스트가 통과하는지 확인합니다. (`./gradlew test`)
5. **PR 생성**: [Pull Request Template](.github/pull_request_template.md)에 맞춰 내용을 작성합니다.

---

## 🌿 브랜치 전략 (Git-flow)

이 프로젝트는 표준 **Git-flow** 전략을 지향합니다.

- **main**: 실제 서비스가 운영되는 브랜치입니다. `release` 또는 `hotfix` 브랜치에서만 병합됩니다.
- **release/**: 새로운 버전을 배포하기 위한 준비 브랜치입니다. `develop`에서 분기하며, 최종 QA와 버전 태깅 후 `main`과 `develop`에 병합됩니다.
- **develop**: 다음 버전을 위한 통합 개발 브랜치입니다. 모든 기능 개발의 기준점이 됩니다.
- **feature/**: 새로운 기능 개발을 위해 사용됩니다. `develop`에서 분기하며 작업 완료 후 다시 `develop`으로 병합됩니다.
- **hotfix/**: 운영 중인 서비스(main)에서 발생한 긴급한 버그 수정을 위해 사용됩니다.

---

## ✍️ 커밋 메시지 컨벤션 (Conventional Commits)

커밋 메시지는 다음 형식을 따르며, 한글 작성을 권장합니다.

```
<type>: <description> (#issue-number)
```

### 주요 타입 (Type)
- **feat**: 새로운 기능 추가
- **fix**: 버그 수정
- **docs**: 문서 수정 (README.md, API 문서 등)
- **style**: 코드 포맷팅, 세미콜론 누락 등 (로직 변경 없음)
- **refactor**: 코드 리팩토링
- **test**: 테스트 코드 추가/수정
- **chore**: 빌드 업무 수정, 패키지 매니저 설정 등 (프로덕션 코드 변경 없음)

---

## 🛠 개발 원칙 (Technical Principles)

Roomhub는 **멀티 모듈 구조**와 **MSA**를 지향합니다. 각 모듈의 역할에 맞는 코드를 작성해 주세요.

### 1. 모듈별 역할
- **`module-api`**: 외부 요청을 받는 진입점입니다. Controller, DTO, API 전용 Service가 위치합니다.
- **`module-common`**: 전역적으로 사용되는 공통 도메인 엔티티, 예외 처리(ErrorCode), 유틸리티 클래스가 위치합니다.
- **`module-infra`**: 외부 시스템 연동(Redis, Email, S3, 외부 API 호출 등)을 담당하는 기술적인 구현체가 위치합니다.

### 2. 코드 스타일 (Java 21)
- **Modern Java**: Java 21의 기능을 적극 활용합니다. (Record, Pattern Matching, Stream API 등)
- **Style Guide**: 기본적으로 Google Java Style Guide를 따르며, IntelliJ의 기본 포맷터를 활용합니다.
- **Lombok**: `@Getter`, `@NoArgsConstructor`, `@Builder` 등을 활용하여 가독성을 높입니다. 단, 불변성 유지를 위해 `@Setter` 사용은 지양합니다.

### 3. 테스트 원칙
- **단위 테스트**: 비즈니스 로직이 포함된 클래스는 반드시 대응하는 단위 테스트를 작성해야 합니다.
- **통합 테스트**: 외부 환경(DB, API) 연동 확인이 필요한 경우 `module-api`에서 통합 테스트를 작성합니다.
- **검증**: 모든 PR은 CI 환경에서 빌드 및 테스트가 통과되어야 머지될 수 있습니다.

---

## ✅ 체크리스트
- [ ] `./gradlew build` 시 에러가 발생하지 않는가?
- [ ] 새로운 기능에 대한 테스트 코드를 작성했는가?
- [ ] 불필요한 `System.out.println`이나 주석이 포함되어 있지 않가?
- [ ] 변경 사항이 이슈의 범위와 일치하는가?
