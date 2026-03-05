# ⚡ GUÍA RÁPIDA - Arrancar con IA (Mañana)

## 📝 Pasos Exactos para Mañana

### 1️⃣ Obtener API Key (5 minutos)

1. Ve a: **https://platform.openai.com/api-keys**
2. Click en **"Create new secret key"**
3. Copia la clave (empieza con `sk-...`)
4. **GUÁRDALA SEGURA** (solo se muestra una vez)

---

### 2️⃣ Configurar en Windows

**Abrir PowerShell:**

```powershell
# Configurar la API key
$env:OPENAI_API_KEY="sk-TU-CLAVE-AQUI"

# Verificar que está configurada
echo $env:OPENAI_API_KEY
```

---

### 3️⃣ Arrancar Backend

**En PowerShell (misma ventana):**

```powershell
cd C:\Users\Oscar\Downloads\gestion-jugadores-futbolBase-VersionMovil\gestion-jugadores-futbolBase-VersionMovil\gestion-jugadores-futbolBase

mvn spring-boot:run
```

**Espera a ver:**
```
OpenAiAdapter inicializado con modelo: gpt-3.5-turbo
Started GestionJugadoresFutbolBaseApplication
```

✅ **Backend listo**

---

### 4️⃣ Arrancar App Móvil

**Otra terminal/PowerShell:**

```powershell
cd C:\Users\Oscar\Downloads\gestion-jugadores-futbolBase-VersionMovil\gestion-jugadores-futbolBase-VersionMovil\gestion-jugadores-mobile

ionic serve
```

Se abrirá en: **http://localhost:8100**

---

### 5️⃣ Probar IA

1. Ve a **pestaña "Jugadores"**
2. Busca un jugador con estadísticas
3. Click en botón **"⚡ Análisis IA"**
4. Espera 2-5 segundos ⏳
5. **¡Modal con análisis!** 🎉

---

## 🐛 Si Algo No Funciona

### ❌ "Error al comunicarse con OpenAI"

**Causa:** API key no configurada o inválida

**Solución:**
```powershell
# Verifica que esté configurada
echo $env:OPENAI_API_KEY

# Si no aparece nada, configúrala de nuevo:
$env:OPENAI_API_KEY="sk-tu-clave"

# Reinicia el backend (Ctrl+C y mvn spring-boot:run)
```

---

### ❌ "El jugador no tiene estadísticas"

**Causa:** El jugador no tiene datos en esa temporada

**Solución:**
- Usa un jugador que haya jugado partidos
- O crea estadísticas de prueba primero

---

### ❌ "No se puede conectar con el servidor"

**Causa:** Backend no está corriendo

**Solución:**
```powershell
# Verifica que backend esté en http://localhost:8080
curl http://localhost:8080/api/v1/ai/health
```

Debería responder: `"Módulo de IA operativo - OpenAI configurado correctamente"`

---

## 💰 Monitorear Uso

En los logs del backend verás:

```
Tokens utilizados - Prompt: 250, Completion: 350, Total: 600
```

Eso significa **$0.001** (1 décima de céntimo) por informe.

---

## ✅ Test Rápido

**Health check desde navegador:**
```
http://localhost:8080/api/v1/ai/health
```

**Test manual completo:**
```bash
curl -X POST "http://localhost:8080/api/v1/ai/jugador/1/informe?temporada=2024/2025"
```

*(Reemplaza `1` con un ID de jugador real)*

---

## 🎯 Resultado Esperado

**En la app móvil verás:**

```
╔════════════════════════════╗
║ ⚡ Análisis con IA     ✕  ║
╠════════════════════════════╣
║                            ║
║ 👤 Juan Pérez              ║
║ [MC] 📅 2024/2025         ║
║                            ║
║ 📈 Análisis Técnico        ║
║ ───────────────────────    ║
║ Juan Pérez demuestra un    ║
║ rendimiento sólido como    ║
║ mediocampista central...   ║
║                            ║
║ 💡 Generado con IA         ║
║                            ║
║ [     Cerrar     ]         ║
╚════════════════════════════╝
```

---

## 📞 Si Necesitas Ayuda

1. Revisa logs: `logs/application.log`
2. Abre consola del navegador (F12)
3. Verifica que ambos servicios estén corriendo
4. Asegúrate de que hay jugadores con estadísticas

---

**¡Listo para mañana! Solo necesitas la API key de OpenAI.** 🚀

Todo el código ya está implementado y funcional.
