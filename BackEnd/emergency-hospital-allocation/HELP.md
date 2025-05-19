# Emergency Project - Initialization Guide

## 1. Start the Application with Docker

Use Docker Compose to build and start the application in detached mode:

```bash
docker compose up --build -d
```

## 2. Authenticate with Keycloak

To interact with the secured API, you need to retrieve a valid access token from Keycloak.

### a. Access the backend container:

```bash
docker exec -it emergency_back sh
```

### b. Retrieve an access token via `curl`:

```bash
curl -X POST http://keycloak:8080/realms/medhead/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=emergency-app" \
  -d "client_secret=5VgmTTTjjvoyx19TErAOSxtcke0VbcbC" \
  -d "grant_type=password" \
  -d "username=dr-john" \
  -d "password=test123"
```

Copy the value of the `access_token` from the JSON response.

## 3. Use Postman to Test the API

### a. Open and import the Postman collection:

Located at:
```
/Postman Test
```

### b. Set your token in Postman:

Use the `access_token` from the previous step and set it in the "Authorization" header as a Bearer token for all API calls.

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