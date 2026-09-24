# Order Fulfillment Platform

A production-style **Java Spring Boot microservices backend** for managing users, products, inventory, and orders.

## 🏗️ Architecture

```text
                    ┌─────────────────┐
                    │   User Service  │
                    │     :8081       │
                    └────────┬────────┘
                             │ REST
                             ▼
┌──────────────┐     ┌─────────────────┐
│    Client    │────►│  Order Service  │
└──────────────┘     │     :8083       │
                     └────────┬────────┘
                              │ REST
                              ▼
                     ┌─────────────────┐
                     │ Product Service │
                     │     :8082       │
                     └─────────────────┘
```

Each service has its own PostgreSQL database.

```text
order-fulfillment-platform/
├── user-service/
├── product-service/
├── order-service/
├── build.gradle
├── settings.gradle
└── README.md
```

## 🧩 Services

### User Service — `:8081`

* User CRUD
* Validation
* Exception handling
* PostgreSQL persistence

### Product Service — `:8082`

* Product CRUD
* Price and stock management
* Validation
* Exception handling
* PostgreSQL persistence

### Order Service — `:8083`

* Create and retrieve orders
* User/Product validation
* Stock validation
* Order total calculation
* Order status management
* Product price snapshot
* PostgreSQL persistence

## 🔗 Order Flow

```text
Create Order
     │
     ├──► Validate User
     │
     ├──► Validate Products
     │
     ├──► Check Stock
     │
     ├──► Calculate Total
     │
     └──► Save Order
```

Order Service communicates with User and Product Services through REST APIs using Spring `RestClient`.

##  Main APIs

| Service | Method | Endpoint                       |
| ------- | ------ | ------------------------------ |
| User    | POST   | `/api/v1/users`                |
| User    | GET    | `/api/v1/users/{id}`           |
| Product | POST   | `/api/v1/products`             |
| Product | GET    | `/api/v1/products/{id}`        |
| Order   | POST   | `/api/v1/orders`               |
| Order   | GET    | `/api/v1/orders/{id}`          |
| Order   | GET    | `/api/v1/orders/user/{userId}` |

## 🛠️ Tech Stack

* Java 17
* Spring Boot
* Spring Data JPA / Hibernate
* REST APIs
* PostgreSQL
* Gradle
* Git / GitHub
* JUnit
* Postman
* Spring Actuator

##  Current Testing

Tested scenarios include:

* User and product creation
* Successful order creation
* Invalid user/product
* Insufficient stock
* Order retrieval
* Service-to-service communication
* Gradle builds

Health check:

```text
GET http://localhost:8083/actuator/health
```

##  Next Steps

* [ ] Swagger / OpenAPI
* [ ] Unit & integration testing
* [ ] Testcontainers
* [ ] Docker
* [ ] Kafka
* [ ] API Gateway
* [ ] Authentication & authorization
* [ ] Prometheus & Grafana
* [ ] OpenTelemetry
* [ ] Centralized logging
* [ ] CI/CD
* [ ] SonarQube
* [ ] Kubernetes
* [ ] AWS deployment

##  Goal

Build a realistic backend system demonstrating **Java development, microservices, production support, observability, testing, and deployment practices** used in modern engineering teams.
