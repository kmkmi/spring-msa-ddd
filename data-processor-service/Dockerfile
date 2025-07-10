FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/data-processor-service-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/keys /app/keys

EXPOSE 8086

CMD ["java", "-jar", "app.jar"] 