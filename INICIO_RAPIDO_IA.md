# 🚀 Guía de Inicio Rápido - Módulo IA

## ⚡ Pasos para Empezar (5 minutos)

### 1️⃣ Obtener API Key de OpenAI

1. Ve a: https://platform.openai.com/api-keys
2. Inicia sesión o regístrate
3. Click en "Create new secret key"
4. Copia la clave (empieza con `sk-...`)
5. **¡GUÁRDALA! Solo se muestra una vez**

---

### 2️⃣ Configurar API Key

**Windows PowerShell:**
```powershell
$env:OPENAI_API_KEY="sk-tu-clave-aqui"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY="sk-tu-clave-aqui"
```

---

### 3️⃣ Compilar el Proyecto

```bash
cd gestion-jugadores-futbolBase
mvn clean install
```

---

### 4️⃣ Arrancar la Aplicación

```bash
mvn spring-boot:run
```

Espera a ver en consola:
```
Started GestionJugadoresFutbolBaseApplication in X seconds
```

---

### 5️⃣ Probar el Módulo

**Opción A: Con curl (terminal)**

```bash
# Health check
curl http://localhost:8080/api/v1/ai/health

# Generar informe de jugador (reemplaza {id} con un ID real)
curl -X POST "http://localhost:8080/api/v1/ai/jugador/1/informe?temporada=2024/2025"

# Generar informe de partido (reemplaza {id} con un ID real)
curl -X POST "http://localhost:8080/api/v1/ai/partido/1/informe"
```

**Opción B: Con Swagger UI (navegador)**

1. Abre: http://localhost:8080/swagger-ui.html
2. Busca sección **"Análisis con IA"**
3. Expande un endpoint
4. Click "Try it out"
5. Ingresa ID del jugador/partido
6. Click "Execute"
7. ¡Ver resultado! ✨

---

## 🎯 Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/ai/health` | Verificar que IA está activa |
| POST | `/api/v1/ai/jugador/{id}/informe` | Informe de jugador |
| POST | `/api/v1/ai/partido/{id}/informe` | Informe de partido |

---

## 🐛 Solución Rápida de Problemas

### ❌ Error: "Error al comunicarse con OpenAI"

**Causa:** API key no configurada

**Solución:**
```powershell
# Verifica que esté configurada
echo $env:OPENAI_API_KEY  # PowerShell
echo $OPENAI_API_KEY      # Linux/Mac

# Si no aparece nada, configúrala:
$env:OPENAI_API_KEY="sk-tu-clave-aqui"
```

### ❌ Error: "Jugador no encontrado"

**Causa:** ID no existe en la base de datos

**Solución:** Usa un ID de jugador que exista. Puedes verificar con:
```bash
curl http://localhost:8080/api/v1/jugadores
```

### ❌ Error de compilación

**Solución:**
```bash
mvn clean install -U
```

---

## 💡 Ejemplo de Respuesta Exitosa

### Informe de Jugador

```json
{
  "jugadorId": 1,
  "nombreCompleto": "Juan Pérez",
  "posicion": "MC",
  "temporada": "2024/2025",
  "analisisTecnico": "Juan Pérez demuestra un rendimiento sólido como mediocampista central en la temporada 2024/2025. Con buena participación en 12 partidos (10 como titular) acumulando 850 minutos, destaca por su aporte ofensivo con 5 goles y 3 asistencias. Sus 4.2 pases clave por 90 minutos muestran visión de juego. En el aspecto defensivo, registra 2.8 robos por partido sugiriendo intensidad adecuada. Sin embargo, las 3.5 pérdidas por 90 minutos indican margen de mejora en la toma de decisiones bajo presión. Durante el desarrollo, se recomienda trabajar retención del balón en zonas críticas y decisiones más rápidas en transiciones."
}
```

### Informe de Partido

```json
{
  "partidoId": 5,
  "titulo": "Partido vs Rival FC",
  "fecha": "2024-11-15T18:00:00",
  "resultado": "Victoria",
  "resumenTactico": "**PUNTOS DESTACADOS:**\n- Presión alta efectiva en los primeros 30 minutos\n- 8 tiros a puerta demuestran intención ofensiva\n- 6 pases clave generando ocasiones claras\n- Solidez defensiva con 12 robos\n\n**ÁREAS DE MEJORA:**\n- Reducir pérdidas en campo propio (9 pérdidas detectadas)\n- Mejorar circulación en últimos 15 minutos cuando se iba ganando\n- Trabajo en retención bajo presión rival",
  "puntosDestacados": "- Presión alta efectiva en los primeros 30 minutos\n- 8 tiros a puerta demuestran intención ofensiva\n- 6 pases clave generando ocasiones claras",
  "areasMejora": "- Reducir pérdidas en campo propio (9 pérdidas detectadas)\n- Mejorar circulación en últimos 15 minutos cuando se iba ganando"
}
```

---

## 📚 Documentación Completa

Para información detallada, consulta:

- **`MODULO_IA_README.md`** - Documentación completa del módulo
- **`VARIABLES_ENTORNO.md`** - Guía de configuración avanzada
- **`RESUMEN_IMPLEMENTACION_IA.md`** - Detalles técnicos de la implementación

---

## 🎯 Próximos Pasos

1. ✅ Probar endpoints con datos reales de tu base de datos
2. ✅ Integrar en tu aplicación móvil Ionic
3. ✅ Añadir botón "Generar Análisis IA" en vista de jugadores
4. ✅ Mostrar informes en la app móvil

---

## 💰 Coste Estimado de Pruebas

- **10 informes**: ~$0.01
- **100 informes**: ~$0.10
- **1000 informes**: ~$1.00

**Conclusión:** Puedes probar tranquilamente sin preocuparte por costes.

---

## 🆘 ¿Necesitas Ayuda?

1. Revisa los logs: `logs/application.log`
2. Consulta `MODULO_IA_README.md`
3. Verifica configuración en `application.properties`
4. Prueba health check: `curl http://localhost:8080/api/v1/ai/health`

---

## ✅ Checklist de Inicio

- [ ] Obtuve mi API key de OpenAI
- [ ] Configuré la variable de entorno `OPENAI_API_KEY`
- [ ] Compilé el proyecto con `mvn clean install`
- [ ] Arranqué la aplicación con `mvn spring-boot:run`
- [ ] Probé el health check
- [ ] Generé mi primer informe con IA
- [ ] Revisé el resultado en Swagger UI
- [ ] ¡Funciona! 🎉

---

**¡Listo para usar el módulo de IA! 🚀**

*Si todo funciona, ya tienes una aplicación con Inteligencia Artificial integrada.*
