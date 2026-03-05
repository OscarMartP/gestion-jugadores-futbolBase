# 🎯 Guía Paso a Paso - Primera Prueba de Análisis IA

## 📋 Pre-requisitos (verificar primero)

```powershell
# 1. Verificar que Docker está corriendo
docker ps

# 2. Verificar que MySQL está activo
docker ps --filter "name=mysql"

# 3. Verificar que tienes tu API key de OpenAI
# (la que empieza con sk-proj-...)
```

---

## 🚀 Paso 1: Arrancar el Backend con la API Key

```powershell
# En una terminal PowerShell
cd "C:\Users\Oscar\Downloads\gestion-jugadores-futbolBase-VersionMovil\gestion-jugadores-futbolBase-VersionMovil\gestion-jugadores-futbolBase"

# Configurar API key (reemplaza con tu clave real)
$env:OPENAI_API_KEY="sk-proj-TU-CLAVE-AQUI"

# Arrancar backend
mvn spring-boot:run
```

**Espera a ver:**
```
OpenAiAdapter inicializado con modelo: gpt-3.5-turbo
Started GestionJugadoresFutbolBaseApplication
```

---

## 📱 Paso 2: Arrancar la App Móvil

```powershell
# En OTRA terminal PowerShell
cd "C:\Users\Oscar\Downloads\gestion-jugadores-futbolBase-VersionMovil\gestion-jugadores-futbolBase-VersionMovil\gestion-jugadores-mobile"

ionic serve
```

**Se abrirá en:** `http://localhost:8100`

---

## ✅ Paso 3: Verificar que el Módulo IA Funciona

**En el navegador, ve a:**
```
http://localhost:8080/api/v1/ai/health
```

**Debe decir:**
```json
"Módulo de IA operativo - OpenAI configurado correctamente"
```

✅ Si ves esto → Todo listo para probar

❌ Si da error → La API key no está configurada correctamente

---

## 🎮 Paso 4: Probar el Análisis IA desde la App

1. **Abre la app móvil:** `http://localhost:8100`

2. **Inicia sesión** con tu usuario

3. **Ve a la pestaña "Jugadores"** (icono de personas)

4. **Busca un jugador que haya jugado el partido** (debe tener estadísticas)

5. **Click en el botón "⚡ Análisis IA"**

6. **Verás loading:**
   ```
   🤖 Analizando rendimiento con IA...
   ```

7. **En 2-5 segundos, se abre un modal con:**
   ```
   ╔═══════════════════════════════╗
   ║ ⚡ Análisis con IA        ✕  ║
   ╠═══════════════════════════════╣
   ║ 👤 [Nombre Jugador]           ║
   ║ [Posición] 📅 2024/2025      ║
   ║ X partidos · Y min            ║
   ║                               ║
   ║ 📈 Análisis Técnico           ║
   ║ ─────────────────────         ║
   ║ [Texto generado por IA        ║
   ║  con análisis profesional     ║
   ║  del rendimiento...]          ║
   ║                               ║
   ║ 💡 Generado con IA            ║
   ║ 🕐 [Fecha y hora]            ║
   ║                               ║
   ║ [      Cerrar      ]          ║
   ╚═══════════════════════════════╝
   ```

---

## 📊 Paso 5: Verificar Coste y Logs

**En la terminal del backend verás:**
```
INFO - Generando informe de jugador ID: X, temporada: 2024/2025
INFO - Estadísticas encontradas: X partidos, Y minutos
INFO - Llamando a OpenAI con prompt optimizado
INFO - Respuesta recibida de OpenAI
INFO - Tokens utilizados - Prompt: ~250, Completion: ~350, Total: ~600
INFO - Coste estimado: $0.0006
```

**Coste real:** Menos de 1 décima de céntimo por análisis 💰

---

## 🐛 Si Algo Falla...

### ❌ Error: "El jugador no tiene estadísticas"

**Causa:** El jugador no jugó ningún partido en la temporada 2024/2025

**Solución:** 
- Prueba con otro jugador que SÍ haya jugado
- Verifica que finalizaste el partido (no solo lo cancelaste)

---

