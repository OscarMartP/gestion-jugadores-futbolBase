# 🚀 Guía de Deployment en Render.com

## ✅ Ventajas de Render.com

- ✅ **100% GRATIS** (no requiere tarjeta de crédito)
- ✅ **512MB RAM** incluidos (suficiente para Spring Boot)
- ✅ **750 horas gratis/mes** (suficiente para 1 app 24/7)
- ✅ **Deploy desde GitHub** automático
- ✅ **Base de datos externa** (tu Neon PostgreSQL) funciona perfecto
- ✅ **SSL/TLS** automático y gratuito
- ⚠️ Se "duerme" tras 15 minutos de inactividad (tarda ~30s en despertar)

## 📋 Pasos de Deployment

### 1. Crear cuenta en Render
1. Ve a: https://render.com/
2. Click en "Get Started" o "Sign Up"
3. Usa tu cuenta de **GitHub** para registrarte (recomendado)
4. ✅ **NO requiere tarjeta de crédito**

### 2. Crear nuevo Web Service

1. En el dashboard de Render, click en **"New +"** → **"Web Service"**
2. Click en **"Connect GitHub"** (si es la primera vez)
3. Busca y selecciona: `OscarMartP/gestion-jugadores-futbolBase`
4. Click en **"Connect"**

### 3. Configurar el servicio

#### Basic Settings:
- **Name**: `gestion-jugadores-api`
- **Region**: Frankfurt (Europe) - más cercano a España
- **Branch**: `VersionMobilPro-IA`
- **Root Directory**: `gestion-jugadores-futbolBase`

#### Build Settings:
- **Environment**: `Docker`
- **Dockerfile Path**: `./Dockerfile` (detección automática)

#### Instance Settings:
- **Instance Type**: **Free** ⭐ (512MB RAM, 0.1 CPU)

### 4. Configurar Variables de Entorno (Environment Variables)

En la sección **"Environment"**, añade las siguientes variables:

#### Variables públicas:
```
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

#### Variables secretas:

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
http://localhost:8100,http://localhost:4200,http://localhost:8101
```

### 5. Health Check (Opcional pero recomendado)

En **"Advanced"** → **"Health Check Path"**:
```
/actuator/health
```

### 6. Desplegar

1. Click en **"Create Web Service"**
2. Render comenzará el build (5-10 minutos la primera vez)
3. Monitorea los logs en tiempo real

## 🌐 Acceder a tu API

Una vez desplegado, tu API estará disponible en:
```
https://gestion-jugadores-api.onrender.com
```

Endpoints principales:
- Health: `https://gestion-jugadores-api.onrender.com/actuator/health`
- API Base: `https://gestion-jugadores-api.onrender.com/api/v1/`

## 📊 Monitoreo

### Ver logs en tiempo real:
1. Ve a tu servicio en el dashboard
2. Click en la pestaña "Logs"
3. Filtra por tipo: Info, Warning, Error

### Métricas disponibles:
- Request count
- Response times
- Memory usage
- CPU usage
- Deploy history

## 🔄 Deploys automáticos

Render hace deploy automático cada vez que hagas push a `VersionMobilPro-IA`:

```bash
git add .
git commit -m "feat: nuevas funcionalidades"
git push origin VersionMobilPro-IA
```

## ⚡ Sobre el "Sleep Mode"

### ¿Cómo funciona?
- Tras **15 minutos** sin requests, el servicio se "duerme"
- Primera petición después: tarda ~**30 segundos** en despertar
- Peticiones siguientes: responden normal

### ¿Cómo evitarlo? (Opcionales)
1. **Cron job gratuito**: Usa cron-job.org para hacer ping cada 14 minutos
2. **UptimeRobot**: Servicio gratuito de monitoring que hace ping automático
3. **Desde tu frontend**: Un timer que haga ping cada 10 minutos

