# 🔐 Configuración de Variables de Entorno

Este archivo contiene plantillas para configurar las variables de entorno necesarias para el proyecto.

## ⚠️ IMPORTANTE
**NUNCA subas este archivo con valores reales a Git si contiene API keys o credenciales reales.**

---

## 🧠 Variables de Inteligencia Artificial

### OPENAI_API_KEY
Clave de API de OpenAI para funcionalidad de análisis con IA.

**Obtener clave:**
1. Regístrate en https://platform.openai.com
2. Ve a https://platform.openai.com/api-keys
3. Genera una nueva API key
4. Cópiala (solo se muestra una vez)

**Configurar:**

Windows PowerShell:
```powershell
$env:OPENAI_API_KEY="sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

Windows CMD:
```cmd
set OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

Linux/Mac:
```bash
export OPENAI_API_KEY="sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

Para hacer permanente (Linux/Mac), añade a `~/.bashrc` o `~/.zshrc`:
```bash
export OPENAI_API_KEY="sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

### OPENAI_MODEL (Opcional)
Modelo de OpenAI a utilizar. Por defecto: `gpt-3.5-turbo`

**Opciones:**
- `gpt-3.5-turbo` - Recomendado: rápido y económico
- `gpt-4` - Más potente pero más costoso
- `gpt-4-turbo` - Balance entre potencia y velocidad

```powershell
$env:OPENAI_MODEL="gpt-3.5-turbo"
```

---

## 💾 Variables de Base de Datos

### DATABASE_URL
URL de conexión a la base de datos.

**Desarrollo:**
```powershell
$env:DATABASE_URL="jdbc:mysql://localhost:3306/control_jugadores"
```

**Producción (ejemplo con PostgreSQL):**
```bash
export DATABASE_URL="jdbc:postgresql://tu-servidor:5432/nombre_bd"
```

### DATABASE_USERNAME
Usuario de la base de datos.

```powershell
$env:DATABASE_USERNAME="root"
```

### DATABASE_PASSWORD
Contraseña de la base de datos.

```powershell
$env:DATABASE_PASSWORD="tu-password-segura"
```

---

## 🔐 Variables de Seguridad

### JWT_SECRET
Clave secreta para firmar tokens JWT.

**IMPORTANTE:** En producción debe ser una cadena aleatoria de mínimo 32 caracteres.

Generar una clave segura (PowerShell):
```powershell
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$secret = [Convert]::ToBase64String($bytes)
echo $secret
```

Configurar:
```powershell
$env:JWT_SECRET="tu-clave-super-secreta-de-minimo-32-caracteres"
```

### JWT_EXPIRATION
Tiempo de expiración del token JWT en milisegundos.

Default: 36000000 (10 horas)

```powershell
$env:JWT_EXPIRATION="36000000"
```

---

## 🌐 Variables de CORS

### CORS_ALLOWED_ORIGINS
Orígenes permitidos para peticiones CORS (separados por comas).

**Desarrollo:**
```powershell
$env:CORS_ALLOWED_ORIGINS="http://localhost:4200,http://localhost:8100"
```

**Producción:**
```bash
export CORS_ALLOWED_ORIGINS="https://tu-dominio.com,https://www.tu-dominio.com"
```

---

## 🚀 Script Completo para Desarrollo Local

### Windows PowerShell

Crea un archivo `set-env.ps1`:

```powershell
# AI Configuration
$env:OPENAI_API_KEY="sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
$env:OPENAI_MODEL="gpt-3.5-turbo"

# Database Configuration
$env:DATABASE_URL="jdbc:mysql://localhost:3306/control_jugadores"
$env:DATABASE_USERNAME="root"
$env:DATABASE_PASSWORD="1234"

# Security Configuration
$env:JWT_SECRET="examportal_dev_secret_key_change_in_production_minimum_32_chars"
$env:JWT_EXPIRATION="36000000"

# CORS Configuration
$env:CORS_ALLOWED_ORIGINS="http://localhost:4200,http://localhost:8100,http://localhost:8101"

Write-Host "✅ Variables de entorno configuradas correctamente"
Write-Host "🚀 Ahora puedes ejecutar: mvn spring-boot:run"
```

Ejecutar:
```powershell
.\set-env.ps1
```

### Linux/Mac

Crea un archivo `set-env.sh`:

```bash
#!/bin/bash

# AI Configuration
export OPENAI_API_KEY="sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
export OPENAI_MODEL="gpt-3.5-turbo"

# Database Configuration
export DATABASE_URL="jdbc:mysql://localhost:3306/control_jugadores"
export DATABASE_USERNAME="root"
export DATABASE_PASSWORD="1234"

# Security Configuration
export JWT_SECRET="examportal_dev_secret_key_change_in_production_minimum_32_chars"
export JWT_EXPIRATION="36000000"

# CORS Configuration
export CORS_ALLOWED_ORIGINS="http://localhost:4200,http://localhost:8100,http://localhost:8101"

echo "✅ Variables de entorno configuradas correctamente"
echo "🚀 Ahora puedes ejecutar: mvn spring-boot:run"
```

Ejecutar:
```bash
source set-env.sh
```

---

## 📋 Verificar Variables

### Windows PowerShell
```powershell
echo $env:OPENAI_API_KEY
echo $env:DATABASE_URL
echo $env:JWT_SECRET
```

### Linux/Mac
```bash
echo $OPENAI_API_KEY
echo $DATABASE_URL
echo $JWT_SECRET
```

---

## 🐳 Docker / Docker Compose

Si usas Docker, configura en `docker-compose.yml`:

```yaml
services:
  backend:
    environment:
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - DATABASE_URL=${DATABASE_URL}
      - DATABASE_USERNAME=${DATABASE_USERNAME}
      - DATABASE_PASSWORD=${DATABASE_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
```

Y crea un archivo `.env` en la raíz del proyecto:

```env
OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
DATABASE_URL=jdbc:mysql://db:3306/control_jugadores
DATABASE_USERNAME=root
DATABASE_PASSWORD=root_password_secure
JWT_SECRET=tu-clave-super-secreta-de-minimo-32-caracteres
```

---

## 🔒 Seguridad

### ✅ Buenas prácticas:

1. **NUNCA** subir `.env` o archivos con credenciales a Git
2. Añadir a `.gitignore`:
   ```
   .env
   set-env.ps1
   set-env.sh
   application.properties (si contiene credenciales)
   ```
3. Usar variables de entorno en producción
4. Rotar claves periódicamente
5. Usar claves diferentes para desarrollo y producción
6. No compartir API keys por email/chat/screenshot

### ⚠️ Si expones una clave accidentalmente:

1. **OpenAI**: Ve a https://platform.openai.com/api-keys y revoca la clave
2. **JWT**: Cambia la clave inmediatamente
3. **Base de datos**: Cambia las credenciales

---

## 📚 Recursos

- [OpenAI API Keys](https://platform.openai.com/api-keys)
- [Spring Boot External Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [The Twelve-Factor App - Config](https://12factor.net/config)

---

**¡Configura tus variables de entorno antes de ejecutar la aplicación! 🚀**
