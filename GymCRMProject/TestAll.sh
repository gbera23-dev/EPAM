#!/usr/bin/env bash

# GymCRMSystem component integration and unit tests
mvn -f GymCRMSystem/pom.xml test

# Trainer-history-service component integration and unit tests
mvn -f Trainer-history-service/pom.xml test

# End-to-end integration tests
docker compose -f docker-compose.test.yaml up --build --exit-code-from e2e_tests --attach e2e_tests

echo "All tests were successful!"
