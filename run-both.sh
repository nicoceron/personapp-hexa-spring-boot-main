#!/bin/bash

echo "Starting PersonApp in dual-interface mode (REST API + CLI)..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
  echo "Error: Docker is not running. Please start Docker and try again."
  exit 1
fi

# Ensure the application is built
echo "Building the application..."
mvn clean install -DskipTests

# Start the Docker containers
echo "Starting Docker containers (REST API and databases)..."
docker-compose down
docker-compose up --build -d

# Wait for the application to start
echo "Waiting for the REST API to start (this may take a moment)..."
sleep 10

# Check if REST API is up
if curl -s http://localhost:3000/api-docs > /dev/null; then
  echo "REST API is up and running at http://localhost:3000"
  echo "Swagger UI available at http://localhost:3000/swagger-ui/index.html"
else
  echo "Warning: REST API may not be fully started yet. Check 'docker logs personapp-api'"
fi

# Start the CLI
echo "\nStarting CLI application..."
echo "=================================="
echo "When you exit the CLI, the REST API will continue running."
echo "Use 'docker-compose down' to stop all containers when done."
echo "==================================\n"

# Run the CLI
java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar 