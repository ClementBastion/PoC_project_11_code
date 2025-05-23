# Emergency Hospital Allocation

# 📖 Description
This project is a proof of concept (**PoC**) for a real-time hospital bed allocation system based on available medical specialities.  
It is built on a **Spring Boot + React + PostgreSQL** stack, follows a **microservices architecture**, and is production-ready DevOps environments.

---

## ⚙️ Technologies Used
- **Back-end**: Java 17 + Spring Boot 3
- **Front-end**: React + TypeScript + Material UI (MUI)
- **Database**: PostgreSQL + PostGIS + Flyway
- **CI/CD**: GitHub Actions
- **Containerization**: Docker & Docker Compose
- **Testing**:
  - Unit/Integration: JUnit 5, Testcontainers, Vitest, React Testing Library
  - **E2E:** Cypress 
- **Security**: Spring Security, OAuth2, Keycloak

---

## 🐳 **Docker Compose Environment**

This project comes with a rich service stack for local and CI usage:

- **Keycloak** : Identity and access management
- **PostgreSQL + PostGIS**: Main application database 
- **pgAdmin**: Database management UI 
- **Prometheus, Grafana, Loki, Promtail**: Monitoring, dashboards, and logs
- **Zookeeper, Kafka, Kafdrop, kafka-exporter**: Event streaming and monitoring
- **postgres-exporter**: PostgreSQL monitoring for Prometheus


---

# 🚀 Git Workflow – Emergency Hospital Allocation

## 📖 Introduction
This document describes the Git workflow used to manage the project’s development.  
It follows the **GitHub Flow** model, where each feature is developed in a dedicated branch before being merged into `develop`, and eventually into `main`.

---

## 🏗 **Main Branches**
The project relies on three main branches:
- **`main`**: Contains stable, production-ready code.
- **`develop`**: Development branch where all features are merged before being promoted to production.
- **`feature/feature-name`**: Temporary branches used to add new features.
  
### 📌 Commit Message Convention:
- `feat: ...` → New feature
- `fix: ...` → Bug fix
- `refactor: ...` → Code refactoring
- `test: ...` → Adding/modifying tests
- `docs: ...` → Documentation updates

---

# 📌 ***Clone the Project***

```sh
git clone https://github.com/ClementBastion/PoC_project_11_code.git
cd emergency-hospital-allocation
