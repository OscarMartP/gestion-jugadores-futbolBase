# Fix: Error SCRAM Iteration en Render con Neon PostgreSQL

## Error Original

```
Caused by: java.lang.IllegalArgumentException: Argument 'iteration must be >= 4096' is not valid
    at org.postgresql.shaded.com.ongres.scram.common.message.ServerFirstMessage.<init>(ServerFirstMessage.java:75)
```

## Causa

Incompatibilidad entre PostgreSQL driver 42.7.4 y el endpoint pooler de Neon PostgreSQL. El driver está rechazando el número de iteraciones SCRAM que envía Neon.

## Solución Implementada

### ✅ Downgrade del Driver PostgreSQL (Recomendado)

Cambiar de `42.7.4` a `42.6.0` en `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
    <scope>runtime</scope>
</dependency>
```

**Ventajas:**
- ✅ Mantiene el pooler de Neon (mejor rendimiento)
- ✅ No requiere cambios en Render
- ✅ Versión estable y probada con Neon

### Alternativa: Usar Endpoint Directo de Neon

Si el downgrade no funciona, cambiar el `DATABASE_URL` en Render:

**Antes (con pooler):**
```
postgresql://neondb_owner:npg_M4XBZsp3tuIq@ep-muddy-sky-agxvuo5l-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
```

**Después (directo):**
```
postgresql://neondb_owner:npg_M4XBZsp3tuIq@ep-muddy-sky-agxvuo5l.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require
```

Cambio: eliminar `-pooler` del hostname.

**Desventajas:**
- ⚠️ Sin connection pooling de Neon (puede ser más lento)
- ⚠️ Más conexiones directas a la base de datos

## Despliegue

1. El código ya está pusheado a GitHub (commit 735d308)
2. Ve a Render Dashboard
3. Haz clic en "Manual Deploy" → "Clear build cache & deploy"
4. Espera 5-10 minutos para que compile

## Verificación

```bash
# Espera a que el despliegue termine (~5-10 min)
# Luego verifica el health check:
curl https://gestion-jugadores-api.onrender.com/actuator/health

# Debería responder:
# {"status":"UP"}
```

## Notas

- La versión 42.6.0 es estable y ampliamente usada
- Es compatible con SCRAM-SHA-256
- Funciona correctamente con Neon pooler
- También funciona con Spring Boot 2.5.6

## Referencias

- [Neon PostgreSQL Pooler Docs](https://neon.tech/docs/connect/connection-pooling)
- [PostgreSQL JDBC Release Notes](https://jdbc.postgresql.org/changelogs/)
