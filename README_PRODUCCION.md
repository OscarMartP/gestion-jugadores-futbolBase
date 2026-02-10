# 🚀 Rama VersionMobilPro-Render - Lista para Producción

Esta rama contiene todos los cambios necesarios para desplegar la aplicación en producción de forma **100% GRATUITA**.

## 📋 Cambios Realizados

### Backend (Spring Boot)
- ✅ Añadido soporte para PostgreSQL (Neon)
- ✅ Mantenido soporte MySQL para desarrollo local
- ✅ Configuración con variables de entorno
- ✅ Archivo `application-prod.properties` para producción
- ✅ Configuración CORS global
- ✅ Health check endpoint (`/actuator/health`)
- ✅ Archivo `render.yaml` para deployment automático

### Frontend (Angular)
- ✅ Configuración de producción actualizada
- ✅ URL del API apuntando a Render
- ✅ Archivo `vercel.json` para deployment
- ✅ Scripts de build optimizados

### Mobile (Ionic)
- ✅ Configuración de producción actualizada
- ✅ URL del API apuntando a Render
- ✅ Archivo `vercel.json` para deployment
- ✅ Scripts de build optimizados

## 🎯 Stack de Producción Gratuito

| Componente | Servicio | Plan | Límites |
|------------|----------|------|---------|
| Backend | Render | Free | 750h/mes, 512MB RAM |
| Base de Datos | Neon PostgreSQL | Free | 3GB, 100h cómputo/mes |
| Frontend Web | Vercel | Free | 100GB bandwidth/mes |
| App Móvil | Vercel | Free | 100GB bandwidth/mes |

**Total: $0/mes** para hasta 30 usuarios

## 📚 Guía de Despliegue

Consulta el archivo **[DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)** para instrucciones detalladas paso a paso.

### Resumen Rápido

1. **Base de Datos**: Crear cuenta en Neon.tech → Obtener connection string
2. **Backend**: Crear Web Service en Render.com → Configurar variables de entorno
3. **Frontend**: Importar proyecto en Vercel → Deploy automático
4. **Mobile**: Importar proyecto en Vercel → Deploy automático
5. **CORS**: Actualizar variable en Render con URLs de Vercel

## ⚙️ Variables de Entorno Necesarias

### Render (Backend)
```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://user:pass@host/dbname
DATABASE_USERNAME=usuario
DATABASE_PASSWORD=contraseña
JWT_SECRET=tu_secreto_minimo_32_caracteres
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app
```

### Neon (Base de Datos)
- Se crea automáticamente al registrarte
- Copiar connection string y credenciales

### Vercel (Frontend/Mobile)
- Deployment automático desde GitHub
- No requiere configuración adicional

## 🔍 Verificación Post-Deployment

### Backend
```bash
curl https://gestion-jugadores-backend.onrender.com/actuator/health
# Respuesta esperada: {"status":"UP"}
```

### Frontend
```
https://gestion-jugadores-xxxxxxx.vercel.app
# Debería cargar la aplicación web
```

### Swagger UI
```
https://gestion-jugadores-backend.onrender.com/swagger-ui.html
# Documentación API interactiva
```

## ⚠️ Consideraciones Importantes

### Tiempo de "Despertar"
- El backend en Render se "duerme" tras 15 min de inactividad
- Primera petición tras dormirse tarda ~30-60 segundos
- Peticiones subsiguientes son instantáneas
- **Solución**: Hacer un health check cada 10 minutos

### Migración de Datos
Si ya tienes datos en MySQL local:
1. Exportar datos: `mysqldump control_jugadores > backup.sql`
2. Convertir a PostgreSQL con herramienta online o script
3. Importar en Neon usando pgAdmin o DBeaver

## 🛠️ Desarrollo Local

La aplicación sigue funcionando en local con MySQL:

```bash
# Backend
cd gestion-jugadores-futbolBase
mvn spring-boot:run

# Frontend
cd gestion-jugadores-frontend
npm install
npm start

# Mobile
cd gestion-jugadores-mobile
npm install
ionic serve
```

## 📊 Monitoreo

- **Render Dashboard**: Ver logs y métricas del backend
- **Vercel Dashboard**: Ver deploy logs y analytics
- **Neon Console**: Ver uso de base de datos

## 🔄 CI/CD Automático

Cada push a esta rama desplegará automáticamente:
- Backend → Render (si configuraste GitHub integration)
- Frontend → Vercel (deploy automático)
- Mobile → Vercel (deploy automático)

## 📞 Soporte

Si encuentras problemas durante el deployment:
1. Revisa logs en el dashboard correspondiente
2. Verifica que todas las variables de entorno están configuradas
3. Comprueba que la conexión a base de datos es válida

## ✅ Checklist Pre-Deployment

- [ ] Cuenta creada en Neon.tech
- [ ] Cuenta creada en Render.com
- [ ] Cuenta creada en Vercel.com
- [ ] Repositorio actualizado en GitHub
- [ ] Variables de entorno preparadas
- [ ] JWT_SECRET generado (32+ caracteres)

---

**¡Tu aplicación está lista para producción!** 🎉

Tiempo estimado de deployment completo: **~30 minutos**
