FROM openjdk:17-jdk

WORKDIR /app

COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/keys /app/keys

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 