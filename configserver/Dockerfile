# config-server/Dockerfile
FROM gradle:8.12.1-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon

FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl

# GitHub Credentials
ARG GIT_USERNAME
ARG GIT_PW

ENV GIT_USERNAME=${GIT_USERNAME}
ENV GIT_PW=${GIT_PW}

VOLUME /tmp
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8071
ENTRYPOINT ["java", "-jar", "/app.jar"]