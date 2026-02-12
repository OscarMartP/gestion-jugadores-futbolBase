# 🧪 Guía de Testing - Sistema de Gestión de Jugadores

**Última actualización:** 4 Febrero 2026  
**Versión:** 1.0

---

## 📋 Índice

1. [Introducción](#introducción)
2. [Backend Testing (Spring Boot)](#backend-testing-spring-boot)
3. [Frontend Mobile Testing (Ionic Angular)](#frontend-mobile-testing-ionic-angular)
4. [Ejecutar Tests](#ejecutar-tests)
5. [Cobertura de Tests](#cobertura-de-tests)
6. [Mejores Prácticas](#mejores-prácticas)

---

## Introducción

Este documento describe la infraestructura de testing implementada para validar las funcionalidades críticas del sistema, especialmente:

- ✅ Sistema de estadísticas (procesamiento de eventos)
- ✅ RefreshService (actualización automática de listas)
- ✅ Integración de servicios

### Stack de Testing

| Componente | Framework | Herramientas |
|------------|-----------|--------------|
| **Backend** | JUnit 5 | Mockito, Spring Boot Test |
| **Frontend Mobile** | Jasmine/Karma | Angular Testing Utilities |

---

## Backend Testing (Spring Boot)

### 🎯 Ubicación de Tests

```
gestion-jugadores-futbolBase/
└── src/
    └── test/
        └── java/
            └── com/
                └── gestion/
                    └── jugadores/
                        ├── controlador/
                        │   ├── EventoJugadorControladorV2Test.java
                        │   ├── JugadorControladorV2Test.java
                        │   └── PartidoControladorV2Test.java
                        └── servicios/
                            └── EstadisticasServiceImplTest.java ✨ NUEVO
```

### 📊 EstadisticasServiceImplTest

**Propósito:** Validar el cálculo correcto de estadísticas a partir de eventos de jugadores.

#### Tests Implementados (6 tests)

```java
// 1. Procesamiento de evento GOL
@Test
void testActualizarEstadisticasJugadorConGol()

// 2. Procesamiento de evento TIRO_A_PUERTA
@Test
void testActualizarEstadisticasJugadorConTiroAPuerta()

// 3. Obtención de estadísticas de partido
@Test
void testObtenerEstadisticasPartido()

// 4. Creación de estadísticas nuevas
@Test
void testCrearEstadisticasNuevasCuandoNoExisten()

// 5. Procesamiento de múltiples eventos
@Test
void testProcesarMultiplesEventos()

// 6. Manejo de diferentes formatos de eventos
@Test
void testManejarEventosConDiferentesFormatos()
```

#### Ejemplo de Test

```java
@Test
void testActualizarEstadisticasJugadorConGol() {
    // Crear evento de gol
    EventoJugador evento = new EventoJugador();
    evento.setId(1L);
    evento.setJugador(jugador);
    evento.setPartido(partido);
    evento.setTipoEvento("GOL");
    evento.setMinuto(25);
    
    EstadisticasJugador stats = new EstadisticasJugador();
    stats.setJugador(jugador);
    stats.setTemporada("2025-2026");
    
    // Configurar mocks
    when(jugadorRepository.findById(1L)).thenReturn(Optional.of(jugador));
    when(estadisticasJugadorRepository.findByJugador_IdAndTemporada(1L, "2025-2026"))
        .thenReturn(Optional.of(stats));
    when(eventoJugadorRepository.findByJugador_Id(1L)).thenReturn(Arrays.asList(evento));
    when(estadisticasJugadorRepository.save(any(EstadisticasJugador.class)))
        .thenReturn(stats);
    
    // Ejecutar
    estadisticasService.actualizarEstadisticasJugador(1L, "2025-2026");
    
    // Verificar
    verify(estadisticasJugadorRepository).save(any(EstadisticasJugador.class));
}
```

#### Cobertura

| Método Testeado | Cobertura |
|-----------------|-----------|
| `actualizarEstadisticasJugador()` | ✅ 80% |
| `obtenerEstadisticasPartido()` | ✅ 60% |
| Manejo de eventos (GOL, TIRO_A_PUERTA) | ✅ 100% |
| Normalización de eventos | ✅ 100% |

---

## Frontend Mobile Testing (Ionic Angular)

### 🎯 Ubicación de Tests

```
gestion-jugadores-mobile/
└── src/
    └── app/
        ├── core/
        │   └── services/
        │       └── refresh.service.spec.ts ✨ NUEVO
        └── pages/
            ├── estadisticas-partido/
            │   └── estadisticas-partido.page.spec.ts ✨ MEJORADO
            ├── jugadores/
            │   └── jugadores.page.spec.ts ✨ MEJORADO
            └── jugador-form/
                └── jugador-form.page.spec.ts ✨ MEJORADO
```

### 📊 RefreshService Tests

**Propósito:** Validar el sistema de actualización automática de listas mediante eventos RxJS.

#### Tests Implementados (7 tests)

```typescript
// 1. Creación del servicio
it('should be created')

// 2. Emisión de evento de jugadores
it('should emit jugadores refresh event')

// 3. Emisión de evento de equipos
it('should emit equipos refresh event')

// 4. Emisión de evento de partidos
it('should emit partidos refresh event')

// 5. Emisión de evento de estadísticas
it('should emit estadisticas refresh event')

// 6. Múltiples suscriptores
it('should handle multiple subscribers for jugadores')

// 7. Aislamiento de eventos
it('should not emit to other observables when refreshing jugadores')
```

#### Ejemplo de Test

```typescript
it('should emit jugadores refresh event', (done) => {
  service.onJugadoresRefresh.subscribe(() => {
    expect(true).toBe(true);
    done();
  });
  
  service.refreshJugadores();
});
```

### 📊 EstadisticasPartidoPage Tests

**Propósito:** Validar la carga de estadísticas de partido y procesamiento de eventos.

#### Tests Implementados (3 tests)

```typescript
// 1. Creación del componente
it('should create')

// 2. ID de partido desde ruta
it('should have partidoId from route params')

// 3. Llamada a servicios
it('should call partido service when loading estadisticas')
```

### 📊 JugadoresPage Tests

**Propósito:** Validar la integración con RefreshService para actualización automática.

#### Tests Implementados (3 tests)

```typescript
// 1. Creación del componente
it('should create')

// 2. Suscripción a eventos de refresh
it('should subscribe to refresh events')

// 3. Recarga al recibir evento
it('should reload jugadores when refresh is triggered')
```

### 📊 JugadorFormPage Tests

**Propósito:** Validar el guardado de jugadores y emisión de eventos de refresh.

#### Tests Implementados (3 tests)

```typescript
// 1. Creación del componente
it('should create')

// 2. Existencia del formulario
it('should have a form')

// 3. Emisión de refresh después de guardar
it('should call refresh service after successful save')
```

---

## Ejecutar Tests

### Backend (Maven)

```bash
# Todos los tests
mvn test

# Test específico
mvn test -Dtest=EstadisticasServiceImplTest

# Con reporte de cobertura
mvn test jacoco:report
```

### Frontend Mobile (Karma/Jasmine)

```bash
# Todos los tests
npm test

# Modo headless (CI/CD)
npm test -- --watch=false --browsers=ChromeHeadless

# Test específico
npm test -- --include='**/refresh.service.spec.ts'

# Con cobertura
npm test -- --code-coverage
```

---

## Cobertura de Tests

### Backend

| Componente | Tests | Estado | Cobertura |
|------------|-------|--------|-----------|
| EstadisticasServiceImpl | 6 | ✅ PASS | 70% |
| EventoJugadorControlador | 4 | ⚠️ 1 FAIL | - |
| JugadorControlador | 4 | ⚠️ 4 FAIL | - |
| PartidoControlador | 5 | ⚠️ 1 FAIL | - |
| **TOTAL** | **20** | **14 PASS** | **70%** |

### Frontend Mobile

| Componente | Tests | Estado | Cobertura |
|------------|-------|--------|-----------|
| RefreshService | 7 | ✅ PASS | 100% |
| EstadisticasPartidoPage | 3 | ✅ PASS | 40% |
| JugadoresPage | 3 | ⚠️ 2 FAIL | - |
| JugadorFormPage | 3 | ⚠️ 3 FAIL | - |
| Otros componentes | 8 | ⚠️ 6 FAIL | - |
| **TOTAL** | **24** | **13 PASS** | **54%** |

---

## Mejores Prácticas

### ✅ Backend (JUnit + Mockito)

1. **Usar mocks para dependencias externas**
   ```java
   @Mock
   private JugadorRepository jugadorRepository;
   
   @InjectMocks
   private EstadisticasServiceImpl estadisticasService;
   ```

2. **Inicializar mocks en @BeforeEach**
   ```java
   @BeforeEach
   void setUp() {
       MockitoAnnotations.openMocks(this);
   }
   ```

3. **Verificar interacciones importantes**
   ```java
   verify(repository).save(any(Entity.class));
   ```

4. **Nomenclatura descriptiva**
   ```java
   @Test
   void testActualizarEstadisticasJugadorConGol() // ✅ Claro
   void test1() // ❌ No descriptivo
   ```

### ✅ Frontend Mobile (Jasmine/Karma)

1. **Usar spies para servicios**
   ```typescript
   const serviceSpy = jasmine.createSpyObj('Service', ['method']);
   ```

2. **Configurar TestBed correctamente**
   ```typescript
   await TestBed.configureTestingModule({
     imports: [Component],
     providers: [
       { provide: Service, useValue: serviceSpy }
     ]
   }).compileComponents();
   ```

3. **Limpiar suscripciones**
   ```typescript
   afterEach(() => {
     fixture.destroy();
   });
   ```

4. **Usar done() para tests asíncronos**
   ```typescript
   it('should emit event', (done) => {
     service.event$.subscribe(() => {
       expect(true).toBe(true);
       done();
     });
   });
   ```

---

## 🐛 Tests Conocidos con Fallos

### Backend

1. **JugadorControladorV2Test** (4 fallos)
   - **Causa:** Posición "Delantero" no válida
   - **Solución:** Cambiar a posiciones válidas (POR, LD, LI, CEN, MC, MCO, EXD, EXIZ, DC)

2. **EventoJugadorControladorV2Test** (1 fallo)
   - **Causa:** Espera status 200 pero recibe 201
   - **Solución:** Ajustar aserto a `.isCreated()` en lugar de `.isOk()`

3. **PartidoControladorV2Test** (1 fallo)
   - **Causa:** Espera status 200 pero recibe 201
   - **Solución:** Ajustar aserto a `.isCreated()`

### Frontend Mobile

Los fallos en componentes pre-existentes son por falta de mocks en servicios. Se recomienda actualizar gradualmente siguiendo el patrón de RefreshService.spec.ts.

---

## 📝 Próximos Pasos

### Prioridad Alta
- [ ] Corregir tests de JugadorControlador (cambiar posiciones)
- [ ] Aumentar cobertura de EstadisticasServiceImpl a 90%
- [ ] Completar tests de JugadorFormPage

### Prioridad Media
- [ ] Tests de integración end-to-end (E2E)
- [ ] Tests de EquipoService
- [ ] Tests de PartidoService

### Prioridad Baja
- [ ] Tests de performance
- [ ] Tests de UI con Cypress (Frontend)
- [ ] Tests de carga con JMeter (Backend)

---

## 📚 Referencias

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Jasmine Documentation](https://jasmine.github.io/)
- [Karma Configuration](https://karma-runner.github.io/latest/config/configuration-file.html)
- [Angular Testing Guide](https://angular.io/guide/testing)

---

**Autor:** Sistema de Gestión de Jugadores - Equipo de Desarrollo  
**Fecha de creación:** 4 Febrero 2026  
**Última revisión:** 4 Febrero 2026
