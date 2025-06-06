# Dockerfile (백엔드 프로젝트 루트에)
FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar # <--- Gradle 사용 시 이 경로로 변경
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080