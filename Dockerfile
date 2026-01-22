# 1단계: 빌드 스테이지 (Gradle + JDK 17)
FROM gradle:8-jdk21 AS build

ARG SERVICE_PATH
ARG JAR_PATH
WORKDIR /home/gradle/app

# 소스 코드 복사
COPY --chown=gradle:gradle . .

# 해당 모듈만 빌드하여 실행 가능한 JAR 생성
RUN gradle ${SERVICE_PATH}:bootJar --no-daemon

# 빌드된 JAR 중 plain이 아닌 것을 찾아 app.jar로 미리 복사 (Manifest 에러 방지)
RUN cp ${JAR_PATH}/build/libs/*[!plain].jar /app.jar


# 2단계: 실행
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 헬스 체크를 위한 curl 설치 (Ubuntu 기반)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 빌드 스테이지에서 준비된 app.jar만 복사
COPY --from=build /app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
