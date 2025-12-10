**Proyecto: Gestion Jugadores FutbolBase**

Propósito: documentación orientada a programadores que describe la arquitectura del backend (Spring Boot) y frontend (Angular), flujos clave, modelo de datos resumido, **nuevas funciones implementadas**, análisis de mejoras y recomendaciones prácticas para optimización.

**Última actualización:** Diciembre 2025

**Cambios recientes implementados:**
- ✅ Bulk deactivate de partidos con @Modifying JPQL + @Transactional.
- ✅ Listado de jugadores por usuario autenticado (endpoint `/api/v1/jugadores` sin parámetros).
- ✅ Gestión de partidos en componente separado con selector de equipo.
- ✅ Simplificación del navbar (solo título + sesión).
- ✅ Redirección automática de `/` a `/admin`.
- ✅ Fixes de binding en selects (uso de `[ngValue]` y coerción numérica).

---

**Contenido**

- **Resumen Rápido**: descripción del stack y responsabilidades.
- **Backend**: diagramas y flujo de activación de partido; estructura de paquetes; endpoints principales; cambios recientes; análisis de mejoras.
- **Frontend**: estructura de componentes, servicios, rutas, cambios UI recientes y flujo de inicio de partido.
- **Mejoras Prioritarias (Backend)**: lista con justificación, impacto y snippets de ejemplo.
- **Recomendaciones Adicionales**: seguridad, testing, escalabilidad y mantenibilidad.

---

**Resumen Rápido**

- Backend: Java 11/17 (según pom), Spring Boot (servicios, repositorios JPA/Hibernate), seguridad JWT.
- Frontend: Angular 17+, TypeScript, Bootstrap 5 / Angular Material.
- BD: MySQL (según properties), JPA/Hibernate para persistencia.

**Convenciones**
- Entidades: `com.gestion.jugadores.modelo`.
- Controladores: `com.gestion.jugadores.controlador`.
- Servicios: `com.gestion.jugadores.servicios` (interfaces) y `servicios.impl` (implementaciones).

---

**Backend — Arquitectura y Flujos**

Paquetes clave (resumen):

- `controlador` — REST controllers (AuthenticationController, EquipoController, PartidoControlador, UsuarioController, EventoJugadorControlador, JugadorControlador)
- `servicios` — interfaces de negocio
- `servicios.impl` — implementaciones
- `repositorio` — Spring Data JPA repositories (EquipoRepository, PartidoRepository, etc.)
- `modelo` — entidades JPA (Partido, Equipo, Jugador, EventoJugador, Usuario, Rol)

Endpoints principales (ejemplos):

- POST `/api/v1/auth/login` — autenticación (JWT)
- GET `/api/v1/jugadores` — listar jugadores del usuario autenticado (sin params) o por equipo (param `equipoId`)
- GET `/api/v1/partidos/equipo/{equipoId}` — listar partidos de un equipo
- PUT `/api/v1/partidos/{id}/activar` — activar un partido (desactiva otros activos del mismo equipo vía bulk update)
- PUT `/api/v1/partidos/{id}/desactivar` — desactivar partido
- GET `/equipos/me` — obtener equipos del usuario autenticado
- POST `/equipos/registrar` — registrar equipo para usuario autenticado
- GET `/usuarios/me` — obtener perfil del usuario autenticado

Flujo crítico: Activar Partido (resumen)

**Estado actual (Implementado ✅):**

El flujo de activación de partidos ha sido optimizado con un bulk update JPQL mediante `@Modifying`:

```mermaid
sequenceDiagram
  participant UI as Frontend
  participant API as PartidoControlador
  participant Service as PartidoService
  participant Repo as PartidoRepository
  participant DB as MySQL

  UI->>API: PUT /api/v1/partidos/{id}/activar
  API->>Service: activarPartido(id)
  Service->>Repo: findById(id)
  Repo-->>Service: partido
  Service->>Repo: deactivateOtherActiveByEquipoId(equipoId, id) ✅ BULK UPDATE
  Repo-->>DB: UPDATE partido SET partido_activo=false WHERE... (single roundtrip)
  Service->>Repo: save(partido con partido_activo=true)
  Repo-->>DB: INSERT/UPDATE
  Service-->>API: partidoActivado
  API-->>UI: 200 OK + body
```

**Ventajas de la implementación actual:**
- Operación atómica dentro de `@Transactional` en el service.
- Una única sentencia UPDATE para desactivar todos → menor latencia y menor riesgo de estados intermedios.
- Cumple el requisito de garantizar máximo 1 partido activo por equipo.

