# Build stage
FROM maven:3.9-amazoncorretto-11-alpine AS build

WORKDIR /app

# Copy POM files first for better caching
COPY pom.xml .
COPY common/pom.xml common/
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY maria-output-adapter/pom.xml maria-output-adapter/
COPY mongo-output-adapter/pom.xml mongo-output-adapter/
COPY rest-input-adapter/pom.xml rest-input-adapter/
COPY cli-input-adapter/pom.xml cli-input-adapter/

# Download dependencies (cached if pom files don't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY common/src common/src
COPY domain/src domain/src
COPY application/src application/src
COPY maria-output-adapter/src maria-output-adapter/src
COPY mongo-output-adapter/src mongo-output-adapter/src
COPY rest-input-adapter/src rest-input-adapter/src
COPY cli-input-adapter/src cli-input-adapter/src

# Build the project
RUN mvn clean install -DskipTests

# Runtime stage
FROM amazoncorretto:11-alpine

WORKDIR /app

# Copy the REST application JAR from the build stage
COPY --from=build /app/rest-input-adapter/target/rest-input-adapter-0.0.1-SNAPSHOT.jar app.jar
COPY docker-application.properties application.properties

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "-Dspring.config.location=application.properties"] 