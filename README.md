# PersonApp - Hexagonal Architecture with Spring Boot

This project is a demonstration of a Hexagonal (Clean) Architecture implemented using Spring Boot. It manages `Person` entities and their related information like `Phone` numbers and `Study` records, with options to persist data in either MariaDB or MongoDB.

## Features

- **Hexagonal Architecture**: Decouples the core application logic from external concerns like UI, databases, and other services.
- **Spring Boot**: For rapid application development and easy configuration.
- **Multi-module Maven Project**: Organizes the codebase into logical units:
  - `domain`: Core business entities and logic.
  - `application`: Use cases and application ports (interfaces for input/output).
  - `common`: Shared utilities, annotations, and exceptions.
  - `rest-input-adapter`: RESTful API endpoints for interacting with the application (input adapter).
  - `cli-input-adapter`: Command-line interface for interacting with the application (input adapter).
  - `maria-output-adapter`: Persistence layer for MariaDB (output adapter).
  - `mongo-output-adapter`: Persistence layer for MongoDB (output adapter).
- **Dual Database Support**: Demonstrates a flexible persistence layer that can switch between MariaDB and MongoDB.
- **Dockerized**: Comes with `Dockerfile` and `docker-compose.yml` for easy setup and deployment of the application and its database dependencies.
- **Swagger API Documentation**: Integrated Swagger (OpenAPI) for easy API exploration and testing.
- **Automated Tests**: Comprehensive unit tests for CLI adapters with 115+ passing tests.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 11 or higher.
- **Apache Maven**: For building the project.
- **Docker**: For running the application and databases in containers.
- **Docker Compose**: For orchestrating multi-container Docker applications.

## Getting Started

Follow these steps to get the application up and running:

### 1. Clone the Repository

```bash
git clone <repository-url>
cd personapp-hexa-spring-boot-main
```

### 2. Build the Application

This command will compile the code, run tests (currently skipped in this command for speed), install the application modules into your local Maven repository, and package the application.

```bash
mvn clean install -DskipTests
```

### 3. Running the REST API with Docker Compose

