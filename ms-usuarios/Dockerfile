# ─── Stage 1: build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B -q
COPY src ./src
RUN mvn package -DskipTests -B -q

# ─── Stage 2: runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
EXPOSE 8081
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