**Notas:**
- La transacción en `PartidoServiceImpl.activarPartido()` asegura que ambas operaciones (deactivate bulk + activate) se ejecutan en la misma transacción DB.

Esquema de entidad (resumen):

- `Partido` { id, equipo (ManyToOne Equipo), fecha, partidoActivo(Boolean), duracion, ... }
- `Equipo` { id, nombre, usuario (ManyToOne Usuario), jugadores(List<Jugador>), duracionPartido }
- `Jugador` { id, nombre, apellido, posicion, equipo (ManyToOne Equipo) }
- `EventoJugador` { id, jugadorId, partidoId, tipoEvento, minuto }

---

**Frontend — Arquitectura y Flujos**

Estructura principal:

- `app/` contiene componentes y servicios.
- **Componentes clave**: 
  - `partido-modo.component` (Iniciar/Controlar partido; flujo 2-fases: seleccionar → jugar).
  - `gestionar-partidos.component` (selector equipo + tabla partidos activos/inactivos).
  - `lista-jugadores.component` (listado con filtro "Todos" o por equipo; uso de `[ngValue]` y `ngModel`).
  - `historial-partidos.component`, `crear-equipo`, `registrar-jugador`, `jugador-detalles`.
  - `navbar.component` (simplificado: solo título + botón home + sesión).
  - `sidebar.component` (menú principal con todas las opciones de navegación).
- **Servicios**: `partido.service.ts`, `equipo.service.ts`, `jugador.service.ts` (con métodos para filtrado por usuario), `evento-jugador.service.ts`, `user.service.ts`.
- **Routing centralizado** en `app-routing.module.ts`:
  - Ruta raíz (`''`) redirige automáticamente a `/admin`.
  - Rutas autenticadas: `/admin`, `/iniciar-partido`, `/gestionar-partidos`, `/lista-jugadores`, etc.

**Cambios UI recientes implementados:**
- Eliminación de enlaces redundantes en navbar (solo sidebar gestiona navegación).
- Botón "home" en navbar para ir directamente a `/admin`.
- Selectores con `[ngValue]` para mantener tipos de datos (evita strings donde se esperan números).
- Coerción numérica en componentes (ej. `lista-jugadores.component.ts` convierte `equipoId` a Number).

Flujo crítico: Iniciar Partido (Frontend)

```mermaid
flowchart TD
  A[Usuario navega a Iniciar Partido] --> B[Componente carga equipos via equipo.service.obtenerEquiposMe]
  B --> C[Selector de equipo con ngModel/ngValue]
  C --> D[Cambio de equipo => obtenerJugadoresDelEquipo]
  D --> E{¿Usuarios autenticado?}
  E -->|Sí| F[Se listan jugadores del equipo]
  E -->|No| G[Redirect a login]
  F --> H[Selector para seleccionar Partido inactivo]
  H --> I[Botón Iniciar Partido => PUT /partidos/{id}/activar]
  I --> J{¿Éxito?}
  J -->|Sí| K[Frontend carga en Modo Juego]
  J -->|No| L[Mostrar error, permitir reintentar]
  K --> M[Usuario registra eventos: gol, asistencia, etc.]
  M --> N{Finalizar?}
  N -->|Sí| O[PUT /partidos/{id}/desactivar]
  O --> P[Volver a lista de partidos]
  N -->|No| M
```

**Servicios y acceso a datos:**
- `JugadorService.obtenerJugadoresPorUsuario()`: GET `/api/v1/jugadores` (sin parámetros, usa Authentication).
- `JugadorService.obtenerJugadoresPorEquipoId(equipoId)`: GET `/api/v1/jugadores?equipoId={id}`.
- Ambos métodos extraen token de `localStorage` y lo pasan en headers Authorization.

**Binding y coerción:**
- Los selects ahora usan `[(ngModel)]="selectedObject"` + `[ngValue]="objeto"` en options para evitar conversiones string accidentales.
- Ejemplo: `lista-jugadores.component.ts` convierte `this.equipoId` a Number con `Number(this.equipoId)` antes de comparaciones.

---

**Mejoras Prioritarias (Backend)**

**Estado: Parcialmente implementado**

Resumen: priorizar eficiencia (menor I/O DB), seguridad, y mantenibilidad.

### 1) ✅ Usar una única consulta @Modifying para desactivar partidos activos del equipo (bulk update)

