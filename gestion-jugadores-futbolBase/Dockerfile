# Dockerfile para Spring Boot en Koyeb (Optimizado para 512MB RAM)
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

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod

# OPTIMIZACIÓN DE MEMORIA para Koyeb (512MB RAM disponibles)
# -Xmx: Heap máximo (320MB deja ~150MB para metaspace y sistema)
# -Xms: Heap inicial (igual al máximo para evitar resize)
# -XX:MaxMetaspaceSize: Metaspace máximo
# -XX:+UseSerialGC: GC sencillo, menos overhead
# -Djava.security.egd: Acelera startup
ENTRYPOINT ["java", \
    "-Xmx320m", \
    "-Xms320m", \
    "-XX:MaxMetaspaceSize=96m", \
    "-XX:+UseSerialGC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dserver.address=0.0.0.0", \
    "-jar", \
    "app.jar"]
