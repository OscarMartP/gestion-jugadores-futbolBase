# Plan de Validación de Estadísticas - Pre-Producción

## ✅ Tests Unitarios Implementados

Se han creado **tests unitarios exhaustivos** en:
- **Archivo**: `gestion-jugadores-futbolBase/src/test/java/com/gestion/jugadores/servicios/EstadisticasServiceImplTest.java`

### Tests Implementados:

#### 1. **Tests de Cálculo de Goles** ✓
- ✅ `testCalculoGolesCorrectamente()` - Verifica que se cuentan todos los goles
- ✅ `testDistribucionTemporalGoles()` - Verifica distribución en intervalos de 15 min
- ✅ `testDistribucionGolesEnLimitesIntervalos()` - Prueba casos límite (min 0, 15, 45, 90)

#### 2. **Tests de Asistencias** ✓
- ✅ `testCalculoAsistenciasCorrectamente()` - Verifica conteo de asistencias

#### 3. **Tests de Tarjetas** ✓
- ✅ `testCalculoTarjetasAmarillasYRojas()` - Verifica amarillas y rojas

#### 4. **Tests de Pases Clave** ✓
- ✅ `testCalculoPasesClave()` - Verifica conteo total
- ✅ `testDistribucionTemporalPasesClave()` - Verifica distribución temporal (6 intervalos)

#### 5. **Tests de Tiros a Puerta** ✓
- ✅ `testCalculoTirosAPuerta()` - Verifica conteo de tiros
- ✅ `testGolesYTirosAPuertaSeSumanCorrectamente()` - **CRÍTICO**: Verifica que goles también cuentan como tiros
- ✅ `testDistribucionTemporalTirosAPuerta()` - Verifica distribución temporal

#### 6. **Tests de Robos y Pérdidas** ✓
- ✅ `testCalculoRobos()` - Verifica conteo de robos
- ✅ `testDistribucionTemporalRobos()` - Verifica distribución temporal
- ✅ `testCalculoPerdidas()` - Verifica conteo de pérdidas

#### 7. **Tests de Escenarios Completos** ✓
- ✅ `testEscenarioPartidoCompleto()` - Simula partido con múltiples eventos variados
- ✅ `testMultiplesPartidos()` - Verifica acumulación entre varios partidos
- ✅ `testJugadorSinEventos()` - Verifica que estadísticas en 0 funcionan

#### 8. **Tests de Estadísticas de Partido** ✓
- ✅ `testObtenerEstadisticasPartido()` - Verifica DTO de estadísticas de partido individual

---

## 🚀 Cómo Ejecutar los Tests

### Opción 1: Maven (Línea de comandos)
```powershell
cd gestion-jugadores-futbolBase
mvn test -Dtest=EstadisticasServiceImplTest
```

### Opción 2: Ejecutar un test específico
```powershell
mvn test -Dtest=EstadisticasServiceImplTest#testEscenarioPartidoCompleto
```

### Opción 3: Todos los tests del proyecto
```powershell
mvn test
```

### Opción 4: Desde VS Code
- Abrir el archivo `EstadisticasServiceImplTest.java`
- Click derecho en la clase o método
- Seleccionar "Run Test" o "Debug Test"

---

## 📊 Validaciones que Realizan los Tests

### ✅ Validaciones de Cálculo Correcto:
- [x] Goles se cuentan correctamente
- [x] Asistencias se acumulan
- [x] Tarjetas amarillas y rojas se diferencian
- [x] Pases clave se contabilizan
- [x] **Tiros a puerta incluyen goles** (importante)
- [x] Robos y pérdidas se registran

### ✅ Validaciones de Distribución Temporal:
- [x] Intervalo 0-15 minutos
- [x] Intervalo 16-30 minutos
- [x] Intervalo 31-45 minutos
- [x] Intervalo 46-60 minutos
- [x] Intervalo 61-75 minutos
- [x] Intervalo 76-90 minutos
- [x] Casos límite (minuto 0, 15, 45, 90)

### ✅ Validaciones de Escenarios:
- [x] Partido completo con eventos variados
- [x] Múltiples partidos acumulan correctamente
- [x] Jugador sin eventos (estadísticas en 0)
- [x] Diferentes formatos de nombres de eventos

---

## 🔍 Validación Manual Adicional Recomendada