**Estado: IMPLEMENTADO en PartidoRepository y PartidoServiceImpl**

Motivación: reducir múltiples consultas/commits cuando hay varios partidos activos; evita problemas de concurrencia y mejora latencia.

Implementación actual en `PartidoRepository`:

```java
@Modifying
@Query("UPDATE Partido p SET p.partidoActivo = false WHERE p.equipo.id = :equipoId AND p.partidoActivo = true AND p.id <> :excludeId")
int deactivateOtherActiveByEquipoId(@Param("equipoId") Long equipoId, @Param("excludeId") Long excludeId);
```

Uso en `PartidoServiceImpl.activarPartido()` dentro de una transacción:

```java
@Override
@Transactional
public Partido activarPartido(Long id) {
    Partido partido = partidoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));

    if (partido.getPartidoActivo()) {
        return partido;
    }

    Long equipoId = partido.getEquipo().getId();
    // Bulk deactivate other actives (single UPDATE query)
    partidoRepository.deactivateOtherActiveByEquipoId(equipoId, id);

    partido.setPartidoActivo(true);
    return partidoRepository.save(partido);
}
```

**Beneficios:**
- Transacción atómica: ambas operaciones (deactivate + activate) son ACID.
- Una única sentencia UPDATE → latencia reducida.
- Menor riesgo de condiciones de carrera (ventana más pequeña que iterar).

---

### 2) ✅ Endpoint para listar jugadores por usuario autenticado

**Estado: IMPLEMENTADO en JugadorControlador**

Cambio en el controlador: el endpoint `GET /api/v1/jugadores` ahora soporta dos modos:

```java
@GetMapping("/jugadores")
public ResponseEntity<List<Jugador>> listarJugadores(@RequestParam(required = false) Long equipoId, Authentication authentication) {
    if (equipoId != null) {
        // Modo 1: filtrar por equipo específico
        return ResponseEntity.ok(jugadorService.obtenerPorEquipo(equipoId));
    }

    // Modo 2: obtener jugadores del usuario autenticado (todos sus equipos)
    String username = authentication.getName();
    Usuario usuario = usuarioRepository.findByUsername(username);
    if (usuario == null) {
        return ResponseEntity.status(404).build();
    }

    List<Jugador> jugadores = jugadorService.obtenerPorUsuario(usuario.getId());
    return ResponseEntity.ok(jugadores);
}
```

**Beneficios:**
- Frontend puede mostrar "Todos los jugadores" de un usuario en un selector.
- Evita duplicaciones de datos y confusiones con jugadores de otros usuarios.

---

### 3) ☐ Centralizar mapping entre Entidades <-> DTOs

**Estado: NO IMPLEMENTADO (Recomendado)**

Motivación: evitar repetición de código que construye DTOs en muchos controladores/servicios.

Solución sugerida: utilizar `MapStruct` (anotaciones, generación de código automática) o `ModelMapper`.

Paso 1: Añadir dependencia en `pom.xml`:

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.3.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.3.Final</version>
    <scope>provided</scope>
</dependency>
```

Paso 2: Crear mappers (ej. `PartidoMapper.java`):

```java
@Mapper(componentModel = "spring")
public interface PartidoMapper {
    PartidoDTO toDto(Partido partido);
    Partido toEntity(PartidoDTO dto);
}
```

Paso 3: Inyectar en servicios/controladores:

```java
@RestController
public class PartidoControlador {
    @Autowired
    private PartidoMapper partidoMapper;
    
    @GetMapping("/partidos/{id}")
    public ResponseEntity<PartidoDTO> obtener(@PathVariable Long id) {
        Partido partido = service.findById(id);
        return ResponseEntity.ok(partidoMapper.toDto(partido));
    }
}
```

**Impacto:** reducción de copy-paste, mantenimiento centralizado de conversiones.

---

### 4) ☐ Introducir una capa base para operaciones CRUD repetitivas

**Estado: NO IMPLEMENTADO (Recomendado para escalabilidad)**

Pattern: `GenericService<T, ID>` + `GenericRepository<T, ID>` para evitar servicios con solo `save`, `delete`, `findById`.

Ejemplo:

```java
public abstract class GenericService<T, ID> {
    protected abstract JpaRepository<T, ID> getRepository();
    
