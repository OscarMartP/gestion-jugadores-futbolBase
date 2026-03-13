# Dockerfile para Spring Boot en Render.com (Optimizado para 512MB RAM)
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

# Exponer el puerto (Render usa PORT dinámico)
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod

# OPTIMIZACIÓN DE MEMORIA para Render.com (512MB RAM)
# -Xmx: Heap máximo (350MB deja espacio para metaspace y sistema)
# -Xms: Heap inicial
# -XX:MaxMetaspaceSize: Metaspace máximo
# -XX:+UseSerialGC: GC sencillo, menos overhead
# -Djava.security.egd: Acelera startup
ENTRYPOINT ["java", \
    "-Xmx350m", \
    "-Xms350m", \
    "-XX:MaxMetaspaceSize=96m", \
    "-XX:+UseSerialGC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dserver.address=0.0.0.0", \
    "-jar", \
    "app.jar"]