Ejemplo de cron externo:
```bash
# Hacer GET cada 14 minutos a:
https://gestion-jugadores-api.onrender.com/actuator/health
```

## 🆓 Límites del tier gratuito

- ✅ **750 horas/mes** por servicio
- ✅ **512MB RAM**
- ✅ **0.1 CPU**
- ✅ **100GB bandwidth/mes**
- ✅ **Multiple web services** 
- ⚠️ Se duerme tras 15 min inactividad

**Tu app está dentro de los límites** ✅

## 🆘 Troubleshooting

### Error: "Out of Memory"
- Verifica que el Dockerfile tenga las optimizaciones:
  ```dockerfile
  -Xmx350m -Xms350m -XX:MaxMetaspaceSize=96m
  ```

### Error: "Build Failed"
- Revisa los logs de build en Render
- Verifica que el `Root Directory` sea correcto: `gestion-jugadores-futbolBase`
- Asegúrate que el Maven build funciona local: `mvn clean package`

### Error: "Connection timeout"
- Verifica que la DATABASE_URL de Neon sea correcta
- Asegúrate que Neon permita conexiones externas
- Prueba la conexión desde local primero

### La app no responde después de despertar:
- Es normal que tarde ~30s la primera vez
- Si tarda más, revisa los logs
- Verifica el health check endpoint

### Error: "Port Already in Use"
- Render asigna el puerto automáticamente
- No cambies el `SERVER_PORT=8080` en las variables

## 🔐 Seguridad

- Variables de entorno encriptadas automáticamente
- SSL/TLS automático incluido (HTTPS)
- Health checks configurables
- Logs con retención de 7 días

## 📱 Actualizar frontend/móvil

Después del deploy, actualiza las URLs:

**Frontend** (`gestion-jugadores-frontend/src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://gestion-jugadores-api.onrender.com/api/v1'
};
```

**Móvil** (`gestion-jugadores-mobile/src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://gestion-jugadores-api.onrender.com/api/v1'
};
```

Y actualiza CORS_ALLOWED_ORIGINS con tus URLs reales.

## ⚙️ Configuración avanzada (opcional)

### Custom Domain:
1. Ve a Settings → Custom Domains
2. Añade tu dominio
3. Configura los DNS según instrucciones

### Notifications:
1. Settings → Notifications
2. Configura alertas por email
3. Deploy success/failure notifications

### Auto-Deploy:
- Por defecto está activado
- Puedes desactivarlo en Settings si prefieres deploys manuales

## 🔄 Redeploy Manual

Si necesitas forzar un redeploy:
1. Ve a tu servicio
2. Click en "Manual Deploy"
3. Selecciona "Clear build cache & deploy"

## 💡 Tips de Optimización

### Acelerar wake-up:
- Usa health check optimizado
- Minimiza dependencias pesadas
- Lazy-loading de módulos cuando sea posible

### Reducir uso de memoria:
- Ya está optimizado en el Dockerfile
- Monitorea en Metrics si necesitas ajustar

### Mejorar build time:
- El cache de Docker reduce builds a ~2-3 minutos
- Solo cambia si es necesario el `pom.xml`

## 📚 Recursos

- Dashboard: https://dashboard.render.com/
- Documentación: https://render.com/docs
- Status: https://status.render.com/
- Community: https://community.render.com/

---

## ✨ Resumen

1. ✅ Cuenta gratuita (sin tarjeta)
2. ✅ Repositorio conectado
3. ✅ Variables de entorno configuradas
4. ✅ Deploy automático activado
5. ✅ HTTPS incluido
6. ⚠️ Se duerme tras 15 min (acceptable para dev/demo)

**Tu backend funcionando 24/7 gratis** 🎉

## 🎯 Próximos pasos

1. Crear cuenta en Render
2. Conectar repo GitHub
3. Configurar variables
4. Deploy
5. Actualizar URLs en apps
6. (Opcional) Configurar cron para evitar sleep
