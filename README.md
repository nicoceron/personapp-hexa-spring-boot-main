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

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 11 or higher.
- **Apache Maven**: For building the project.
- **Docker**: For running the application and databases in containers.
- **Docker Compose**: For orchestrating multi-container Docker applications.
- **Lombok**: Ensure your IDE is configured to work with Lombok for automatic generation of getters, setters, constructors, etc.

## Getting Started

Follow these steps to get the application up and running:

### 1. Clone the Repository

```bash
git clone <repository-url>
cd personapp-hexa-spring-boot-main
```

### 2. Build the Application

This command will compile the code, run tests (currently skipped in this command for speed), and package the application into JAR files.

```bash
mvn clean package -DskipTests
```

### 3. Run with Docker Compose

This command will build the Docker images (if they don't exist) and start the application along with MariaDB and MongoDB containers in detached mode.

```bash
docker-compose up --build -d
```

This will start the following services:

- `personapp-api`: The Spring Boot application (REST API), accessible on `http://localhost:3000`.
- `personapp-mariadb`: MariaDB instance, with data persisted in a Docker volume. The host port is `3307` (mapped to container port `3306`).
- `personapp-mongodb`: MongoDB instance, with data persisted in a Docker volume. The host port is `27017` (mapped to container port `27017`).

Database initialization scripts (`persona_ddl_maria.sql`, `persona_dml_maria.sql` for MariaDB, and `persona_ddl_mongo.js`, `persona_dml_mongo.js` for MongoDB) are automatically executed when the database containers start for the first time.

### 4. Accessing the Application

- **API Documentation (Swagger UI)**: Once the application is running, you can access the Swagger UI to explore and test the API endpoints by navigating to:
  `http://localhost:3000` (This will automatically redirect to `http://localhost:3000/swagger-ui/index.html`)

  The API documentation can also be found at `http://localhost:3000/api-docs`.

- **Example API Endpoints** (as defined in `PersonaControllerV1`):
  - `GET /api/v1/persona/{database}`: Get all persons from the specified database (`MARIA` or `MONGO`).
    - Example: `curl http://localhost:3000/api/v1/persona/MARIA`
  - `GET /api/v1/persona/{database}/{id}`: Get a specific person by ID from the specified database.
    - Example: `curl http://localhost:3000/api/v1/persona/MARIA/123456789`
  - `POST /api/v1/persona`: Create a new person. The request body should specify the database.
  - `PUT /api/v1/persona/{id}`: Update an existing person. The request body should specify the database.
  - `DELETE /api/v1/persona/{database}/{id}`: Delete a person by ID from the specified database.
  - `GET /api/v1/persona/{database}/count`: Get the count of persons in the specified database.

### Database Credentials (for Docker environment)

These are configured in `docker-application.properties` and `docker-compose.yml`.

- **MariaDB**:

  - Host (within Docker network): `personapp-mariadb`
  - Port (within Docker network): `3306`
  - Database Name: `persona_db`
  - User: `persona_db`
  - Password: `persona_db`
  - Root Password: `root`

- **MongoDB**:
  - Host (within Docker network): `personapp-mongodb`
  - Port (within Docker network): `27017`
  - Database Name: `persona_db`
  - Admin User: `root`
  - Admin Password: `root`
  - Authentication Database: `admin`

### 5. Stopping the Application

To stop all running containers defined in the `docker-compose.yml` file:

```bash
docker-compose down
```

If you also want to remove the Docker volumes (which will delete the database data):

```bash
docker-compose down -v
```

## Project Structure Overview

- `personapp-hexa-spring-boot-main/` (Parent POM)
  - `common/`: Contains common classes, annotations, exceptions, and setup utilities used across different modules.
  - `domain/`: Defines the core domain entities (e.g., `Person`, `Phone`, `Study`, `Profession`, `Gender`). This module has no dependencies on other layers.
  - `application/`: Contains the application logic (use cases) and defines the input and output ports (interfaces) for interacting with the domain.
    - `port/in/`: Interfaces for incoming requests (e.g., `PersonInputPort`).
    - `port/out/`: Interfaces for outgoing data (e.g., `PersonOutputPort` for persistence).
    - `usecase/`: Implementations of the input ports, orchestrating domain logic.
  - `rest-input-adapter/`: Implements the input ports defined in the `application` layer using REST controllers. This is how external clients interact with the application via HTTP.
  - `cli-input-adapter/`: (Placeholder/Example) Implements input ports using a command-line interface.
  - `maria-output-adapter/`: Implements the output ports for data persistence using MariaDB (JPA).
  - `mongo-output-adapter/`: Implements the output ports for data persistence using MongoDB (Spring Data MongoDB).
  - `scripts/`: Contains SQL and JS scripts for database schema creation (DDL) and initial data insertion (DML).
  - `Dockerfile`: Defines how to build the Docker image for the Spring Boot application.
  - `mariadb.Dockerfile`: Defines how to build the Docker image for MariaDB, including initialization scripts.
  - `mongodb.Dockerfile`: Defines how to build the Docker image for MongoDB, including initialization scripts.
  - `docker-compose.yml`: Orchestrates the deployment of the application and its database dependencies.
  - `docker-application.properties`: Spring Boot properties file used specifically when running within the Docker environment (activated by the `docker` profile).

## Contributing

Feel free to fork this repository and submit pull requests.

## License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file for details.
