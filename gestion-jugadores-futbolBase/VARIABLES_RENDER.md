# Variables de Entorno para Render

Después de los últimos cambios, debes configurar las siguientes variables de entorno en Render:

## Variables REQUERIDAS (Cambio importante)

**ANTES usabas:**
- `DATABASE_URL`

**AHORA debes usar:**

### 1. SPRING_DATASOURCE_URL
```
jdbc:postgresql://ep-muddy-sky-agxvuo51.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require
```
**IMPORTANTE:** Ahora **SÍ lleva el prefijo `jdbc:`**

### 2. SPRING_DATASOURCE_USERNAME
```
neondb_owner
```

### 3. SPRING_DATASOURCE_PASSWORD
```
npg_M4XBZsp3tuIq
```

---

## Otras Variables (sin cambios)

```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
JWT_SECRET=sz97eem>=`LK$s|)q^D2zXg(dqB+_IOR*4*25]=Eyzi%I:K5ZIo1y]lq+/y^+SeG
OPENAI_API_KEY=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
CORS_ALLOWED_ORIGINS=http://localhost:8100,http://localhost:4200,http://localhost:8101
```

---

## Pasos en Render:

1. **Elimina** la variable `DATABASE_URL` (si existe)
2. **Crea** las 3 nuevas variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
3. Guarda los cambios
4. **Manual Deploy** → **Clear build cache & deploy**

---

## ¿Por qué este cambio?

Spring Boot interpreta mejor las variables específicas `SPRING_DATASOURCE_*` que la genérica `DATABASE_URL`, evitando problemas de parsing con drivers modernos.
