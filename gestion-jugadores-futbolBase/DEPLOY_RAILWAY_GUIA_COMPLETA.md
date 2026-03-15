# 🚀 Guía Completa de Despliegue en Railway

## ✅ Estado del Proyecto

**El código está 100% listo para Railway:**
- ✅ Spring Boot 2.7.18
- ✅ PostgreSQL Driver 42.7.3
- ✅ Dockerfile optimizado para Railway
- ✅ Configuración usa `DATABASE_URL` (generada automáticamente por Railway)
- ✅ Sin MySQL (eliminado)
- ✅ Sin conflictos de drivers

---

## 📋 Pasos de Despliegue

### **1. Preparar Repositorio GitHub**

**Si el repo NO es público todavía:**
```
1. Ve a: https://github.com/OscarMartP/gestion-jugadores-futbolBase
2. Settings → Danger Zone → "Change visibility" → "Change to public"
3. Confirmar escribiendo el nombre del repo
```

**Nota:** Puedes volver a ponerlo privado después del despliegue.

---

### **2. Acceder a Railway con Cuenta Nueva**

**¿Ya tienes cuenta Railway nueva con $5 crédito?**

**SI:**
- Ve a https://railway.app
- Login con el GitHub nuevo (futbolbasestudio)

**NO (necesitas crear cuenta nueva):**
- Ventana incógnito → https://railway.app
- "Login" → "Sign in with GitHub"
- "Continue with Google" → Seleccionar Gmail nuevo
- Completar registro GitHub
- Railway te da $5 gratis

---

### **3. Crear Proyecto en Railway**

1. Click **"New Project"**
2. Selecciona **"Deploy from GitHub repo"**
3. Si no aparece el repo:
   - Click "Configure GitHub App"
   - Authorize Railway
   - Seleccionar "All repositories"
4. Buscar: `gestion-jugadores-futbolBase`
5. Click en el repositorio

---

### **4. Configurar Deploy**

Railway te preguntará:

**Branch:**
```
VersionMobilPro-IA
```

**Root Directory:**
```
gestion-jugadores-futbolBase
```

**Luego click "Deploy"** o "Deploy Now"

---

### **5. Agregar PostgreSQL (CRÍTICO)**

Mientras se despliega:

1. En el proyecto, click **"+ New"** (arriba derecha)
2. Click **"Database"**
3. Selecciona **"Add PostgreSQL"**
4. Railway conecta automáticamente (crea la variable `DATABASE_URL`)

**IMPORTANTE:** La base de datos tarda 1-2 minutos en crearse.

---

### **6. Configurar Variables de Entorno**

Click en el **servicio de Spring Boot** (no en PostgreSQL) → pestaña **"Variables"**

Agregar estas variables **una por una**:

#### **Variable 1: SPRING_PROFILES_ACTIVE**
```
SPRING_PROFILES_ACTIVE=prod
```

#### **Variable 2: JWT_SECRET**
```
JWT_SECRET=7k9mP2xR5n0Bwl3jF6vBicZ4hN0yT9sA5eGBiD2oU7qW3mX6rKIpL9
```

#### **Variable 3: JWT_EXPIRATION**
```
JWT_EXPIRATION=86400000
```

#### **Variable 4: CORS_ALLOWED_ORIGINS**
```
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:8100
```

#### **Variable 5: OPENAI_API_KEY** (si usas módulo IA)
```
OPENAI_API_KEY=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
```

**NO AGREGUES:**
- ❌ DATABASE_URL (Railway la crea automáticamente)
- ❌ SPRING_DATASOURCE_URL/USERNAME/PASSWORD (no se usan)

---

### **7. Monitorear el Despliegue**

1. Click en **"Deployments"**
2. Ver logs en tiempo real
3. Esperar mensajes:
   - `Building...` (3-5 min)
   - `Deploying...` (1-2 min)
   - `Started Application in X seconds` ✅
   - `Deployed` ✅

**Si hay errores:**
- Ver logs completos
- Buscar líneas con "ERROR" o "Exception"
- Verificar que PostgreSQL esté conectado

---

### **8. Obtener la URL Pública**

1. Click en el **servicio** (no en PostgreSQL)
2. Pestaña **"Settings"**
3. Sección **"Public Networking"** o **"Domains"**
4. Click **"Generate Domain"**
5. Te da URL: `https://gestion-jugadores-xxxxx.up.railway.app`

---

### **9. Verificar que Funciona**

**Prueba el health check:**
```bash
curl https://tu-url.up.railway.app/actuator/health
```

**Respuesta esperada:**
```json
{"status":"UP"}
```

**Prueba un endpoint:**
```bash
curl https://tu-url.up.railway.app/api/v1/auth/test
```

---

### **10. (Opcional) Volver Repo a Privado**

Si hiciste el repo público:

1. GitHub → Settings → Danger Zone
2. "Change visibility" → "Change to private"
3. Confirmar

**Nota:** Railway seguirá funcionando porque ya tiene acceso.

---

## 🔧 Troubleshooting

### **Error: "No repositories found"**
- Verificar que el repo es público
- Click "Configure GitHub App" y autorizar

### **Error: "Application failed to start"**
- Ver logs en Deployments
- Verificar que PostgreSQL está conectado
- Verificar que DATABASE_URL existe en Variables

### **Error: "Connection refused" o "Unable to connect"**
- Esperar 2 minutos (PostgreSQL tarda en arrancar)
- Verificar variables de entorno
- Ver logs de PostgreSQL

### **Error: "Out of memory"**
- Railway tiene 512MB RAM (debería ser suficiente)
- Ver logs para confirmar OOM
- Contactar soporte Railway si persiste

---

## 📊 Límites del Plan Gratuito

- **Crédito:** $5 gratis
- **Duración:** ~1 mes con uso moderado
- **Recursos:** 512MB RAM, 1GB storage
- **Sleep:** No (Railway no duerme servicios)

---

## 🎯 Checklist Final

Antes de dar por terminado, verifica:

- [ ] Proyecto creado en Railway
- [ ] PostgreSQL conectado
- [ ] 5 variables de entorno configuradas
- [ ] Deploy exitoso (logs sin errores)
- [ ] URL generada
- [ ] Health check responde `{"status":"UP"}`
- [ ] (Opcional) Repo vuelto a privado

---

## 🆘 Si Necesitas Ayuda

1. Ver logs completos en Deployments
2. Copiar el error exacto
3. Verificar que todos los pasos se siguieron
4. Contactar para debugging específico

---

## ✅ Resumen de Configuración Actual

**Spring Boot:** 2.7.18  
**Java:** 17  
**PostgreSQL Driver:** 42.7.3  
**Branch:** VersionMobilPro-IA  
**Root Directory:** gestion-jugadores-futbolBase  
**Puerto:** 8080 (Railway lo mapea automáticamente)  

**Última actualización:** Configuración optimizada para Railway con DATABASE_URL automática.
