# Emergency Project - Initialization Guide

## 1. Start the Application

Use Docker Compose to build and start the env in detached mode:

```bash
docker compose -f compose_dev.yml up --build -d
```

Start java app:

```bash
make dev-run
```

## 2. Authenticate with Keycloak

To interact with the secured API, you need to retrieve a valid access token from Keycloak.

### a. Access the backend container:

```bash
docker exec -it emergency-back sh
```

## 3. Use Postman to Test the API

### a. Open and import the Postman collection:

Located at:
```
/Postman Test
```

### b. Set your token in Postman:

To set the “Authorization” header for the Bearer token for all API calls.
In the Postman collection, call:

```
Register&SaveToken
```

## 4. Trigger Initial Data Synchronization

To initialize the system with necessary data:

### a. In the Postman collection **`HospitalImportController`**, call:

```
triggerImport
```

**Note:** This process takes about **5 minutes**.

### b. In the Postman collection **`SpecialityController`**, call:

```
importSpecialitiesAndAssignToHospitals
```

**Note:** This process takes about **1 minute**.

## 5. Locate the Nearest Hospital with Available Beds for a Given Speciality

### a. In **`SpecialityController`**, call:

```
getAllSpecialities
```

This returns the list of speciality IDs.

### b. In **`HospitalLocatorController`**, call:

```
findNearestHospital Random
```

This simulates a search based on fake coordinates to find the nearest hospital with availability for the given speciality.

---

Make sure to keep your token updated and follow the call order to ensure data is properly initialized before performing advanced queries.