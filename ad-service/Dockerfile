FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ad-service-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/keys /app/keys

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"] 