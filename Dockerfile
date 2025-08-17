# --- Step 1: Gradle 빌드 단계 ---
FROM gradle:8.4.0-jdk17 AS build

# 프로젝트 소스 복사
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project

# Gradle 빌드 (테스트 제외)
RUN gradle build -x test

# --- Step 2: 실행 단계 ---
FROM openjdk:17
WORKDIR /app

# 빌드 결과물 JAR 복사
COPY --from=build /home/gradle/project/build/libs/app.jar app.jar

# 포트 노출
EXPOSE 8080

# 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
