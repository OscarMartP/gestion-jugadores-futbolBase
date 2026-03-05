# 🧠 Módulo de Análisis con Inteligencia Artificial

## Descripción

Este módulo integra capacidades de Inteligencia Artificial (OpenAI) en la aplicación de gestión de jugadores de fútbol base. Permite generar informes técnicos profesionales automáticos de jugadores y partidos basados en estadísticas reales.

## ✨ Funcionalidades

### 1. Informe Técnico de Jugador
Genera un análisis técnico profesional de un jugador específico que incluye:
- Evaluación de rendimiento general
- Puntos fuertes principales
- Áreas de mejora específicas
- Conclusión orientada al desarrollo del jugador

### 2. Informe Técnico de Partido
Genera un análisis del partido que incluye:
- Resumen táctico del partido
- Puntos destacados (aspectos positivos)
- Áreas de mejora para entrenamientos

---

## 🏗️ Arquitectura

El módulo sigue **arquitectura hexagonal** (puertos y adaptadores) para mantener el código limpio y desacoplado:

```
ai/
├── port/                    # Interfaces (contratos)
│   └── AiAnalysisPort.java
├── adapter/                 # Implementaciones externas
│   └── OpenAiAdapter.java
├── service/                 # Lógica de negocio
│   ├── AiAnalysisService.java
│   └── impl/
│       └── AiAnalysisServiceImpl.java
├── dto/                     # Objetos de transferencia de datos
│   ├── InformeJugadorDTO.java
│   ├── InformePartidoDTO.java
│   ├── OpenAiRequest.java
│   └── OpenAiResponse.java
└── [controlador incluido en paquete controlador general]
```

### Ventajas de esta arquitectura:
✅ **Testeable**: Puerto puede ser mockeado fácilmente  
✅ **Sustituible**: Fácil cambiar de OpenAI a otro proveedor  
✅ **Limpio**: Lógica de negocio no depende de infraestructura  
✅ **Escalable**: Fácil añadir nuevas funcionalidades  
✅ **Enterprise-style**: Patrón profesional reconocido en la industria  

---

## 🚀 Configuración

### 1. Obtener API Key de OpenAI

