# API Error Monitoring System

A distributed API error monitoring system built with **Spring Boot, Apache Kafka, MySQL, Docker, and Docker Compose**.

The system captures API errors, publishes structured error events to Kafka, processes them asynchronously, stores them in MySQL, and exposes REST APIs for monitoring and analysis.

## Architecture

```text
                         Client
                           |
                           v
                +-----------------------+
                |  API Error Monitor    |
                |  Spring Boot :8081    |
                +-----------+-----------+
                            |
                     Error Event
                            |
                            v
                +-----------------------+
                |        Kafka          |
                |     api-errors        |
                +-----------+-----------+
                            |
                            v
                +-----------------------+
                |   Error Processor     |
                |  Spring Boot :8082    |
                +-----------+-----------+
                            |
                            v
                     +-------------+
                     |    MySQL    |
                     | error_logs  |
                     +------+------+
                            |
                            v
                  Monitoring REST APIs
````

## Features

* Global exception handling using `@RestControllerAdvice`
* Structured API error events
* Kafka-based asynchronous error processing
* Kafka retry mechanism
* Dead Letter Topic (DLT) handling
* MySQL persistence using Spring Data JPA
* Error filtering by:

    * ID
    * HTTP status code
    * Error type
    * Timestamp range
* Error statistics API
* Pagination support
* Dockerized Spring Boot applications
* Docker Compose support
* Unit, controller, and repository tests

## Technology Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Kafka
* Spring Data JPA
* Apache Kafka
* MySQL
* Maven
* Docker
* Docker Compose
* JUnit
* Mockito

## Project Structure

```text
api-error-monitoring-system/
│
├── docker-compose.yml
├── .env
├── .gitignore
│
├── api-error-monitor/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
└── error-processor/
    ├── Dockerfile
    ├── pom.xml
    └── src/
```

### API Error Monitor

Responsible for:

* Receiving API requests
* Handling exceptions
* Creating `ErrorEvent` objects
* Publishing error events to Kafka

### Error Processor

Responsible for:

* Consuming error events from Kafka
* Processing retryable failures
* Handling DLT messages
* Persisting errors to MySQL
* Providing monitoring APIs

## Error Event

The services exchange structured error events containing:

```json
{
  "timestamp": "2026-09-24T17:58:02",
  "serviceName": "api-error-monitor",
  "endpoint": "/api/orders/500",
  "httpMethod": "GET",
  "statusCode": 500,
  "errorType": "RuntimeException",
  "message": "Unexpected error while processing order",
  "requestId": "request-123"
}
```

## Kafka Flow

The main Kafka topic is:

```text
api-errors
```

The processing flow is:

```text
api-errors
     |
     v
Error Processor
     |
     +---- Success ----> MySQL
     |
     +---- Failure ----> api-errors-retry
                              |
                              v
                         Retry processing
                              |
                              +---- Success ----> MySQL
                              |
                              +---- Failure ----> api-errors-dlt
```

The project uses `@RetryableTopic` with:

* 4 total attempts
* 1 second retry delay

The DLT is used for messages that continue to fail after the configured retry attempts.

## API Endpoints

### API Error Monitor

#### Get Order

```http
GET /api/orders/{id}
```

Example:

```http
GET /api/orders/123
```

Response:

```text
Order 123
```

### Trigger 404 Error

```http
GET /api/orders/999
```

Response:

```text
Order with id 999 not found
```

### Trigger 500 Error

```http
GET /api/orders/500
```

Response:

```text
Internal server error
```

## Monitoring APIs

Base URL:

```text
http://localhost:8082/api/errors
```

### Get All Errors

```http
GET /api/errors
```

Supports pagination through Spring's `Pageable`.

Example:

```http
GET /api/errors?page=0&size=10
```

### Get Error by ID

```http
GET /api/errors/{id}
```

Example:

```http
GET /api/errors/1
```

### Filter by Status Code

```http
GET /api/errors/status/{statusCode}
```

Example:

```http
GET /api/errors/status/500
```

### Filter by Error Type

```http
GET /api/errors/type/{errorType}
```

Example:

```http
GET /api/errors/type/RuntimeException
```

### Filter by Timestamp

```http
GET /api/errors/filter?from={from}&to={to}
```

Example:

```http
GET /api/errors/filter?from=2026-09-24T00:00:00&to=2026-09-24T23:59:59
```

### Error Statistics

```http
GET /api/errors/stats
```

Example response:

```json
{
  "totalErrors": 8,
  "errorsByStatus": {
    "400": 0,
    "404": 5,
    "500": 3
  }
}
```

## Running Locally

### Prerequisites

Make sure the following are installed:

* Java 21
* Docker Desktop
* MySQL
* Apache Kafka

### Create MySQL Database

Create the database:

```sql
CREATE DATABASE error_monitor;
```

Configure the MySQL credentials in your local environment.

### Kafka

The application expects Kafka to be available at:

```text
localhost:9092
```

When running the Spring Boot applications inside Docker, Kafka is accessed through:

```text
host.docker.internal:29092
```

### Build the Applications

For `api-error-monitor`:

```powershell
.\mvnw.cmd clean package -DskipTests
```

For `error-processor`:

```powershell
.\mvnw.cmd clean package -DskipTests
```

### Build Docker Images

```powershell
docker build -t api-error-monitor:1.0 ./api-error-monitor
```

```powershell
docker build -t error-processor:1.0 ./error-processor
```

### Run with Docker Compose

Create a `.env` file:

```properties
MYSQL_PASSWORD=your_mysql_password
```

Then:

```powershell
docker compose up -d
```

Check running services:

```powershell
docker compose ps
```

View logs:

```powershell
docker compose logs
```

Stop the services:

```powershell
docker compose down
```

## Testing

The project contains tests for:

* Service layer
* REST controllers
* Kafka producer
* Kafka consumer
* Repository queries
* Exception handling
* Error filtering
* Error statistics

Run tests using:

```powershell
.\mvnw.cmd test
```

Run tests separately for each service from its project directory.

## Docker Communication

When the Spring Boot applications run directly on Windows:

```text
localhost:9092
localhost:3306
```

When the applications run inside Docker:

```text
host.docker.internal:29092
host.docker.internal:3306
```

This allows the Dockerized applications to communicate with Kafka and MySQL running on the host machine.

## Key Design Decisions

### Asynchronous Error Processing

Kafka decouples the API from error persistence.

The API does not need to directly communicate with MySQL when an error occurs.

```text
API
 |
 v
Kafka
 |
 v
Processor
 |
 v
MySQL
```

This allows error processing to happen asynchronously.

### Retry and DLT

Temporary processing failures can be retried automatically.

Messages that continue to fail are moved to the Dead Letter Topic instead of being continuously retried.

### Request ID

Each captured error contains a `requestId`, which can be used to correlate an error event with the originating API request.

### Separation of Responsibilities

The project uses two Spring Boot applications:

* `api-error-monitor` — API/error event producer
* `error-processor` — Kafka consumer/database/monitoring service

This keeps API handling and error processing separate.

## Future Improvements

Possible future enhancements:

* Use `Instant` or `LocalDateTime` instead of storing timestamps as strings
* Add structured logging with SLF4J
* Add authentication and authorization to monitoring APIs
* Add Kafka and MySQL to Docker Compose
* Add a monitoring dashboard
* Add metrics using Micrometer and Prometheus
* Add Grafana visualization
* Add centralized correlation IDs across services
* Add CI/CD pipeline
* Add integration tests using Testcontainers

## Author

Built as a backend engineering project to demonstrate:

**Java + Spring Boot + Kafka + Microservices + MySQL + Docker**