### ❌ Error: "Error al comunicarse con OpenAI"

**Causa:** API key incorrecta o no configurada

**Solución:**
```powershell
# Verifica que la API key está configurada
echo $env:OPENAI_API_KEY

# Si está vacía, configúrala de nuevo:
$env:OPENAI_API_KEY="sk-proj-TU-CLAVE-REAL"

# Reinicia el backend (Ctrl+C y mvn spring-boot:run)
```

---

### ❌ Error: "No se pudo conectar con el servidor"

**Causa:** Backend no está corriendo

**Solución:** 
- Verifica que el backend está arrancado y en el puerto 8080
- Ejecuta: `mvn spring-boot:run` en la carpeta del backend

---

### ❌ Error 401 Unauthorized

**Causa:** Los endpoints de IA requieren autenticación

**Solución:** El backend ya está configurado para permitir acceso público a `/api/v1/ai/**`, solo necesitas reiniciarlo.

---

## 💡 Prueba Avanzada (Opcional)

Si quieres probar **sin la app móvil**, usa curl:

```powershell
# Reemplaza {ID} con el ID de un jugador real
curl -X POST "http://localhost:8080/api/v1/ai/jugador/{ID}/informe?temporada=2024/2025"
```

Verás el JSON completo con el análisis.

---

## 🎯 Checklist de Tu Primera Prueba

- [ ] Docker corriendo
- [ ] MySQL corriendo  
- [ ] API key de OpenAI obtenida
- [ ] Backend arrancado con `$env:OPENAI_API_KEY`
- [ ] Health check OK (`/api/v1/ai/health`)
- [ ] App móvil corriendo (`ionic serve`)
- [ ] Partido finalizado con estadísticas
- [ ] Jugador con minutos jugados
- [ ] Click en "⚡ Análisis IA"
- [ ] Modal abierto con análisis
- [ ] Logs del backend muestran coste

---

## 💰 Recordatorio de Costes

**Por cada análisis de jugador:**
- Tokens aproximados: 600
- Coste: ~$0.0006 (menos de 1 décima de céntimo)

**Tu uso semanal estimado:**
- 1 partido con 15 jugadores = 15 análisis
- Coste semanal: ~$0.009 (menos de 1 céntimo)
- Coste mensual: ~$0.04 (4 céntimos)

---

## 📂 Backup de Datos

**Antes de hacer pruebas, haz un backup:**

```powershell
cd "C:\Users\Oscar\Downloads\gestion-jugadores-futbolBase-VersionMovil"

docker exec mysql_control_jugadores mysqldump -u root -p1234 control_jugadores > backup_antes_IA_$(Get-Date -Format 'yyyyMMdd_HHmmss').sql
```

**Para restaurar el backup si algo sale mal:**

```powershell
docker exec -i mysql_control_jugadores mysql -u root -p1234 control_jugadores < backup_antes_IA_FECHA.sql
```

---

## 🔄 Para Mañana - Arranque Rápido

```powershell
# Terminal 1: Docker
docker compose up -d

# Terminal 2: Backend
cd gestion-jugadores-futbolBase
$env:OPENAI_API_KEY="tu-clave-aqui"
mvn spring-boot:run

# Terminal 3: App Móvil
cd gestion-jugadores-mobile
ionic serve
```

---

## 📝 Notas Importantes

- La API key se pierde al cerrar la terminal, debes configurarla cada vez con `$env:OPENAI_API_KEY`
- Si quieres que sea permanente, agrégala a `application.properties`
- El health check es crucial: siempre verifica que funcione antes de probar
- Los análisis se generan en tiempo real, no se guardan en BD (son bajo demanda)
- Cada click en "Análisis IA" consume tokens y cuesta dinero

---

## ✅ Próximos Pasos (Para Mañana)

1. Realizar primera prueba de análisis IA
2. Verificar calidad del análisis generado
3. Ajustar prompts si es necesario
4. Probar con diferentes tipos de jugadores (portero, defensa, delantero)
5. Verificar costes reales en el dashboard de OpenAI

---

**¡Todo listo para mañana!** 🚀

Fecha de creación: 12/02/2026
Última actualización: 12/02/2026
