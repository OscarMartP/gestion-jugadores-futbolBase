# Multi-stage build para optimizar el tamaño de la imagen

# Etapa 1: Build con Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copiar archivos de Maven para cachear dependencias
COPY gestion-jugadores-futbolBase/pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY gestion-jugadores-futbolBase/src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime con JRE ligero
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR compilado desde la etapa builder
COPY --from=builder /app/target/gestion-jugadores-futbolBase-1.0.jar app.jar

# Exponer puerto 8080 (Render lo mapeará automáticamente)
EXPOSE 8080

# Variables de entorno por defecto (se sobrescriben en Render)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
