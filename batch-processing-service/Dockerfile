FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/batch-processing-service-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/keys /app/keys

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"] 