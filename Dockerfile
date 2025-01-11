FROM openjdk:17-jdk-slim AS builder
WORKDIR /opt/app
COPY gradlew settings.gradle build.gradle ./
COPY gradle /opt/app/gradle
COPY ./src ./src
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

FROM openjdk:17-jdk-slim
WORKDIR /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]