### 1. Validación con Datos Reales en Base de Datos

#### Paso 1: Crear datos de prueba
```sql
-- Insertar equipo de prueba
INSERT INTO equipos (nombre) VALUES ('Equipo Validación');

-- Insertar jugadores
INSERT INTO jugadores (nombre, apellido, equipo_id) 
VALUES ('Test', 'Jugador1', (SELECT id FROM equipos WHERE nombre='Equipo Validación'));

-- Insertar partido
INSERT INTO partidos (titulo, fecha, equipo_id, resultado, goles_equipo, goles_rival, duracion, partido_activo)
VALUES ('Partido Test', NOW(), (SELECT id FROM equipos WHERE nombre='Equipo Validación'), 'Victoria', 3, 1, 90, false);

-- Insertar eventos variados
INSERT INTO eventos_jugador (jugador_id, partido_id, tipo_evento, minuto)
VALUES 
  ((SELECT id FROM jugadores WHERE nombre='Test'), (SELECT id FROM partidos WHERE titulo='Partido Test'), 'GOL', 15),
  ((SELECT id FROM jugadores WHERE nombre='Test'), (SELECT id FROM partidos WHERE titulo='Partido Test'), 'GOL', 45),
  ((SELECT id FROM jugadores WHERE nombre='Test'), (SELECT id FROM partidos WHERE titulo='Partido Test'), 'ASISTENCIA', 30),
  ((SELECT id FROM jugadores WHERE nombre='Test'), (SELECT id FROM partidos WHERE titulo='Partido Test'), 'TIRO_A_PUERTA', 25),
  ((SELECT id FROM jugadores WHERE nombre='Test'), (SELECT id FROM partidos WHERE titulo='Partido Test'), 'PASE_CLAVE', 10),
  ((SELECT id FROM jugadores WHERE nombre='Test'), (SELECT id FROM partidos WHERE titulo='Partido Test'), 'PASE_CLAVE', 55);
```

#### Paso 2: Ejecutar cálculo de estadísticas
```java
// A través de la aplicación o endpoint REST
estadisticasService.actualizarEstadisticasJugador(jugadorId, "2025-2026");
```

#### Paso 3: Verificar resultados con SQL
```sql
-- Verificar que los totales coinciden con los eventos
SELECT 
    j.nombre,
    ej.total_goles,
    ej.total_asistencias,
    ej.total_tiros_a_puerta,
    ej.total_pases_clave,
    COUNT(DISTINCT CASE WHEN ev.tipo_evento = 'GOL' THEN ev.id END) as eventos_goles,
    COUNT(DISTINCT CASE WHEN ev.tipo_evento = 'ASISTENCIA' THEN ev.id END) as eventos_asistencias,
    COUNT(DISTINCT CASE WHEN ev.tipo_evento IN ('TIRO_A_PUERTA', 'GOL') THEN ev.id END) as eventos_tiros
FROM estadisticas_jugadores ej
JOIN jugadores j ON j.id = ej.jugador_id
LEFT JOIN eventos_jugador ev ON ev.jugador_id = j.id
WHERE j.nombre = 'Test'
GROUP BY j.nombre, ej.total_goles, ej.total_asistencias, ej.total_tiros_a_puerta, ej.total_pases_clave;

-- Verificar distribución temporal
SELECT 
    j.nombre,
    ej.pases_clave_0_15,
    ej.pases_clave_16_30,
    ej.pases_clave_31_45,
    ej.pases_clave_46_60,
    ej.pases_clave_61_75,
    ej.pases_clave_76_90,
    COUNT(CASE WHEN ev.minuto BETWEEN 0 AND 15 AND ev.tipo_evento = 'PASE_CLAVE' THEN 1 END) as pases_reales_0_15,
    COUNT(CASE WHEN ev.minuto BETWEEN 16 AND 30 AND ev.tipo_evento = 'PASE_CLAVE' THEN 1 END) as pases_reales_16_30
FROM estadisticas_jugadores ej
JOIN jugadores j ON j.id = ej.jugador_id
LEFT JOIN eventos_jugador ev ON ev.jugador_id = j.id
WHERE j.nombre = 'Test'
GROUP BY j.nombre, ej.pases_clave_0_15, ej.pases_clave_16_30, ej.pases_clave_31_45, 
         ej.pases_clave_46_60, ej.pases_clave_61_75, ej.pases_clave_76_90;
```

