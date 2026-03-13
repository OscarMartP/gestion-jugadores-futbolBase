# Configuración Final para Render

## Problema Resuelto

El `DataSourceConfig.java` estaba agregando un segundo prefijo `jdbc:` a la URL, creando:
```
jdbc:jdbc:postgresql://...  ← DOBLE PREFIJO (ERROR)
```

**Solución:** Eliminado `DataSourceConfig.java` y configuración directa en `application-prod.properties`.

---

## Variables de Entorno en Render

Ve a tu servicio en Render → **Environment** y configura:

### DATABASE_URL
**IMPORTANTE:** La URL debe empezar con `postgresql://` (SIN `jdbc:`)

```bash
postgresql://neondb_owner:npg_M4XBZsp3tuIq@ep-muddy-sky-agxvuo51.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require
```

**Nota:** Sin `-pooler` en el hostname (endpoint directo)

### Otras Variables Requeridas

```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
JWT_SECRET=sz97eem>=`LK$s|)q^D2zXg(dqB+_IOR*4*25]=Eyzi%I:K5ZIo1y]lq+/y^+SeG
OPENAI_API_KEY=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
CORS_ALLOWED_ORIGINS=http://localhost:8100,http://localhost:4200,http://localhost:8101
```

---

## Pasos para Desplegar

1. **Asegúrate de que la variable `DATABASE_URL` empiece con `postgresql://`** (NO con `jdbc:`)
2. Ve a tu servicio en Render Dashboard
3. Clic en **"Manual Deploy"** → **"Clear build cache & deploy"**
4. Espera 5-10 minutos

---

## Verificación

Una vez desplegado, verifica con:

```bash
curl https://tu-servicio.onrender.com/actuator/health
```

Deberías ver:
```json
{"status":"UP"}
```

---

## Cambios Realizados

- ✅ Eliminado `DataSourceConfig.java` (causaba doble prefijo)
- ✅ Configuración directa en `application-prod.properties`
- ✅ Driver PostgreSQL 42.2.27
- ✅ Endpoint directo de Neon (sin pooler)
