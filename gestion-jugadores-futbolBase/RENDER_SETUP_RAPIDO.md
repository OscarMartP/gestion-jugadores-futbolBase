# 🚀 Setup Rápido Render.com - 5 minutos

## 1️⃣ Crear cuenta (1 minuto)
👉 https://dashboard.render.com/register

- Click en **"Sign up with GitHub"**
- Autoriza Render
- ✅ Ya tienes cuenta (sin tarjeta)

## 2️⃣ Crear Web Service (2 minutos)

1. Dashboard → Click **"New +"** → **"Web Service"**
2. Click **"Connect account"** (para GitHub)
3. Busca: `OscarMartP/gestion-jugadores-futbolBase`
4. Click **"Connect"**

## 3️⃣ Configuración (2 minutos)

### Información básica:
```
Name: gestion-jugadores-api
Region: Frankfurt (Europe)
Branch: VersionMobilPro-IA
Root Directory: gestion-jugadores-futbolBase
```

### Build:
```
Environment: Docker
```

### Instance:
```
Instance Type: Free (512MB RAM)
```

### Variables de entorno:

Click en **"Advanced"** y añade:

```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DATABASE_URL=postgresql://neondb_owner:npg_M4XBZsp3tuIq@ep-muddy-sky-agxvuo5l-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
JWT_SECRET=sz97eem>=`LK$s|)q^D2zXg(dqB+_IOR*4*25]=Eyzi%I:K5ZIo1y]lq+/y^+SeG
OPENAI_API_KEY=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
CORS_ALLOWED_ORIGINS=http://localhost:8100,http://localhost:4200,http://localhost:8101
```

### Health Check (opcional):
```
Health Check Path: /actuator/health
```

## 4️⃣ Deploy

- Click **"Create Web Service"**
- Espera 5-10 minutos (build de Spring Boot)
- ✅ URL generada: `https://gestion-jugadores-api.onrender.com`

## 5️⃣ Verificar

Prueba el health check:
```bash
curl https://gestion-jugadores-api.onrender.com/actuator/health
```

Respuesta esperada:
```json
{"status":"UP"}
```

---

## ✅ ¡Listo!

Tu API está corriendo en:
```
https://gestion-jugadores-api.onrender.com
```

**Endpoints:**
- Health: `/actuator/health`
- API: `/api/v1/*`

---

## ⚠️ Nota sobre "Sleep Mode"

- Se duerme tras 15 min sin uso
- Primera petición: ~30s para despertar
- Solución: UptimeRobot (gratuito) hace ping cada 5 min

---

## 🔄 Deploys automáticos

Cada `git push` a `VersionMobilPro-IA` hace deploy automático.

---

## 📱 Actualizar apps

Cambia la URL en tus environments:
```typescript
apiUrl: 'https://gestion-jugadores-api.onrender.com/api/v1'
```

---

**Total: ~5 minutos** ⏱️
**Costo: $0/mes** 💰
