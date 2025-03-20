# Emergency Hospital Allocation

## 📖 Description
Ce projet est une preuve de concept (**PoC**) pour un système d'allocation de lits d'hôpital en temps réel en fonction des spécialités médicales disponibles. Il repose sur une architecture **Spring Boot + React + PostgreSQL** et s'inscrit dans une architecture **microservices**.

---

## ⚙️ Technologies utilisées
- **Back-end** : Java 17 + Spring Boot 3
- **Front-end** : React
- **Base de données** : PostgreSQL + Flyway
- **CI/CD** : GitHub Actions 
- **Conteneurisation** : Docker & Docker Compose
- **Tests** : JUnit 5 + RestAssured + Testcontainers
- **Sécurité** : Spring Security

---
# 🚀 Workflow Git – Emergency Hospital Allocation

## 📖 Introduction
Ce document décrit le workflow Git utilisé pour gérer le développement du projet. Il suit le modèle **GitHub Flow**, où chaque fonctionnalité est développée dans une branche dédiée avant d'être fusionnée dans `develop`, puis en `main`.

---

## 🏗 **Branches principales**
Le projet repose sur trois branches principales :
- **`main`** : Contient le code stable et prêt pour la production.
- **`develop`** : Branche de développement où toutes les fonctionnalités sont fusionnées avant de passer en production.
- **`feature/nom-fonctionnalité`** : Branches temporaires utilisées pour ajouter de nouvelles fonctionnalités.
  
### 📌 Convention des messages de commit :
- feat: … → Nouvelle fonctionnalité
- fix: … → Correction de bug
- refactor: … → Refactorisation de code
- test: … → Ajout/modification de tests
- docs: … → Mise à jour de la documentation

---

# 📌 ***Cloner le projet***

```sh
git clone https://github.com/ClementBastion/PoC_project_11_code.git
cd emergency-hospital-allocation