### 2. Checklist de Validación Manual

Antes de subir a producción, validar manualmente:

#### Estadísticas de Partido:
- [ ] Crear partido con eventos en tiempo real
- [ ] Verificar que estadísticas se actualizan en vivo
- [ ] Verificar que distribución temporal es correcta

#### Estadísticas de Temporada:
- [ ] Crear 3 partidos con eventos diferentes
- [ ] Verificar acumulación correcta entre partidos
- [ ] Verificar que goles + tiros a puerta suman correctamente

#### Casos Edge:
- [ ] Partido sin eventos (todos los valores en 0)
- [ ] Evento en minuto 0
- [ ] Evento en minuto 90
- [ ] Múltiples eventos en el mismo minuto
- [ ] Diferentes formatos de nombres de eventos (GOL/GOLES, TIRO_A_PUERTA/"TIRO A PUERTA")

#### Estadísticas de Equipo:
- [ ] Verificar que suma todos los jugadores
- [ ] Verificar cálculo de "Mayor Pasador"
- [ ] Verificar puntos (3 por victoria, 1 por empate, 0 por derrota)
- [ ] Verificar partidos ganados/empatados/perdidos

### 3. Tests de Rendimiento

```powershell
# Crear datos de prueba masivos
# 10 equipos x 20 jugadores x 30 partidos x 10 eventos = 60,000 eventos

# Medir tiempo de cálculo
Measure-Command {
    # Ejecutar actualización de estadísticas
    # estadisticasService.actualizarEstadisticasEquipo(equipoId, "2025-2026");
}

# Meta: < 5 segundos para 60,000 eventos
```

---

## 📝 Resultado Esperado de los Tests

### Todos los tests deben pasar (verde ✅):
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.gestion.jugadores.servicios.EstadisticasServiceImplTest
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🐛 Puntos Críticos a Verificar

### 1. **Los goles deben contar como tiros a puerta**
```java
// CORRECTO: Si hay 2 goles y 3 tiros a puerta
totalGoles = 2
totalTirosAPuerta = 5  // 2 + 3
```

### 2. **Distribución temporal debe respetar límites**
```java
// Minuto 15 → intervalo 0-15 ✓
// Minuto 16 → intervalo 16-30 ✓
// Minuto 90 → intervalo 76-90 ✓
```

### 3. **Eventos en diferentes formatos deben procesarse igual**
```java
"GOL" == "GOLES" ✓
"TIRO_A_PUERTA" == "TIRO A PUERTA" ✓
"PASE_CLAVE" == "PASE CLAVE" ✓
```

### 4. **Múltiples partidos deben acumular estadísticas**
```java
// 2 goles en partido 1 + 3 goles en partido 2 = 5 goles totales ✓
// partidosJugados debe contar partidos únicos ✓
```

---

## ✅ Checklist Pre-Producción

- [ ] **Todos los tests unitarios pasan** (17 tests)
- [ ] **Validación manual con datos reales en BD**
- [ ] **Verificación de SQL que estadísticas coinciden con eventos**
- [ ] **Prueba de casos edge (minuto 0, 90, sin eventos)**
- [ ] **Verificación de estadísticas de equipo**
- [ ] **Test de rendimiento con datos masivos**
- [ ] **Validación de estadísticas en frontend**
- [ ] **Logs de verificación sin errores**

---

## 🎯 Comandos Útiles

### Ejecutar todos los tests
```powershell
cd gestion-jugadores-futbolBase
mvn clean test
```

### Ejecutar solo tests de estadísticas
```powershell
mvn test -Dtest=EstadisticasServiceImplTest
```

### Ver coverage de tests
```powershell
mvn clean test jacoco:report
# Ver reporte en: target/site/jacoco/index.html
```

### Ejecutar aplicación en modo test
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

---

## 📞 Contacto y Soporte

Si algún test falla:
1. Revisar logs en consola
2. Verificar que el método `determinarEstadoMarcadorEnMinuto()` existe
3. Verificar que los repositorios están configurados correctamente
4. Ejecutar tests individuales para aislar problemas

**Estado**: ✅ Tests creados y listos para ejecución
**Fecha**: Febrero 10, 2026
