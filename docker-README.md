# PersonApp Docker Setup

This directory contains the necessary files to run the PersonApp application in Docker with MariaDB and MongoDB.

## Prerequisites

- Docker
- Docker Compose

## Configuration

The setup includes:

- MariaDB database (port 3307)
- MongoDB database (port 27017)
- PersonApp REST API (port 3000)

## How to Run

1. Build and start all services:

   ```
   docker-compose up -d
   ```

2. To rebuild the services (if you make changes):

   ```
   docker-compose up -d --build
   ```

3. To stop all services:

   ```
   docker-compose down
   ```

4. To view logs for the REST API (useful for debugging):
   ```
   docker logs personapp-api
   ```

## Current Setup

The current setup uses Maven to directly compile and run the Spring Boot application inside Docker:

- The Dockerfile uses a Maven image as the base and runs the application with `mvn spring-boot:run`
- The docker-compose.yml mounts the configuration file to the appropriate location in the container
- MariaDB and MongoDB containers are initialized with the data from the scripts directory

## Accessing the Application

- REST API: http://localhost:3000
- Swagger UI: http://localhost:3000/swagger-ui/index.html
- API Docs: http://localhost:3000/api-docs

## Accessing the Databases

- MariaDB: localhost:3307

  - Username: persona_db
  - Password: persona_db
  - Database: persona_db

- MongoDB: localhost:27017
  - Authentication Database: admin
  - Username: persona_db
  - Password: persona_db
  - Database: persona_db
