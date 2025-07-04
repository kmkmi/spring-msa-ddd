FROM openjdk:17-jdk

WORKDIR /app

COPY target/jwks-service-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/keys /app/keys

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"] 