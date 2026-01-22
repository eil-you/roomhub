# Roomhub 기여 가이드 (Contributing Guide)

안녕하세요! Roomhub 프로젝트에 관심을 가져주셔서 감사합니다. 이 문서는 효율적인 협업과 일관된 코드 품질을 유지하기 위한 가이드라인입니다.

## 🌿 브랜치 전략 (Git-flow)

이 프로젝트는 **Git-flow** 전략을 기본으로 사용합니다.

- **develop**: 프로젝트의 중심 브랜치로, 모든 개발 및 배포 준비가 이곳을 기준으로 진행됩니다.
- **feature/**: 새로운 기능을 개발할 때 사용 (develop에서 분기).
- **hotfix/**: 긴급한 버그 수정을 위해 사용 (develop에서 분기).
- **release/**: 릴리스를 위한 브랜치 (develop에서 분기).

### 브랜치 커스텀 명명 규칙
- `feature/{issue-number}-{feature-name}` (예: `feature/12-user-login`)

## ✍️ 커밋 메시지 컨벤션 (Conventional Commits)

커밋 메시지는 다음 형식을 따릅니다:

```
<type>: <description>
```

### 주요 타입 (Type)
- **feat**: 새로운 기능 추가
- **fix**: 버그 수정
- **docs**: 문서 수정
- **style**: 코드 포맷팅, 세미콜론 누락 등 (코드 변경 없음)
- **refactor**: 코드 리팩토링
- **test**: 테스트 코드 추가/수정
- **chore**: 빌드 업무 수정, 패키지 매니저 설정 등 (프로덕션 코드 변경 없음)

### 예시
- `feat: 구글 로그인 기능 구현`
- `fix: 회원가입 시 중복 검사 로직 오류 수정`

## 🚀 풀 리퀘스트 (Pull Request)

1. `develop` 브랜치를 최신 상태로 유지하세요.
2. 기능 구현 후 `develop` 브랜치로 PR을 생성합니다.
3. PR 템플릿 양식에 맞춰 내용을 작성해주세요.
