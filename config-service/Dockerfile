FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl
WORKDIR /app
COPY target/config-service-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/config /app/config
CMD ["java", "-jar", "app.jar"] 