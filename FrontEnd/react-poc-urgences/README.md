# MedHead Emergency Recommendation Frontend

A professional React + TypeScript front-end for the MedHead PoC project.  
This app recommends hospitals in real-time for emergency interventions, integrating NHS specialities and secure authentication.

---

## ✨ Features

- **Modern UI:** Built with React, Vite, and Material UI (MUI).
- **Authentication:** OAuth2/Keycloak integration (fully mockable for tests).
- **API-Driven:** Communicates with a Java backend (RESTful).
- **Testing:** Comprehensive unit/integration tests (Vitest, React Testing Library), E2E tests (Cypress).

---

## 🛠️ Tech Stack

- **Framework:** React 18 + TypeScript 5
- **Bundler:** Vite 4
- **UI Library:** Material UI (MUI v5)
- **Testing:**
    - Unit/Integration: Vitest, React Testing Library
    - E2E: Cypress (with full API mocking)

---

## 🚀 Getting Started

### **1. Prerequisites**

- Node.js v18+ (recommended LTS)
- npm v9+ or yarn
- (Optional for local dev) Java backend & Keycloak server

---

### **2. Installation**

Clone the repository and install dependencies:

```bash
cd react-poc-urgences
npm install
```

### **3. Start the Development Server**

The app will be running at http://localhost:5173:

```bash
npm run dev
```

---

## Test

### **1. Unit & Integration Tests (Vitest + React Testing Library)**

Run all unit and integration tests:

```bash
npx vitest
```

---

### **2. End-to-End (E2E) Tests (Cypress)**

Open Cypress UI:

```bash
npx cypress open
```

Run Cypress tests in headless mode:

```bash
npx cypress run
```
