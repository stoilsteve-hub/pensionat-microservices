# Pensionat Microservices

A microservices-based booking and management system for a pensionat / guest house. Built with Java 17 and Spring Boot, containerized with Docker, and deployable via Docker Compose or Kubernetes.

## Architecture & Services

The system consists of three independent Spring Boot microservices:

- **booking-service** (Port `8080`): Handles room inventory, availability checks, and booking creation/cancellation. Includes frontend templates.
- **customer-service** (Port `8081`): Manages customer registration, authentication, and profile data.
- **review-service** (Port `8082`): Manages customer reviews and ratings.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3
- **Data & Persistence:** Spring Data JPA, MySQL / H2
- **Containerization & Orchestration:** Docker, Docker Compose, Kubernetes (`k8s/`)
- **Testing:** JUnit 5, Mockito, Testcontainers

## Prerequisites

- JDK 17 or higher
- Maven 3.8+
- Docker & Docker Compose (optional for containerized run)

## Running Locally

### Option 1: Using Docker Compose

Start all microservices and databases simultaneously:

```bash
docker compose up --build
```

### Option 2: Running Services Individually

Build and run each service with Maven:

```bash
# 1. Booking Service
cd booking-service
mvn spring-boot:run

# 2. Customer Service
cd ../customer-service
mvn spring-boot:run

# 3. Review Service
cd ../review-service
mvn spring-boot:run
```

## Running Tests

Run unit tests for any service using Maven:

```bash
# Booking service tests
cd booking-service
mvn test

# RoomService unit tests only
mvn test -Dtest=RoomServiceTest
```

## Kubernetes Deployment

Manifests for Kubernetes deployments, services, and persistent volume claims are located in the `k8s/` directory:

```bash
kubectl apply -f k8s/
```