1. Ve a [https://platform.openai.com/api-keys](https://platform.openai.com/api-keys)
2. Crea una cuenta o inicia sesión
3. Genera una nueva API key
4. **IMPORTANTE**: Guarda la clave de forma segura (solo se muestra una vez)

### 2. Configurar la API Key

#### Opción A: Variable de entorno (RECOMENDADO para producción)

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="tu-clave-aqui"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY="tu-clave-aqui"
```

#### Opción B: Archivo application.properties (solo desarrollo)

Edita `src/main/resources/application.properties`:
```properties
openai.api.key=tu-clave-aqui
openai.api.model=gpt-3.5-turbo
```

⚠️ **NUNCA subas tu API key a Git**. Asegúrate de que `application.properties` esté en `.gitignore` si contiene claves reales.

### 3. Modelos disponibles

Puedes cambiar el modelo en `application.properties`:

- `gpt-3.5-turbo` (recomendado) - Rápido y económico
- `gpt-4` - Más potente pero más costoso
- `gpt-4-turbo` - Balance entre potencia y velocidad

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/v1/ai
```

### 1. Health Check
Verifica que el módulo de IA está operativo.

**Endpoint:**
```http
GET /api/v1/ai/health
```

**Respuesta:**
```json
"Módulo de IA operativo - OpenAI configurado correctamente"
```

---

### 2. Generar Informe de Jugador

**Endpoint:**
```http
POST /api/v1/ai/jugador/{id}/informe?temporada=2024/2025
```

**Parámetros:**
- `id` (path, requerido): ID del jugador
- `temporada` (query, opcional): Temporada a analizar. Default: `2024/2025`

**Ejemplo de petición:**
```bash
curl -X POST "http://localhost:8080/api/v1/ai/jugador/1/informe?temporada=2024/2025"
```

**Respuesta exitosa (200):**
```json
{
  "jugadorId": 1,
  "nombreCompleto": "Juan Pérez",
  "posicion": "MC",
  "temporada": "2024/2025",
  "analisisTecnico": "Juan Pérez muestra un rendimiento sólido como mediocampista central..."
}
```

---

### 3. Generar Informe de Partido

**Endpoint:**
```http
POST /api/v1/ai/partido/{id}/informe
```

**Parámetros:**
- `id` (path, requerido): ID del partido

**Ejemplo de petición:**
```bash
curl -X POST "http://localhost:8080/api/v1/ai/partido/5/informe"
```

**Respuesta exitosa (200):**
```json
{
  "partidoId": 5,
  "titulo": "Partido vs Rival FC",
  "fecha": "2024-11-15T18:00:00",
  "resultado": "Victoria",
  "resumenTactico": "El equipo mostró un desempeño sólido...",
  "puntosDestacados": "- Presión alta efectiva...",
  "areasMejora": "- Mejorar salida de balón..."
}
```

---

## 💰 Costes y Consumo

### ¿Cómo se cobra?

OpenAI cobra por **tokens** procesados:
- **1 token ≈ 0.75 palabras** (aprox.)
- Se cuentan tokens de entrada (prompt) + salida (respuesta)

### Costes aproximados (gpt-3.5-turbo)

| Acción | Tokens aprox. | Coste aprox. |
|--------|---------------|--------------|
| Informe jugador | 600-800 | ~$0.001 |
| Informe partido | 800-1000 | ~$0.002 |

### Estimación de uso

| Escenario | Informes/mes | Coste/mes |
|-----------|--------------|-----------|
| Desarrollo/pruebas | ~100 | ~$0.10-0.20 |
| Uso equipo (1 equipo) | ~200-500 | ~$0.30-1.00 |
| Uso multiequipo | ~1000-5000 | ~$2-10 |

**Conclusión**: El coste es MUY bajo para proyectos personales o pequeños clubes.

### Control de costes

El código implementa las siguientes medidas:

1. **Límite de tokens**: 
   - Jugador: 600 tokens máximo
   - Partido: 800 tokens máximo

2. **Prompts optimizados**: Solo se envían estadísticas agregadas

3. **Logging de consumo**: Se registra el uso de tokens en logs

4. **Cache (futuro)**: Recomendación para guardar informes generados

---

## 🧪 Testing

### Test Manual

1. **Health Check**:
```bash
curl http://localhost:8080/api/v1/ai/health
```

2. **Informe de prueba**:
```bash
curl -X POST "http://localhost:8080/api/v1/ai/jugador/1/informe"
```

### Test desde Swagger UI

1. Inicia la aplicación
2. Ve a: `http://localhost:8080/swagger-ui.html`
3. Busca la sección "Análisis con IA"
4. Prueba los endpoints directamente desde la interfaz

---

## 🔒 Seguridad

### Buenas prácticas implementadas:

✅ API key nunca va en código fuente  
✅ Uso de variables de entorno  
✅ Límite de tokens para evitar abusos  
✅ Logging de errores sin exponer claves  
✅ CORS configurado correctamente  

### Recomendaciones adicionales:

- **Producción**: SIEMPRE usar variable de entorno
- **Límite de uso**: Considera implementar rate limiting
- **Monitoreo**: Revisa logs de consumo regularmente
- **Rotación**: Cambia la API key periódicamente

---

## 🐛 Troubleshooting

### Error: "Error al comunicarse con OpenAI"

**Causas comunes:**
1. API key no configurada o inválida
2. Sin créditos en cuenta OpenAI
3. Problema de conexión a internet
4. API de OpenAI temporalmente caída

**Solución:**
```bash
# Verifica que la key esté configurada
echo $env:OPENAI_API_KEY  # Windows PowerShell
echo $OPENAI_API_KEY      # Linux/Mac

# Revisa los logs
tail -f logs/application.log
```

### Error: "Jugador no encontrado"

El jugador no existe o no tiene estadísticas en esa temporada.

### Respuesta lenta

- Primera llamada suele ser más lenta (cold start)
- OpenAI puede tener latencia variable
- Considera aumentar timeouts en producción

---

## 🚀 Roadmap Futuro

Posibles mejoras para implementar:

- [ ] **Cache de informes**: Guardar en BD para no regenerar
- [ ] **Comparador de jugadores**: Análisis comparativo con IA
- [ ] **Chat entrenador**: Preguntas en lenguaje natural
- [ ] **Sugerencias tácticas**: Recomendaciones de alineación
- [ ] **Análisis de video**: Integración con transcripción de partidos
- [ ] **Multi-idioma**: Informes en varios idiomas
- [ ] **Informes programados**: Generación automática semanal/mensual

---

## 📚 Recursos

### Documentación oficial:
- [OpenAI API Docs](https://platform.openai.com/docs/api-reference)
- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Arquitectura Hexagonal](https://alistair.cockburn.us/hexagonal-architecture/)

### Ejemplos de prompts:
Ver código en `AiAnalysisServiceImpl.java` métodos:
- `construirPromptJugador()`
- `construirPromptPartido()`

---

## 👨‍💻 Autor y Contribuciones

**Desarrollado por**: Oscar (DAW)  
**Proyecto**: Gestión Jugadores Fútbol Base  
**Fecha**: 2024  

### Arquitectura y decisiones técnicas:

Este módulo demuestra:
- ✅ Integración real de IA en aplicación Spring Boot
- ✅ Arquitectura hexagonal aplicada correctamente
- ✅ Código limpio, documentado y profesional
- ✅ Enfoque práctico para portfolio y entrevistas

---

## 📄 Licencia

Este módulo es parte del proyecto de gestión de jugadores de fútbol base.

---

## 📞 Soporte

Para dudas o problemas:
1. Revisa la sección Troubleshooting
2. Consulta los logs en `logs/application.log`
3. Verifica la documentación de OpenAI

---

**¡El módulo de IA está listo para usar! 🎉**
