FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ad-recommendation-service-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/keys /app/keys

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"] 