This command will build the Docker images (if they don't exist) and start the REST API application along with MariaDB and MongoDB containers in detached mode.

```bash
docker-compose up --build -d
```

This will start the following services:

- `personapp-api`: The Spring Boot application (REST API), accessible on `http://localhost:3000`.
- `personapp-mariadb`: MariaDB instance, with data persisted in a Docker volume. The host port is `3307` (mapped to container port `3306`).
- `personapp-mongodb`: MongoDB instance, with data persisted in a Docker volume. The host port is `27017` (mapped to container port `27017`).

Database initialization scripts for both MariaDB and MongoDB are automatically executed when the database containers start for the first time (or when their volumes are empty).

### 4. Accessing the REST API Application

- **API Documentation (Swagger UI)**: Once the REST API application is running via Docker, you can access the Swagger UI to explore and test the API endpoints by navigating to:
  `http://localhost:3000` (This will automatically redirect to `http://localhost:3000/swagger-ui/index.html`)

  The API documentation can also be found at `http://localhost:3000/api-docs`.

- **Example API Endpoints**:
  - `GET /api/v1/persona/{database}`: Get all persons from the specified database (`MARIA` or `MONGO`).
    - Example: `curl http://localhost:3000/api/v1/persona/MARIA`
  - `GET /api/v1/persona/{database}/{id}`: Get a specific person by ID from the specified database.
    - Example: `curl http://localhost:3000/api/v1/persona/MARIA/123456789`
  - `POST /api/v1/persona`: Create a new person with JSON payload.
    - Example: `curl -X POST http://localhost:3000/api/v1/persona -H "Content-Type: application/json" -d '{"dni": "999900001", "firstName": "RestTest", "lastName": "User", "sex": "MALE", "database": "MONGO"}'`
  - `PUT /api/v1/persona/{id}`: Update an existing person with JSON payload.
    - Example: `curl -X PUT http://localhost:3000/api/v1/persona/999900001 -H "Content-Type: application/json" -d '{"dni": "999900001", "firstName": "RestTest", "lastName": "UserUpdated", "age": "25", "sex": "MALE", "database": "MONGO"}'`
  - `DELETE /api/v1/persona/{database}/{id}`: Delete a person by ID from the specified database.
    - Example: `curl -X DELETE http://localhost:3000/api/v1/persona/MONGO/999900001`
  - `GET /api/v1/persona/{database}/count`: Get the count of persons in the specified database.
    - Example: `curl -X GET http://localhost:3000/api/v1/persona/MONGO/count`

### 5. Running the CLI (Command-Line Interface) Application Locally

The CLI application runs locally and connects to the MariaDB and MongoDB databases that are started via Docker Compose. This setup allows you to interact with the application's core logic through a terminal-based interface.

**Steps to Run the CLI:**

1.  **Ensure Docker Desktop is running.**

2.  **Start Database Containers:**
    Open a terminal in the project root directory (`personapp-hexa-spring-boot-main`) and start the MariaDB and MongoDB database containers in detached mode:

    ```bash
    docker-compose up -d personapp-mariadb personapp-mongodb
    ```

    This command will use the `docker-compose.yml` file to set up `personapp-mariadb` (accessible on host port `3307`) and `personapp-mongodb` (accessible on host port `27017`). Database initialization scripts for both MariaDB and MongoDB are automatically executed when the database containers start for the first time (or when their volumes are empty).

3.  **Build the Application (if not already built):**
    Ensure all project modules, including the CLI adapter, are compiled and packaged:

    ```bash
    mvn clean install -DskipTests
    ```

4.  **Run the CLI Application:**
    Execute the CLI JAR:
    ```bash
    java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
    ```
    This will start the Spring Boot CLI application. It will present a menu in the console, allowing you to choose the database (MariaDB or MongoDB) and perform CRUD operations on Personas, Profesiones, Estudios, and Teléfonos.

**CLI Database Configuration:**

- **MariaDB**: Connects to `jdbc:mariadb://localhost:3307/persona_db` with user `persona_db` and password `persona_db`.
- **MongoDB**: Connects via URI `mongodb://persona_db:persona_db@localhost:27017/persona_db?authSource=admin`.

The CLI application allows you to switch the active persistence mechanism between MariaDB and MongoDB for subsequent operations within each module's menu.

### 6. Automated Tests

The project includes extensive unit tests for the Command-Line Interface (CLI) adapters, focusing on the interaction logic within each adapter. These tests utilize JUnit 5 and Mockito to mock dependencies and verify the behavior of the CLI methods.

**Running CLI Adapter Tests:**

To execute the automated tests for the `cli-input-adapter` module:

```bash
mvn clean test -pl cli-input-adapter
```

This command will compile the test classes and run all JUnit tests. As of the latest updates, all 115+ tests are passing, ensuring the reliability of CLI operations for all entities:

- 29 tests for `PersonaInputAdapterCliTest`
- 19 tests for `ProfesionInputAdapterCliTest`
- 40 tests for `StudyInputAdapterCliTest`
- 27 tests for `TelefonoInputAdapterCliTest`

### 7. Running Both REST API and CLI Simultaneously

One of the key benefits of hexagonal architecture is that multiple input adapters can interact with the same application core:

1. **Start the Docker Environment:**

   ```bash
   docker-compose up -d --build
   ```

   This starts the databases and the REST API.

2. **Run the CLI in a Separate Terminal:**

   ```bash
   java -jar cli-input-adapter/target/cli-input-adapter-0.0.1-SNAPSHOT.jar
   ```

3. **Work with Both Interfaces:**
   - Use the CLI for terminal-based operations
   - Access the REST API via `http://localhost:3000/swagger-ui/index.html`
   - Both interfaces interact with the same databases in real-time

Changes made through one interface will be immediately visible in the other because they share the same underlying databases.

**Using the Convenience Script:**

A convenience script `run-both.sh` is provided to automate the process of starting both interfaces:

```bash
# Make the script executable (first time only)
chmod +x run-both.sh

# Run both interfaces
./run-both.sh
```

This script will:

1. Build the application with Maven
2. Start the Docker containers (REST API and databases)
3. Wait for the REST API to be available
4. Launch the CLI application

When you exit the CLI, the REST API and databases will continue running in Docker. Use `docker-compose down` to stop them when done.

### 8. Stopping the Environment

To stop all running containers defined in the `docker-compose.yml` file (REST API and databases):

```bash
docker-compose down
```

If you also want to remove the Docker volumes (which will delete the database data):

```bash
docker-compose down -v
```

## Architecture Notes

This setup preserves the hexagonal architecture while offering multiple user interfaces:

- The **domain** and **application** modules contain the core business logic
- The **rest-input-adapter** and **cli-input-adapter** are alternative input adapters
- The **maria-output-adapter** and **mongo-output-adapter** are alternative output adapters
- All adapters connect to the same application core through its ports

## Project Structure Overview

- `personapp-hexa-spring-boot-main/` (Parent POM)
  - `common/`: Contains common classes, annotations, exceptions, and setup utilities used across different modules.
  - `domain/`: Defines the core domain entities (e.g., `Person`, `Phone`, `Study`, `Profession`, `Gender`). This module has no dependencies on other layers.
  - `application/`: Contains the application logic (use cases) and defines the input and output ports (interfaces) for interacting with the domain.
    - `port/in/`: Interfaces for incoming requests (e.g., `PersonInputPort`).
    - `port/out/`: Interfaces for outgoing data (e.g., `PersonOutputPort` for persistence).
    - `usecase/`: Implementations of the input ports, orchestrating domain logic.
  - `rest-input-adapter/`: Implements the input ports defined in the `application` layer using REST controllers. This is how external clients interact with the application via HTTP.
  - `cli-input-adapter/`: Implements input ports using a command-line interface. Connects to databases running (likely) in Docker.
  - `maria-output-adapter/`: Implements the output ports for data persistence using MariaDB (JPA).
  - `mongo-output-adapter/`: Implements the output ports for data persistence using MongoDB (Spring Data MongoDB).
  - `scripts/`: Contains SQL and JS scripts for database schema creation (DDL) and initial data insertion (DML).
  - `Dockerfile`: Defines how to build the Docker image for the Spring Boot application (`rest-input-adapter`).
  - `mariadb.Dockerfile`: Defines how to build the Docker image for MariaDB, including initialization scripts.
  - `mongodb.Dockerfile`: Defines how to build the Docker image for MongoDB, including initialization scripts.
  - `docker-compose.yml`: Orchestrates the deployment of the `rest-input-adapter` application and its database dependencies.
  - `docker-application.properties`: Spring Boot properties file used by the `rest-input-adapter` when running within the Docker environment (activated by the `docker` profile).

## Contributing

Feel free to fork this repository and submit pull requests.

## License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file for details.
