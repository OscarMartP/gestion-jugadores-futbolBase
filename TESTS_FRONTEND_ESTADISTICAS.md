# Tests del Frontend - Estadísticas

## ✅ Tests Creados para el Frontend Angular

Se han creado **3 archivos de tests exhaustivos** para validar las estadísticas en el frontend:

### 1. **EstadisticasService** (Servicio)
**Archivo**: `src/app/services/estadisticas.service.spec.ts`

#### Tests Implementados (18 tests):
- ✅ Creación del servicio
- ✅ Obtener estadísticas de jugador (con y sin temporada)
- ✅ Obtener estadísticas de todos los jugadores del equipo
- ✅ Obtener estadísticas del equipo
- ✅ Verificar cálculo correcto de puntos (victorias*3 + empates*1)
- ✅ Obtener resumen completo
- ✅ Top goleadores (con límite personalizable)
- ✅ Top asistentes (ordenados correctamente)
- ✅ **Estadísticas de partido individual**
- ✅ **Verificar eventos por jugador en partido**
- ✅ **Distribución temporal de eventos**
- ✅ Actualización de estadísticas (jugador y equipo)
- ✅ Cálculo de temporada actual
- ✅ Manejo de errores (404, 500)

---

### 2. **EstadisticasGeneralesComponent** (Componente General)
**Archivo**: `src/app/components/estadisticas-generales/estadisticas-generales.component.spec.ts`

#### Tests Implementados (22 tests):
- ✅ Creación del componente
- ✅ Carga inicial de equipos
- ✅ Cálculo de temporada actual
- ✅ Carga de estadísticas al seleccionar equipo
- ✅ Indicador de carga (loading spinner)
- ✅ Top goleadores ordenados
- ✅ **Verificar que partidos totales = ganados + empatados + perdidos**
- ✅ **Verificar que puntos = victorias*3 + empates*1**
- ✅ **Verificar diferencia de goles = favor - contra**
- ✅ **Verificar suma de distribución temporal de pases clave**
- ✅ Mostrar mayor pasador del equipo
- ✅ Selección de tipo de estadística (pases/tiros/robos)
- ✅ Control de visibilidad de secciones
- ✅ Manejo de errores

---

### 3. **EstadisticasEquipoComponent** (Componente de Equipo/Partido)
**Archivo**: `src/app/components/estadisticas-equipo/estadisticas-equipo.component.spec.ts`

#### Tests Implementados (24 tests):
- ✅ Creación del componente
- ✅ Obtener ID del equipo desde URL
- ✅ Cargar datos del equipo
- ✅ Establecer temporada actual
- ✅ Cargar resumen automático
- ✅ Estadísticas completas del equipo
- ✅ **Verificar cálculo de puntos**
- ✅ **Verificar diferencia de goles**
- ✅ Mayor pasador del equipo
- ✅ Estadísticas avanzadas (pases clave, tiros, robos)
- ✅ Cargar estadísticas de todos los jugadores
- ✅ Calcular promedios por partido
- ✅ Cambio de vistas (resumen/jugadores/tops)
- ✅ **Validación: partidos totales = ganados + empatados + perdidos**
- ✅ **Identificar máximo goleador**
- ✅ **Identificar máximo asistente**
- ✅ Comparar minutos jugados
- ✅ Cambio de temporada
- ✅ Manejo de errores

---

## 🚀 Cómo Ejecutar los Tests

### Paso 1: Instalar dependencias (si no están instaladas)
```powershell
cd gestion-jugadores-frontend
npm install
```

### Paso 2: Ejecutar todos los tests del proyecto
```powershell
npm test
```

### Paso 3: Ejecutar solo los tests de estadísticas
```powershell
npm test -- --include='**/estadistica*.spec.ts' --watch=false
```

### Paso 4: Ejecutar con coverage (recomendado)
```powershell
npm test -- --code-coverage --watch=false
```

### Paso 5: Ejecutar un archivo específico
```powershell
# Solo el servicio
npm test -- --include='**/estadisticas.service.spec.ts' --watch=false

# Solo componente general
npm test -- --include='**/estadisticas-generales.component.spec.ts' --watch=false

# Solo componente de equipo
npm test -- --include='**/estadisticas-equipo.component.spec.ts' --watch=false
```

---

## 📊 Resumen de Tests por Archivo

| Archivo | Tests | Descripción |
|---------|-------|-------------|
| **estadisticas.service.spec.ts** | 18 | Tests del servicio HTTP, llamadas API, manejo de parámetros |
| **estadisticas-generales.component.spec.ts** | 22 | Tests del componente general, cálculos, validaciones |
| **estadisticas-equipo.component.spec.ts** | 24 | Tests del componente de equipo, partido, comparaciones |
| **TOTAL** | **64 tests** | Cobertura completa de estadísticas frontend |

---

## ✅ Validaciones Críticas Implementadas

### Validaciones de Cálculo:
1. **Puntos del equipo** = Victorias × 3 + Empates × 1
2. **Partidos totales** = Ganados + Empatados + Perdidos
3. **Diferencia de goles** = Goles favor - Goles contra
4. **Distribución temporal** = Suma de intervalos = Total

### Validaciones de Estadísticas de Partido:
1. ✅ Goles totales coinciden con eventos
2. ✅ Asistencias coinciden con eventos
3. ✅ Distribución temporal de goles (6 intervalos)
4. ✅ Eventos por jugador completos
5. ✅ Tiros a puerta incluyen goles

