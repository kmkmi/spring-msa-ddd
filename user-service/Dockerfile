FROM openjdk:17-jdk

WORKDIR /app

COPY target/user-service-0.0.1-SNAPSHOT.jar app.jar
COPY ./src/main/resources/keys /app/keys

# Install curl for health check debugging (yum 기반)
RUN yum install -y curl || true

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"] 