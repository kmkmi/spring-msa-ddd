FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/data-collector-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085

CMD ["java", "-jar", "app.jar"] 