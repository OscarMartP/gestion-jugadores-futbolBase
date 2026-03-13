# 🚀 Setup rápido de Koyeb - Instrucciones paso a paso

## 1️⃣ Crear cuenta (1 minuto)
1. Ve a: https://app.koyeb.com/auth/signup
2. Click en "Continue with GitHub"
3. Autoriza Koyeb
4. ✅ Ya tienes cuenta (sin tarjeta de crédito)

## 2️⃣ Crear nueva aplicación (5 minutos)

### En el dashboard de Koyeb:
1. Click en **"Create Web Service"**
2. Selecciona **"GitHub"**
3. Si es la primera vez, autoriza Koyeb para acceder a tus repos
4. Selecciona: **OscarMartP/gestion-jugadores-futbolBase**
5. Branch: **VersionMobilPro-IA**

### Configuración del Builder:
- **Builder**: Docker
- **Dockerfile path**: `gestion-jugadores-futbolBase/Dockerfile`
- **Context**: `gestion-jugadores-futbolBase`

### Configuración de la Instancia:
- **Service name**: gestion-jugadores-api
- **Region**: Frankfurt (fra)
- **Instance type**: **Eco** ⭐ (512MB RAM - GRATIS)
- **Scaling**: Min: 1, Max: 1
- **Port**: 8080

### Health Checks:
- **Protocol**: HTTP
- **Path**: `/actuator/health`
- **Port**: 8080
- **Grace period**: 120 segundos
- **Interval**: 60 segundos

## 3️⃣ Variables de Entorno

Click en **"Advanced"** > **"Environment variables"** y añade:

### Variables públicas:
```
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

### Variables secretas (marca como "Secret"):

**DATABASE_URL**:
```
postgresql://neondb_owner:npg_M4XBZsp3tuIq@ep-muddy-sky-agxvuo5l-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
```

**JWT_SECRET**:
```
sz97eem>=`LK$s|)q^D2zXg(dqB+_IOR*4*25]=Eyzi%I:K5ZIo1y]lq+/y^+SeG
```

**OPENAI_API_KEY**:
```
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
```

**CORS_ALLOWED_ORIGINS**:
```
http://localhost:8100,http://localhost:4200,http://localhost:8101,https://tu-frontend.com
```

## 4️⃣ Deploy

1. Revisa la configuración
2. Click en **"Deploy"**
3. Espera 5-10 minutos (compilación de Spring Boot)
4. Monitorea los logs en tiempo real

## ✅ Verificar que funciona

Una vez desplegado, verás tu URL:
```
https://gestion-jugadores-api-XXXXX.koyeb.app
```

Prueba el health check:
```bash
curl https://gestion-jugadores-api-XXXXX.koyeb.app/actuator/health
```

Deberías ver:
```json
{"status":"UP"}
```

## 📱 Actualizar apps (frontend y móvil)

Actualiza la URL de la API en tus aplicaciones:

**Frontend** (`environment.prod.ts`):
```typescript
apiUrl: 'https://gestion-jugadores-api-XXXXX.koyeb.app/api/v1'
```

**Móvil** (`environment.prod.ts`):
```typescript
apiUrl: 'https://gestion-jugadores-api-XXXXX.koyeb.app/api/v1'
```

También actualiza CORS_ALLOWED_ORIGINS con tu URL real del frontend cuando la tengas.

## 🔄 Deploys automáticos

Cada push a `VersionMobilPro-IA` hará deploy automático:
```bash
git push origin VersionMobilPro-IA
```

## 📊 Monitoreo

En el dashboard de Koyeb puedes ver:
- **Logs** en tiempo real
- **Métricas** (CPU, RAM, requests)
- **Health status**
- **Deployment history**

## 🆘 Si hay problemas

### Out of Memory:
- Debería funcionar con 512MB
- Los logs mostrarán "OOM killed" si pasa
- Solución: revisar que el Dockerfile tenga `-Xmx320m`

### No arranca:
- Revisa los logs en el dashboard
- Verifica que todas las variables de entorno estén configuradas
- Aumenta el grace period a 180 segundos si es necesario

### Connection timeout:
- Verifica que la DATABASE_URL de Neon sea correcta
- Prueba la conexión a Neon desde local primero

## 💰 Costos

✅ **$0/mes** con el tier Eco:
- 512MB RAM
- 0.1 CPU
- 2 servicios web
- 100GB transfer/mes

---

## 🎯 Resumen del proceso:

1. ✅ Cuenta en Koyeb (sin tarjeta)
2. ✅ Conectar repo GitHub
3. ✅ Configurar Docker build
4. ✅ Añadir variables de entorno
5. ✅ Deploy
6. ✅ Verificar health check
7. ✅ Actualizar URLs en apps

**Total: ~10 minutos** ⏱️

¡Listo para producción! 🎉