    public T save(T entity) { return getRepository().save(entity); }
    public T findById(ID id) { return getRepository().findById(id).orElse(null); }
    public void delete(ID id) { getRepository().deleteById(id); }
    public List<T> findAll(Pageable p) { return getRepository().findAll(p).getContent(); }
}
```

---

### 5) ☐ Indexes y consultas optimizadas

**Estado: PARCIALMENTE PENDIENTE (Recomendado)**

Añadir índices en columnas usadas en filtros/sorting mediante Flyway migration:

```sql
-- MySQL / PostgreSQL
CREATE INDEX idx_partido_equipo_activo ON partido(equipo_id, partido_activo);
CREATE INDEX idx_equipo_usuario ON equipo(usuario_id);
CREATE INDEX idx_evento_partido ON evento_jugador(partido_id);
CREATE INDEX idx_jugador_equipo ON jugador(equipo_id);
```

Implementación: crear archivo de migración en `src/main/resources/db/migration/V2_0__Add_Indexes.sql`.

**Beneficio:** queries más rápidas, especialmente en endpoints de filtrado.

---

### 6) ☐ Forzar verificación de ownership en controladores

**Estado: PENDIENTE (Crítico para seguridad)**

Motivación: evitar fugas de datos o modificaciones por usuarios distintos.

Implementación: en endpoints write (POST, PUT, DELETE), validar ownership antes de permitir cambios.

Ejemplo:

```java
@PutMapping("/partidos/{id}/activar")
public ResponseEntity<?> activarPartido(
    @PathVariable Long id,
    Authentication authentication) {
    
    Partido partido = partidoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado"));
    
    // Verificar que el usuario autenticado es propietario del equipo
    String username = authentication.getName();
    Usuario usuario = usuarioRepository.findByUsername(username);
    
    if (!partido.getEquipo().getUsuario().getId().equals(usuario.getId())) {
        return ResponseEntity.status(403).body("No tienes permiso para activar este partido");
    }
    
    Partido resultado = partidoService.activarPartido(id);
    return ResponseEntity.ok(resultado);
}
```

**Impacto:** prevención de accesos no autorizados.

---

### 7) ☐ Añadir pruebas de integración para flujos críticos

**Estado: PENDIENTE (Recomendado)**

Crear tests que validen:
- Activar un partido desactiva otros del mismo equipo.
- Un usuario no puede ver/modificar datos de otro usuario.
- Concurrencia: 2 requests simultáneos activando partidos diferentes del mismo equipo → solo 1 queda activo.

Ejemplo usando `@SpringBootTest`:

```java
@SpringBootTest
public class PartidoActivationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PartidoRepository partidoRepository;
    
    @Test
    public void testActivarPartidoDesactivaOtros() {
        // Setup: crear 2 partidos del mismo equipo
        // Activar partido 1
        // Verificar que partido 2 está inactivo
    }
}
```

---

### 8) ☐ Bulk insert/update para eventos masivos

**Estado: PENDIENTE (Para futuro)**

Si la aplicación crece y registra muchos eventos por partido, considerar endpoint batch:

```java
@PostMapping("/eventos/batch")
public ResponseEntity<List<EventoJugador>> registrarEventosBatch(
    @RequestBody List<EventoJugadorDTO> eventos) {
    List<EventoJugador> guardados = eventoService.saveAll(eventos);
    return ResponseEntity.ok(guardados);
}
```

---

### 9) ☐ Mejorar logs y métricas

**Estado: PENDIENTE (Para observabilidad)**

Añadir logs estructurados con SLF4J + MDC (Mapped Diagnostic Context):

```java
@Service
public class PartidoServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(PartidoServiceImpl.class);
    
    @Override
    @Transactional
    public Partido activarPartido(Long id) {
        MDC.put("partidoId", String.valueOf(id));
        logger.info("Activando partido");
        try {
            // ... lógica
            logger.info("Partido activado exitosamente");
        } catch (Exception e) {
            logger.error("Error activando partido", e);
            throw e;
        } finally {
            MDC.remove("partidoId");
        }
    }
}
```

---

### 10) ☐ Caching selectivo

**Estado: PENDIENTE (Para rendimiento)**

Cachear datos estáticos por usuario/equipo usando Spring Cache:

```java
@Cacheable(value = "jugadoresPorEquipo", key = "#equipoId")
public List<Jugador> obtenerPorEquipo(Long equipoId) {
    return jugadorRepository.findByEquipoId(equipoId);
}
```

Configurar expiración en `application.properties`:

```properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=expireAfterWrite=5m
```

---

**Checklist de implementación (prioridad alta → baja)**

- [✅ High] Reemplazar loop de saves por @Modifying bulk update + @Transactional.
- [✅ High] Implementar endpoint para listar jugadores por usuario autenticado.
- [☐ High] Agregar verificación de ownership en endpoints write.
- [☐ High] Añadir índices DB en columnas de filtro.
- [☐ Med] Añadir mappers (MapStruct) y DTO centralizados.
- [☐ Med] Introducir Generic BaseService para CRUD repetido.
- [☐ Med] Añadir pruebas de integración para flujos de partido.
- [☐ Low] Evaluar caching y métricas, preparar para escalado.

---

**Análisis de mejoras adicionales por área**

### Seguridad

1. **Validación de ownership (crítico):** Implementar checks en todos los endpoints que manipulen datos de usuario/equipo/partido.
2. **Rate limiting:** Proteger endpoints públicos con límites de rate (`spring-boot-starter-data-redis` + `bucket4j`).
3. **CORS mejorado:** Validar origin en producción (no `*`).
4. **HTTPS en producción:** Forzar redirección HTTP → HTTPS.
5. **Rotación de tokens JWT:** Implementar refresh tokens con expiración corta en access tokens.

### Rendimiento

1. **Paginación:** Añadir `Pageable` a endpoints que devuelven listas (p. ej., `GET /jugadores?page=0&size=10`).
2. **Lazy loading:** Configurar fetch strategies en relaciones `@ManyToOne` y `@OneToMany` para evitar N+1 queries.
3. **Query optimization:** Usar `@EntityGraph` o `@Query` con JOINs en lugar de lazy cargas.
4. **Connection pooling:** Verificar configuración de HikariCP en `application.properties` (`spring.datasource.hikari.maximum-pool-size`).

### Escalabilidad y DevOps

1. **Docker:** Crear Dockerfile y docker-compose.yml para desplegar backend + MySQL + Redis (opcional).
2. **CI/CD:** Pipeline GitHub Actions para compilar, testear y desplegar automáticamente.
3. **Monitoring:** Integrar Spring Boot Actuator + Prometheus + Grafana para métricas.
4. **Logging centralizado:** ELK Stack (Elasticsearch, Logstash, Kibana) o equivalente.
5. **BD migrations:** Usar Flyway o Liquibase para versionado de schema.

### Frontend (Angular)

1. **Interceptor centralizado:** Centralizar manejo de Authorization headers y error responses.
2. **RxJS operators:** Usar `shareReplay()` para cachear requests en componentes.
3. **Lazy loading de módulos:** Implementar lazy loading en rutas para reducir bundle inicial.
4. **PWA:** Convertir a Progressive Web App para funcionalidad offline.
5. **Error handling:** Mejorar alertas de error (toastr/snackbar) vs console.log.
6. **Validación de formularios:** Usar Validators de Angular Forms (Reactive Forms pattern).

### Testing

1. **Unit tests:** Servicios + controladores (Mockito, JUnit 5).
2. **Integration tests:** Flujos end-to-end con `@SpringBootTest`.
3. **E2E tests (Frontend):** Cypress o Playwright para UI automation.
4. **Load testing:** JMeter para simular concurrencia y picos de tráfico.

---

**Resumen de estado actual y siguientes pasos**

✅ **Implementado:**
- Bulk deactivate de partidos con @Modifying JPQL + @Transactional.
- Listado de jugadores por usuario autenticado.
- Simplificación UI (navbar minimalista + sidebar centralizado).
- Fixes de binding en selects (ngValue, coerción numérica).

🚀 **Próximas prioridades (próximas iteraciones):**
1. Verificación de ownership en endpoints (crítico para seguridad).
2. Índices DB para optimizar queries.
3. Pruebas de integración para flujos de partido y autenticación.
4. Mappers MapStruct para centralizarDTO conversions.

💡 **Recomendaciones de largo plazo:**
- Evaluar migración a arquitetura de microservicios si crece la complejidad.
- Implementar caching y async processing (RabbitMQ/Kafka) para eventos.
- Automatizar despliegues con Docker + Kubernetes.

---

**Notas operativas**

- Backend compila exitosamente con `mvn clean compile`.
- Frontend compila con `ng build --configuration development`.
- DB: Docker Compose levanta MySQL; asegurar credenciales en `.env` o properties.
- Recomendación: ejecutar tests de integración en CI antes de desplegar cambios.

---

Fin del documento (v2 - Diciembre 2025).
