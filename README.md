# ai-powered-ecommerce-platform
AI-powered E-Commerce platform built using Spring Boot, PostgreSQL, Microservices, Kafka, Docker, Kubernetes, AWS, Spring Security, JWT, Redis, and Spring AI. This project is being developed incrementally to learn and implement modern backend development concepts and cloud-native architecture.

              Enterprise E-Commerce Architecture
              
                        API Gateway
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
   User Service        Product Service        Cart Service
        │                     │                     │
        └──────────────┬──────┴──────────────┬──────┘
                       │                     │
                 Order Service         Inventory Service
                       │                     │
                       ├──────────────┬──────┘
                       │              │
                Payment Service   Notification Service
                       │
                  Search Service
