# ✅ Checklist Pre-Despliegue Railway

## 📦 Código Listo
- [x] Spring Boot 2.7.18
- [x] PostgreSQL 42.7.3
- [x] MySQL eliminado
- [x] DATABASE_URL configurado
- [x] Dockerfile optimizado
- [x] dependencyManagement para PostgreSQL

## 🔐 Variables a Configurar en Railway

Copiar y pegar estas en Railway → Variables:

```bash
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=7k9mP2xR5n0Bwl3jF6vBicZ4hN0yT9sA5eGBiD2oU7qW3mX6rKIpL9
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:8100
OPENAI_API_KEY=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
```

**NO agregar:**
- DATABASE_URL (automática)

## 🚀 Configuración Railway

```
Branch: VersionMobilPro-IA
Root Directory: gestion-jugadores-futbolBase
```

## 📝 Pasos Rápidos

1. [ ] Repositorio público (temporal)
2. [ ] Login Railway (cuenta nueva)
3. [ ] Deploy from GitHub
4. [ ] Agregar PostgreSQL
5. [ ] Configurar 5 variables
6. [ ] Esperar deploy (5-10 min)
7. [ ] Generate Domain
8. [ ] Test: /actuator/health
9. [ ] Repo privado (opcional)

## ✅ Todo está listo para desplegar
