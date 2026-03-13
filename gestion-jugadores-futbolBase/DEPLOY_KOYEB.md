# 🚀 Guía de Deployment en Koyeb

## ✅ Ventajas de Koyeb

- ✅ **Completamente GRATIS** (no requiere tarjeta de crédito)
- ✅ **512MB RAM** incluidos (suficiente para Spring Boot)
- ✅ **No se duerme** automáticamente
- ✅ **Deploy desde GitHub** automático
- ✅ Base de datos externa (tu Neon) funciona perfecto

## 📋 Pasos de Deployment

### 1. Crear cuenta en Koyeb
1. Ve a: https://www.koyeb.com/
2. Click en "Sign Up" o "Get Started"
3. Usa tu cuenta de GitHub para registrarte (recomendado)
4. ✅ **NO requiere tarjeta de crédito**

### 2. Conectar tu repositorio

1. En el dashboard de Koyeb, click en **"Create App"**
2. Selecciona **"GitHub"** como source
3. Autoriza Koyeb para acceder a tu repo
4. Selecciona el repositorio: `OscarMartP/gestion-jugadores-futbolBase`
5. Branch: `VersionMobilPro-IA`
6. Directorio: `gestion-jugadores-futbolBase` (si es necesario especificarlo)

### 3. Configurar el servicio

#### Build Settings:
- **Builder**: Docker
- **Dockerfile**: `Dockerfile` (en la raíz del proyecto backend)
- **Context**: `.` (o `gestion-jugadores-futbolBase` si es monorepo)

#### Instance Settings:
- **Region**: Frankfurt (fra) - más cercano a España
- **Instance type**: Eco (512MB RAM, 0.1 CPU) - **GRATIS**
- **Port**: 8080

### 4. Configurar Variables de Entorno (Environment Variables)

Añade las siguientes variables en la sección "Environment variables":

```
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DATABASE_URL=postgresql://neondb_owner:npg_M4XBZsp3tuIq@ep-muddy-sky-agxvuo5l-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
JWT_SECRET=sz97eem>=`LK$s|)q^D2zXg(dqB+_IOR*4*25]=Eyzi%I:K5ZIo1y]lq+/y^+SeG
OPENAI_API_KEY=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
CORS_ALLOWED_ORIGINS=http://localhost:8100,http://localhost:4200,http://localhost:8101
```

**💡 Tip**: Marca las variables sensibles como "Secret" para ocultarlas

### 5. Health Check Configuration

- **Path**: `/actuator/health`
- **Port**: 8080
- **Grace period**: 120 segundos (Spring Boot tarda en arrancar)

### 6. Desplegar

1. Click en **"Deploy"**
2. Espera 5-10 minutos (compilación de Spring Boot)
3. Monitorea los logs en tiempo real

## 🌐 Acceder a tu API

Después del deployment, tu API estará disponible en:
```
https://tu-app-nombre.koyeb.app
```

Endpoints:
- Health: `https://tu-app-nombre.koyeb.app/actuator/health`
- API: `https://tu-app-nombre.koyeb.app/api/v1/`

## 📊 Monitoreo

### Ver logs:
1. Ve a tu app en el dashboard
2. Click en la instancia
3. Click en "Logs"

### Métricas:
- CPU usage
- Memory usage
- Network traffic

## 🔄 Deploys automáticos

Koyeb hará deploy automático cada vez que hagas push a la rama `VersionMobilPro-IA`:

```bash
git add .
git commit -m "feat: cambios"
git push origin VersionMobilPro-IA
```

## ⚙️ Configuración avanzada (opcional)

### Scaling (si necesitas más recursos en el futuro):
- Por defecto: 1 instancia
- Puedes escalar horizontal o verticalmente desde el dashboard

### Custom Domain:
1. Ve a Settings > Domains
2. Añade tu dominio personalizado
3. Configura el DNS según las instrucciones

### Auto-scaling:
- Koyeb puede escalar automáticamente si configuras reglas
- En el tier gratuito estás limitado a 2 servicios

## 🆓 Límites del tier gratuito

- ✅ **2 servicios web** simultáneos
- ✅ **512MB RAM** por servicio
- ✅ **0.1 CPU** por servicio
- ✅ **2.5GB disco** por servicio
- ✅ **100GB tráfico** saliente/mes

**Tu configuración cabe perfectamente en el tier gratuito** ✅

## 🆘 Troubleshooting

### Error: "Out of Memory"
- Verifica que el Dockerfile tenga las optimizaciones de memoria:
  ```dockerfile
  -Xmx400m -Xms400m -XX:MaxMetaspaceSize=96m
  ```

### Error: "Connection timeout"
- Aumenta el grace period del health check a 180 segundos
- Spring Boot puede tardar 60-90s en arrancar la primera vez

### Error: "Port binding failed"
- Verifica que `SERVER_PORT=8080` esté en las variables de entorno
- Verifica que el Dockerfile exponga el puerto 8080

### La app no responde:
```bash
# Verifica que la DB sea accesible
curl https://tu-app.koyeb.app/actuator/health
```

## 🔐 Seguridad

- Las variables de entorno marcadas como "Secret" están encriptadas
- SSL/TLS automático incluido
- Health checks desde múltiples regiones

## 📱 Actualizar frontend/móvil

Después del deploy, actualiza las URLs en tus apps:

**Frontend** (`gestion-jugadores-frontend/src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://tu-app.koyeb.app/api/v1'
};
```

**Móvil** (`gestion-jugadores-mobile/src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://tu-app.koyeb.app/api/v1'
};
```

## 📚 Recursos

- Dashboard: https://app.koyeb.com/
- Documentación: https://www.koyeb.com/docs
- Soporte: https://www.koyeb.com/support

---

## ✨ Resumen

1. ✅ Cuenta creada (sin tarjeta)
2. ✅ Repositorio conectado
3. ✅ Variables de entorno configuradas
4. ✅ Deploy automático activado
5. ✅ Health checks configurados

**Tu backend estará corriendo 24/7 sin costo** 🎉
