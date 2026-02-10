# 🚀 Deployment a Producción - Render + Neon + Vercel

Esta guía te llevará paso a paso para desplegar tu aplicación de gestión de jugadores de fútbol base en producción de forma **100% GRATUITA**.

## 📋 Stack de Producción

- **Backend (Spring Boot)**: Render (750 horas/mes gratis)
- **Base de Datos**: Neon PostgreSQL (3GB gratis)
- **Frontend (Angular)**: Vercel (ilimitado gratis)
- **Mobile (Ionic)**: Vercel o APK para Android

---

## 🗄️ Paso 1: Configurar Base de Datos en Neon

### 1.1 Crear cuenta en Neon
1. Ve a https://neon.tech
2. Regístrate con tu cuenta de GitHub
3. Crea un nuevo proyecto: "gestion-jugadores-db"
4. Selecciona región más cercana

### 1.2 Obtener credenciales
Una vez creado el proyecto, verás:
```
Connection string: postgresql://user:password@ep-xxx.region.aws.neon.tech/dbname
```

Guarda estos datos:
- **DATABASE_URL**: el connection string completo
- **DATABASE_USERNAME**: usuario (antes de `:` en la URL)
- **DATABASE_PASSWORD**: contraseña (después de `:` y antes de `@`)

---

## 🔧 Paso 2: Desplegar Backend en Render

### 2.1 Crear cuenta en Render
1. Ve a https://render.com
2. Regístrate con tu cuenta de GitHub
3. Conecta tu repositorio

### 2.2 Crear Web Service
1. Click en "New +" → "Web Service"
2. Conecta tu repositorio: `OscarMartP/gestion-jugadores-futbolBase`
3. Selecciona rama: `VersionMobilPro-Render`

### 2.3 Configuración del servicio
```
Name: gestion-jugadores-backend
Region: Frankfurt (o más cercana)
Branch: VersionMobilPro-Render
Root Directory: gestion-jugadores-futbolBase
Runtime: Java
Build Command: mvn clean package -DskipTests
Start Command: java -Dspring.profiles.active=prod -jar target/gestion-jugadores-futbolBase-1.0.jar
```

### 2.4 Variables de entorno
En la sección "Environment", añade:

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://user:password@ep-xxx.region.aws.neon.tech/dbname
DATABASE_USERNAME=tu_usuario
DATABASE_PASSWORD=tu_password
JWT_SECRET=tu_secreto_super_seguro_minimo_32_caracteres_aqui_123456
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

**IMPORTANTE**: Por ahora deja CORS_ALLOWED_ORIGINS con localhost, lo actualizaremos después de desplegar el frontend.

### 2.5 Plan
Selecciona: **Free** (750 horas/mes)

### 2.6 Deploy!
Click en "Create Web Service" y espera ~5-10 minutos

Tu backend estará en: `https://gestion-jugadores-backend.onrender.com`

---

## 🎨 Paso 3: Desplegar Frontend en Vercel

### 3.1 Preparar frontend para producción
El frontend ya está configurado. Solo necesitas actualizar la URL del API.

### 3.2 Crear cuenta en Vercel
1. Ve a https://vercel.com
2. Regístrate con tu cuenta de GitHub

### 3.3 Importar proyecto
1. Click en "Add New..." → "Project"
2. Importa tu repositorio
3. Selecciona la carpeta: `gestion-jugadores-frontend`
4. Framework Preset: **Angular**
5. Root Directory: `gestion-jugadores-frontend`

### 3.4 Variables de entorno
```bash
API_URL=https://gestion-jugadores-backend.onrender.com
```

### 3.5 Deploy!
Click en "Deploy" y espera ~2-3 minutos

Tu frontend estará en: `https://gestion-jugadores-xxxxxxx.vercel.app`

---

## 📱 Paso 4: Desplegar Mobile en Vercel

### 4.1 Importar proyecto mobile
Repite el proceso anterior pero con:
- Root Directory: `gestion-jugadores-mobile`
- Framework Preset: **Angular**

### 4.2 Variables de entorno
```bash
API_URL=https://gestion-jugadores-backend.onrender.com
```

Tu app móvil estará en: `https://gestion-jugadores-mobile-xxxxxxx.vercel.app`

---

## 🔄 Paso 5: Actualizar CORS

Una vez tengas las URLs de Vercel, actualiza la variable de entorno en Render:

1. Ve a tu servicio en Render → Environment
2. Edita `CORS_ALLOWED_ORIGINS`:
```
https://gestion-jugadores-xxxxxxx.vercel.app,https://gestion-jugadores-mobile-xxxxxxx.vercel.app
```
3. Guarda y espera a que se redeploy automáticamente

---

## ✅ Verificación

### Backend
- Health check: `https://gestion-jugadores-backend.onrender.com/actuator/health`
- Swagger UI: `https://gestion-jugadores-backend.onrender.com/swagger-ui.html`

### Frontend
- App web: `https://gestion-jugadores-xxxxxxx.vercel.app`
- Debería cargar y conectarse al backend

### Mobile
- App móvil: `https://gestion-jugadores-mobile-xxxxxxx.vercel.app`

---

## 🔧 Troubleshooting

### Backend no arranca
- Verifica logs en Render Dashboard
- Comprueba que las variables de entorno están correctas
- Verifica que DATABASE_URL es válido

### Frontend no conecta
- Verifica CORS en backend
- Comprueba API_URL en variables de entorno de Vercel
- Revisa console del navegador (F12)

### Database connection error
- Verifica que Neon DB está activo
- Comprueba credenciales en Render
- Asegúrate que la IP de Render está permitida (Neon permite todas por defecto)

---

## 📊 Límites del Plan Gratuito

### Render
- ✅ 750 horas/mes (suficiente para uso 24/7)
- ⚠️ Se "duerme" tras 15 min inactividad (primer request tarda ~60s)
- ✅ 512 MB RAM
- ✅ SSL incluido

### Neon
- ✅ 3 GB almacenamiento
- ✅ 100 horas de cómputo/mes
- ✅ Límite de conexiones: 100

### Vercel
- ✅ Bandwidth: 100 GB/mes
- ✅ Builds: Ilimitadas
- ✅ Dominios custom gratis

**Para 30 usuarios es más que suficiente** ✅

---

## 🚀 Próximos pasos

1. **Dominio custom**: Puedes añadir tu propio dominio en Vercel (gratis)
2. **CI/CD**: Los deploys son automáticos al hacer push a la rama
3. **Monitoring**: Usa los dashboards de Render y Vercel
4. **Backups**: Neon hace backups automáticos (last 7 days)

---

## 📞 Soporte

Si algo no funciona:
1. Revisa los logs en Render Dashboard
2. Verifica las variables de entorno
3. Comprueba que la rama en GitHub está actualizada

**¡Tu app está lista para producción!** 🎉
