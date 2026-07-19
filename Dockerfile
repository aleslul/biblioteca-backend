# --- Etapa 1: compilar el proyecto con Maven ---
FROM maven:3.9-eclipse-temurin-26 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# --- Etapa 2: imagen final, solo con el jar ya compilado ---
FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render inyecta la variable PORT; le decimos a Spring que la use
ENV PORT=8081
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]