### Validaciones de Jugadores:
1. ✅ Identificar máximo goleador
2. ✅ Identificar máximo asistente
3. ✅ Calcular promedios por partido
4. ✅ Ordenamiento correcto en tops

---

## 🔍 Ejemplos de Tests Importantes

### Test de Estadísticas de Partido Individual
```typescript
it('debería obtener estadísticas de un partido específico', () => {
  const mockPartido: EstadisticasPartidoDTO = {
    id: 1,
    totalGoles: 3,
    totalAsistencias: 2,
    eventosPorJugador: [
      { jugadorId: 1, goles: 2, asistencias: 1 }
    ],
    distribucionGoles: {
      intervalo0_15: 1,
      intervalo31_45: 1,
      intervalo61_75: 1
    }
  };
  
  // Verificar datos del partido
  // Verificar eventos por jugador
  // Verificar distribución temporal
});
```

### Test de Validación de Cálculos
```typescript
it('debería verificar que puntos = victorias*3 + empates*1', () => {
  const puntosCalculados = stats.partidosGanados * 3 + stats.partidosEmpatados * 1;
  expect(stats.puntos).toBe(puntosCalculados);
});
```

### Test de Distribución Temporal
```typescript
it('debería verificar suma de distribución temporal', () => {
  const sumaParcial = 
    stats.pasesClave0_15 + stats.pasesClave16_30 + 
    stats.pasesClave31_45 + stats.pasesClave46_60 + 
    stats.pasesClave61_75 + stats.pasesClave76_90;
  expect(sumaParcial).toBe(stats.totalPasesClave);
});
```

---

## 🧪 Comandos de Testing Avanzados

### Ejecutar con navegador visible (debug)
```powershell
npm test -- --browsers=Chrome --watch=true
```

### Ejecutar con reporte detallado
```powershell
npm test -- --progress=true --reporters=progress,kjhtml
```

### Ejecutar en CI/CD
```powershell
npm test -- --watch=false --browsers=ChromeHeadless --code-coverage
```

---

## 📝 Resultado Esperado

Al ejecutar los tests, deberías ver:

```
Chrome Headless: Executed 64 of 64 SUCCESS (2.5 secs / 2.3 secs)
TOTAL: 64 SUCCESS

✅ EstadisticasService: 18 tests passed
✅ EstadisticasGeneralesComponent: 22 tests passed  
✅ EstadisticasEquipoComponent: 24 tests passed
```

---

## 🐛 Solución de Problemas

### Error: "Could not find Chrome"
```powershell
# Instalar ChromeHeadless
npm install karma-chrome-launcher --save-dev
```

### Error: "Angular DevKit not found"
```powershell
npm install @angular-devkit/build-angular --save-dev
```

### Error: "Jasmine not found"
```powershell
npm install jasmine-core karma-jasmine --save-dev
```

### Tests muy lentos
```powershell
# Ejecutar solo los tests necesarios
npm test -- --include='**/estadistica*.spec.ts'
```

---

## 📋 Checklist Pre-Producción Frontend

- [ ] **Todos los tests del servicio pasan** (18 tests)
- [ ] **Todos los tests de componente general pasan** (22 tests)
- [ ] **Todos los tests de componente equipo pasan** (24 tests)
- [ ] **Coverage > 80%** en archivos de estadísticas
- [ ] **Validar en navegador manualmente** que estadísticas se muestran
- [ ] **Verificar que API responde correctamente**
- [ ] **Probar con datos reales** desde backend
- [ ] **Verificar distribuciones temporales** en gráficos
- [ ] **Probar cambio de temporadas**

---

## 🎯 Próximos Pasos

1. **Ejecutar los tests**:
   ```powershell
   cd gestion-jugadores-frontend
   npm install
   npm test -- --include='**/estadistica*.spec.ts' --watch=false
   ```

2. **Verificar coverage**:
   ```powershell
   npm test -- --code-coverage --watch=false
   # Ver reporte en: coverage/gestion-jugadores-frontend/index.html
   ```

3. **Integrar con CI/CD**:
   - Agregar paso de tests en pipeline
   - Verificar que tests pasan antes de deploy
   - Generar reportes de coverage

4. **Tests E2E** (opcional):
   - Crear tests e2e con Cypress o Protractor
   - Probar flujos completos usuario → API → frontend

---

## 📞 Notas Importantes

- Los tests usan **HttpClientTestingModule** para mockear peticiones HTTP
- Se usan **spies de Jasmine** para servicios inyectados
- Los tests están **aislados** (no llaman a la API real)
- Se prueban **cálculos matemáticos** además de lógica de negocio
- Se validan **distribuciones temporales** de eventos por intervalos
- Se verifica **manejo de errores** HTTP (404, 500)

**Estado**: ✅ Tests creados y listos para ejecutar  
**Total Tests**: 64 tests  
**Coverage Esperado**: > 85%  
**Fecha**: Febrero 10, 2026

---

## 🔗 Archivos Creados

1. [estadisticas.service.spec.ts](src/app/services/estadisticas.service.spec.ts) - Tests del servicio
2. [estadisticas-generales.component.spec.ts](src/app/components/estadisticas-generales/estadisticas-generales.component.spec.ts) - Tests del componente general
3. [estadisticas-equipo.component.spec.ts](src/app/components/estadisticas-equipo/estadisticas-equipo.component.spec.ts) - Tests del componente de equipo
