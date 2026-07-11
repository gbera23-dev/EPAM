FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

# ---- Runtime stage ----------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]