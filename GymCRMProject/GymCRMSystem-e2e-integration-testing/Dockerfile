FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN ./mvnw -B dependency:go-offline

COPY src src

ENTRYPOINT ["./mvnw", "-B", "test"]