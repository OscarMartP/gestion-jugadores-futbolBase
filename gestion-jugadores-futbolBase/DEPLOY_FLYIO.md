# 🚀 Guía de Deployment en Fly.io

## 📋 Prerequisitos

1. **Instalar Fly CLI**
   ```bash
   # Windows (PowerShell como Administrador)
   iwr https://fly.io/install.ps1 -useb | iex
   ```

2. **Crear cuenta en Fly.io**
   - Visita: https://fly.io/app/sign-up
   - ⚠️ Requiere tarjeta de crédito (pero no cobra en el tier gratuito)
   - ✅ 3 VMs gratis (256MB RAM cada una)

## 🔧 Pasos de Deployment

### 1. Login en Fly.io
```bash
fly auth login
```

### 2. Navegar al directorio del proyecto
```bash
cd gestion-jugadores-futbolBase
```

### 3. Iniciar la aplicación (primera vez)
```bash
# Esto NO hace deploy, solo crea la app en Fly.io
fly apps create gestion-jugadores-api --region mad
```
Regiones disponibles: `mad` (Madrid), `lhr` (Londres), `fra` (Frankfurt), `iad` (USA Este)

### 4. Crear base de datos PostgreSQL
```bash
# Crear base de datos (3GB gratis)
fly postgres create --name gestion-jugadores-db --region mad

# Adjuntar la base de datos a tu app
fly postgres attach gestion-jugadores-db --app gestion-jugadores-api
```
Esto automáticamente crea la variable `DATABASE_URL` con la URL de conexión PostgreSQL.

### 5. Configurar secrets (variables de entorno sensibles)
```bash
# JWT Secret (genera uno seguro)
fly secrets set JWT_SECRET="tu-secret-super-seguro-minimo-64-caracteres-cambiar-esto-en-produccion" --app gestion-jugadores-api

# OpenAI API Key (obtén una en https://platform.openai.com/api-keys)
fly secrets set OPENAI_API_KEY="tu-openai-api-key" --app gestion-jugadores-api

# CORS Origins (tu dominio frontend)
fly secrets set CORS_ALLOWED_ORIGINS="https://tu-frontend.com,https://tu-app-movil.com" --app gestion-jugadores-api
```

### 6. Realizar el primer deploy
```bash
fly deploy
```

### 7. Verificar que funciona
```bash
# Ver logs en tiempo real
fly logs

# Abrir la app en el navegador
fly open

# Ver status
fly status
```

## 🔄 Deploys posteriores

Después del primer deploy, solo necesitas:
```bash
fly deploy
```

## 📊 Comandos útiles

```bash
# Ver logs de la aplicación
fly logs

# Ver aplicaciones
fly apps list

# SSH a la máquina
fly ssh console

# Ver status de la base de datos
fly postgres db list --app gestion-jugadores-db

# Escalar (si necesitas más recursos)
fly scale vm shared-cpu-1x --memory 512 --app gestion-jugadores-api

# Ver secrets configurados
fly secrets list --app gestion-jugadores-api

# Actualizar un secret
fly secrets set JWT_SECRET="nuevo-valor" --app gestion-jugadores-api

# Eliminar la aplicación (cuidado!)
fly apps destroy gestion-jugadores-api
```

## 🌐 Acceder a tu API

Después del deploy, tu API estará disponible en:
```
https://gestion-jugadores-api.fly.dev
```

Endpoints importantes:
- Swagger UI: https://gestion-jugadores-api.fly.dev/swagger-ui.html (deshabilitado en prod)
- Health Check: https://gestion-jugadores-api.fly.dev/actuator/health
- API Base: https://gestion-jugadores-api.fly.dev/api/v1/

## 🔒 Migraciones de Base de Datos

Si ya tienes datos en Railway y quieres migrarlos:

1. **Exportar desde Railway**
   ```bash
   # En Railway, obtén las credenciales de la DB y haz un dump
   pg_dump -h railway-host -U railway-user -d railway-db > backup.sql
   ```

2. **Importar a Fly.io**
   ```bash
   # Obtén las credenciales de Fly.io
   fly postgres connect --app gestion-jugadores-db

   # Desde otra terminal, importa el dump
   psql "postgres://usuario:password@host/database" < backup.sql
   ```

## 💡 Tips

- **Tier gratuito**: 3 VMs gratis con 256MB RAM cada una. Suficiente para proyectos pequeños/medianos
- **No se duerme**: A diferencia de Render, Fly.io no suspende tu app por inactividad
- **Auto-scaling**: Puedes configurar auto-scaling si tu app crece
- **Regiones**: Madrid (mad) es la más cercana a España

## ⚠️ Importante

- Cambia el nombre de la app (`gestion-jugadores-api`) si ya está tomado
- Los secrets son permanentes hasta que los cambies o elimines
- La base de datos PostgreSQL en el tier gratuito tiene 3GB de almacenamiento
- Monitorea el uso en el dashboard: https://fly.io/dashboard

## 🆘 Troubleshooting

**Error: "región no disponible"**
```bash
fly regions list
# Elige una región disponible
```

**Error: "no se puede conectar a la DB"**
```bash
# Verifica que la DB está corriendo
fly postgres db list --app gestion-jugadores-db

# Verifica la variable DATABASE_URL
fly secrets list --app gestion-jugadores-api
```

**App se cuelga o es lenta**
```bash
# Aumenta la memoria
fly scale vm shared-cpu-1x --memory 512
```

## 📱 Actualizar URLs en tu frontend/móvil

Después del deploy, actualiza las URLs en:
- `gestion-jugadores-frontend/src/environments/environment.prod.ts`
- `gestion-jugadores-mobile/src/environments/environment.prod.ts`

Cambia:
```typescript
apiUrl: 'https://tu-app-railway.up.railway.app/api/v1'
```

Por:
```typescript
apiUrl: 'https://gestion-jugadores-api.fly.dev/api/v1'
```

---

¿Problemas? Consulta la documentación oficial: https://fly.io/docs/
