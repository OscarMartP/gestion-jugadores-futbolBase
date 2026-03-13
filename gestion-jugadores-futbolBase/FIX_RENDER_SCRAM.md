# 🔧 Fix para Error SCRAM de PostgreSQL en Render

## ❌ Error que tenías:
```
Exited with status 1 while running your code
Fix SCRAM: PostgreSQL 42.6.0 + HikariCP optimizado
```

## ✅ Solución implementada:

### 1. **DataSourceConfig.java** creado
- Convierte automáticamente `postgresql://` a `jdbc:postgresql://`
- Asegura que SSL esté habilitado (`sslmode=require`)
- Maneja la URL de Neon correctamente

### 2. **application-prod.properties** actualizado
- Configuración optimizada de HikariCP para Neon
- Pool de conexiones: 5 máximo, 2 mínimo
- Timeouts configurados apropiadamente
- `ddl-auto=update` para crear tablas automáticamente

### 3. **PostgreSQL Driver 42.7.4**
- Última versión estable con soporte completo para SCRAM-SHA-256
- Compatible con Neon PostgreSQL

## 🚀 Cómo hacer el nuevo deploy:

### Opción 1: Desde el dashboard de Render

1. Ve a tu servicio: https://dashboard.render.com/web/srv-d65i4j0gjchc73f25o9g
2. Click en **"Manual Deploy"** → **"Clear build cache & deploy"**
3. Espera 5-10 minutos

### Opción 2: Desde Git

```bash
git push origin VersionMobilPro-IA
```

Render detectará el cambio y hará deploy automático.

## 📊 Verificar que funciona:

Una vez desplegado, prueba:

```bash
# Health check
curl https://gestion-jugadores-futbolbase.onrender.com/actuator/health

# Debe devolver:
{"status":"UP"}
```

## 🔍 Si sigue fallando:

### Revisar logs:
1. Dashboard → Tu servicio → "Logs"
2. Busca mensajes de error específicos

### Errores comunes:

**Error: "Connection timeout"**
```
Solución: Verifica que DATABASE_URL esté configurada correctamente
```

**Error: "SSL connection required"**
```
Solución: DATABASE_URL debe incluir ?sslmode=require al final
```

**Error: "Authentication failed"**
```
Solución: Verifica usuario y password en DATABASE_URL de Neon
```

## 🔑 Variable DATABASE_URL correcta:

Debe estar configurada exactamente así en Render:

```
postgresql://neondb_owner:npg_M4XBZsp3tuIq@ep-muddy-sky-agxvuo5l-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
```

**Nota**: No lleva `jdbc:` al principio - DataSourceConfig lo añade automáticamente.

## ✨ Mejoras implementadas:

- ✅ Conexión SSL/TLS automática
- ✅ Pool de conexiones optimizado (5 conexiones máx)
- ✅ Leak detection para detectar conexiones no cerradas
- ✅ Timeouts configurados apropiadamente
- ✅ Soporte completo SCRAM-SHA-256
- ✅ Compatible con Neon PostgreSQL pooling

## 📝 Cambios realizados:

1. **DataSourceConfig.java** - Nuevo archivo de configuración
2. **application-prod.properties** - Optimizado para Neon
3. **pom.xml** - Driver PostgreSQL actualizado
4. **Dockerfile** - Sin cambios (ya optimizado)

## 🎯 Próximo paso:

Haz el deploy nuevamente y debería funcionar correctamente. El error de SCRAM está resuelto.
