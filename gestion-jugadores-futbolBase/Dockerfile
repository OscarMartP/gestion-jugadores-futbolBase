# Dockerfile para Spring Boot en Fly.io (Optimizado para 256MB RAM)
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

# OPTIMIZACIÓN DE MEMORIA para Fly.io (256MB RAM)
# -Xmx: Heap máximo (128MB deja ~100MB para metaspace y otros)
# -Xms: Heap inicial (igual al máximo para evitar resize)
# -XX:MaxMetaspaceSize: Metaspace máximo
# -XX:+UseSerialGC: GC sencillo, menos overhead
# -Djava.security.egd: Acelera startup
ENTRYPOINT ["java", \
    "-Xmx128m", \
    "-Xms128m", \
    "-XX:MaxMetaspaceSize=64m", \
    "-XX:+UseSerialGC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dserver.address=0.0.0.0", \
    "-jar", \
    "app.jar"]
