FROM openjdk:11-jre-slim

WORKDIR /app

COPY rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar app.jar
COPY docker-application.properties application.properties

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:///app/application.properties"] 