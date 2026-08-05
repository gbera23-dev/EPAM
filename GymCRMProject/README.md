# Gym CRM System

---

## Prerequisites

- **Docker** — required on desktop unless you run the services directly from IntelliJ IDEA.
- **Maven + JDK** — only needed if you want to run the module test suites outside of Docker.

---

## Configuration

All environment configuration lives in the **`.env`** file at the project root.
Edit it to switch environments before starting the stack.

---

## Running the application

Bring up the entire application in an isolated Docker environment:

```bash
docker compose up --build
```

### Services

| Service                   | Port          |
| ------------------------- | ------------- |
| Gym CRM System            | `8080`        |
| Trainer History Service   | `8081`        |
| Eureka Discovery          | `8761`        |
| ActiveMQ (broker / UI)    | `61616` / `8161` |

---

## Testing

### Everything at once

A bash script is provided to test the whole application in a single run:

```bash
bash TestAll.sh
```

### End-to-end integration tests

```bash
docker compose -f docker-compose.test.yaml up --build \
  --exit-code-from e2e_tests --attach e2e_tests
```

### Unit + component integration tests

**GymCRMSystem**

```bash
mvn -f GymCRMSystem/pom.xml test
```

**Trainer-history-service**

```bash
mvn -f Trainer-history-service/pom.xml test
```