# Dockerfile para Spring Boot en Fly.io
# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Construir el proyecto
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR desde el stage de build
COPY --from=build /app/target/gestion-jugadores-futbolBase-1.0.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Variables de entorno por defecto (se sobrescriben con fly.toml)
ENV SPRING_PROFILES_ACTIVE=prod

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
