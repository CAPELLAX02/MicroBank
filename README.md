# MicroBank

## Introduction

### Overview of MicroBank
MicroBank is a modern, scalable, and secure banking backend system built using microservice architecture. It enables account management, financial transactions, document generation, and notification services while ensuring robust authentication and authorization mechanisms through Keycloak.

### Why Microservices?
Microservices provide modularization, independent scalability, and better maintainability. Each service in MicroBank is responsible for a specific business capability, allowing independent development and deployment.

### Project Architecture Diagram
<img src="./diagrams/microbank-diagram.png">

### Key Features
- **Microservice Architecture**: Independent services for authentication, account management, transactions, documents, and notifications.
- **Event-Driven Communication**: RabbitMQ for asynchronous messaging between services.
- **Secure Authentication & Authorization**: Keycloak OAuth2 integration.
- **Scalable Storage Solutions**: PostgreSQL, Redis, and MinIO.
- **Containerized Deployment**: Docker and Docker Compose for environment consistency.

## Getting Started

### Prerequisites
Before running MicroBank, ensure you have the following installed:
- **Java 17** or higher
- **Maven (latest version)**
- **Docker & Docker Compose**
- **Postman (optional, for API testing)**

### Running the Project
MicroBank services must be started in a specific order:

1. **Start infrastructure services (databases, Keycloak, Redis, RabbitMQ, MinIO)**:
   ```sh
   docker-compose up -d
   ```
2. **Start Discovery Service first (to enable service registration)**:
   ```sh
   mvn spring-boot:run -f discovery/pom.xml
   ```
3. **Start remaining microservices (auth, account, transaction, document, notification, and API gateway)**
   ```sh
   mvn spring-boot:run -f auth/pom.xml
   mvn spring-boot:run -f account/pom.xml
   mvn spring-boot:run -f transaction/pom.xml
   mvn spring-boot:run -f document/pom.xml
   mvn spring-boot:run -f notification/pom.xml
   mvn spring-boot:run -f apigateway/pom.xml
   ```

### Postman Collection
A **Postman Collection** is provided in the `./postman` directory to facilitate API testing.
1. Import `MicroBank API Collection.postman_collection.json` into Postman.
2. Set environment variables like `baseURL = http://localhost:8123/api/v1`.
3. Run API requests seamlessly.

### Keycloak Configuration
Keycloak authentication is pre-configured in the project with a realm export file (`./keycloak-config/realm-export.json`).
- Access Keycloak Admin UI: [http://localhost:9098](http://localhost:9098)
- Default admin credentials: `admin / admin`
- **Important:** After Keycloak starts, manually reset `client-secret` values in **Clients → microbank-client & admin-cli** and update them in `./auth/src/main/resources/application.yml`.

## Project Architecture

### Microservices Overview
The backend consists of multiple independent microservices:

- **API Gateway**: Routes all requests, handles authentication.
- **Auth Service**: Manages user authentication and token handling.
- **Account Service**: Manages bank accounts and balances.
- **Transaction Service**: Handles money transfers between accounts.
- **Document Service**: Generates and stores transaction receipts.
- **Notification Service**: Sends email notifications for important events.
- **Discovery Service**: Service registry using Eureka for dynamic service discovery.

### Service Communication
- **Synchronous Communication**: OpenFeign is used for inter-service REST API calls.
- **Asynchronous Communication**: RabbitMQ queues for event-driven messaging.

### Database & Storage Solutions
- **PostgreSQL**: Persistent storage for Auth, Account, Transaction, and Document services.
- **Redis**: Temporary storage for authentication-related data (password reset, activation codes).
- **MinIO**: Object storage for transaction receipts.

## Database Access
To access the PostgreSQL databases and perform administrative tasks, you can use the following commands:

**Auth Database (auth_db)**
```shell
psql -U auth_db -d auth_db -h localhost -p 5431
```
(Default `auth_db` password: **auth_db**)

**Account Database (account_db)**
```shell
psql -U account_db -d account_db -h localhost -p 5430
```
(Default `account_db` password: **account_db**)

**Transaction Database (transaction_db)**
```shell
psql -U transaction_db -d transaction_db -h localhost -p 5433
```
(Default `transaction_db` password: **transaction_db**)

**Document Database (document_db)**
```shell
psql -U document_db -d document_db -h localhost -p 5434
```
(Default `document_db` password: **document_db**)

## Identity & Access Management

### Keycloak Integration
- **OAuth2 & OpenID Connect**: Secure authentication and token-based authorization.
- **Role-Based Access Control (RBAC)**: User roles (`USER`, `ADMIN`) managed by Keycloak.

### Authentication & Authorization Flow
1. **User Registration & Activation**
    - Users register via `/api/v1/auth/register`.
    - Activation code is sent via email.
    - User activates the account via `/api/v1/auth/activate`.
2. **Login & Token Handling**
    - Users log in via `/api/v1/auth/login`, obtaining an **access token** and **refresh token**.
    - Token validation occurs at the **API Gateway**.
3. **API Gateway (AuthN) & Microservices (AuthZ)**
    - API Gateway validates tokens and forwards requests.
    - Microservices enforce authorization using Spring Security.

## Endpoint Examples
All API endpoints are structured under `http://localhost:8123/api/v1/`.

| Service       | Endpoint Example | Description |
|--------------|----------------|-------------|
| **Auth** | `/auth/login` | User authentication |
| **Auth** | `/auth/register` | User registration |
| **Account** | `/accounts` | Get user accounts |
| **Transaction** | `/transactions` | Create and list transactions |
| **Document** | `/documents/{transactionId}` | Retrieve transaction documents |
| **Notification** | Internal | Listens to RabbitMQ events |

### Standardized API Response
All API responses follow a uniform structure:
```json
{
  "status": 200,
  "message": "Success message",
  "data": {},
  "timestamp": "2025-02-01T12:00:00.000Z",
  "errors": null
}
```

## Service Order & Dependencies
1. **Start infrastructure** (databases, Keycloak, Redis, RabbitMQ, MinIO) **first**
2. **Start Discovery Service**
3. **Start Microservices**
4. **Start API Gateway last**

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository.
2. Create a feature branch.
3. Submit a pull request.

For any questions, open an issue or reach out to the project maintainers.
