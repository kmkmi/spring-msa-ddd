FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/campaign-service-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/keys /app/keys

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"] 