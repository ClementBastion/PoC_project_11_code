# Emergency Project - Initialization Guide

## 1. Start the Application

Use Docker Compose to build and start the env in detached mode:

```bash
docker compose -f compose_dev.yml up --build -d
```

Start java app:

```bash
make stress-run
```

## 2. Locust

To run stress test with locust.

### a. Run locust:

```bash
make locust
```

Open locust in web browser:

```bash
http://127.0.0.1:8089/
```

### b. Run Stress Test:
Config test:

```bash
Start new load test:
Number of users = 1000
Ramp up = 50
```