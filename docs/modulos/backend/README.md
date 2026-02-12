# 🔧 Backend - Sistema de Gestión de Jugadores

## 📋 Índice

1. [Arquitectura General](#arquitectura-general)
2. [Estructura de Paquetes](#estructura-de-paquetes)
3. [Controladores REST](#controladores-rest)
4. [Servicios de Negocio](#servicios-de-negocio)
5. [Modelo de Datos](#modelo-de-datos)
6. [Flujos Principales](#flujos-principales)

## Arquitectura General

```mermaid
graph TB
    subgraph "Capa de Presentación"
        CTRL[Controladores REST]
        AUTH[Authentication Controller]
    end
    
    subgraph "Capa de Seguridad"
        JWT[JWT Filter]
        SEC[Spring Security]
    end
    
    subgraph "Capa de Negocio"
        SRV[Services]
        MAP[Mappers DTO]
    end
    
    subgraph "Capa de Persistencia"
        REPO[Repositories JPA]
        ENT[Entities]
    end
    
    subgraph "Base de Datos"
        DB[(MySQL)]
    end
    
    CTRL --> JWT
    AUTH --> JWT
    JWT --> SEC
    SEC --> SRV
    SRV --> MAP
    SRV --> REPO
    REPO --> ENT
    ENT --> DB
    
    style CTRL fill:#6db33f
    style SRV fill:#ffd700
    style REPO fill:#ff6b6b
    style DB fill:#4479a1
```

## Estructura de Paquetes

```
com.gestion.jugadores/
├── controlador/              # Controladores REST
│   ├── base/                # Controladores genéricos
│   │   ├── BaseController.java
│   │   └── BaseService.java
│   ├── AuthenticationController.java
│   ├── EquipoController.java
│   ├── UsuarioController.java
│   ├── JugadorControladorV2.java
│   ├── PartidoControladorV2.java
│   ├── EventoJugadorControladorV2.java
│   └── EstadisticasControlador.java
│
├── modelo/                   # Entidades JPA
│   ├── Usuario.java
│   ├── Equipo.java
│   ├── Jugador.java
│   ├── Partido.java
│   ├── EventoJugador.java
│   ├── EstadisticasJugador.java
│   └── EstadisticasEquipo.java
│
├── dto/                      # Data Transfer Objects
│   ├── JugadorDTO.java
│   ├── PartidoDTO.java
│   ├── EventoJugadorDTO.java
│   ├── EstadisticasJugadorDTO.java
│   └── EstadisticasEquipoDTO.java
│
├── mapper/                   # Conversores Entity ↔ DTO
│   ├── JugadorMapper.java
│   ├── PartidoMapper.java
│   └── EventoJugadorMapper.java
│
├── servicios/               # Interfaces de servicios
│   ├── UsuarioService.java
│   ├── EquipoService.java
│   ├── JugadorService.java
│   ├── PartidoService.java
│   ├── EventoJugadorService.java
│   └── EstadisticasService.java
│
├── servicios/impl/          # Implementaciones
│   ├── UsuarioServiceImpl.java
│   ├── EquipoServiceImpl.java
│   ├── JugadorServiceImpl.java
│   ├── PartidoServiceImpl.java
│   ├── EventoJugadorServiceImpl.java
│   └── EstadisticasServiceImpl.java
│
├── repositorio/             # Repositorios JPA
│   ├── UsuarioRepository.java
│   ├── EquipoRepository.java
│   ├── JugadorRepository.java
│   ├── PartidoRepository.java
│   ├── EventoJugadorRepository.java
│   ├── EstadisticasJugadorRepository.java
│   └── EstadisticasEquipoRepository.java
│
├── configuraciones/         # Configuraciones
│   ├── JwtUtils.java
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── SwaggerConfig.java
│
└── excepciones/            # Manejo de errores
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

## Controladores REST

### 📌 Arquitectura de Controladores V2

```mermaid
classDiagram
    class BaseController~E,D,ID~ {
        <<abstract>>
        #BaseService service
        #Function toDto
        #Function toEntity
        +findById(ID) ResponseEntity~D~
        +findAll() ResponseEntity~List~D~~
        +create(D) ResponseEntity~D~
        +update(ID, D) ResponseEntity~D~
        +delete(ID) ResponseEntity~Void~
    }
    
    class JugadorControladorV2 {
        -JugadorService service
        -JugadorMapper mapper
        +obtenerJugadoresDelUsuario()
        +buscarPorEquipo(Long)
    }
    
    class PartidoControladorV2 {
        -PartidoService service
        -PartidoMapper mapper
        +finalizarPartido(Long)
        +actualizarAlineacion(Long, AlineacionDTO)
    }
    
    class EventoJugadorControladorV2 {
        -EventoJugadorService service
        -EventoJugadorMapper mapper
        +registrarEvento(EventoJugadorDTO)
        +obtenerEventosPorPartido(Long)
    }
    
    BaseController <|-- JugadorControladorV2
    BaseController <|-- PartidoControladorV2
    BaseController <|-- EventoJugadorControladorV2
```

### Endpoints Principales

#### 🔐 Autenticación
```
POST   /api/auth/generate-token        # Login y generación JWT
GET    /api/auth/actual-usuario        # Obtener usuario actual
POST   /api/auth/registro              # Registro de nuevo usuario
```

#### 👥 Jugadores
```
GET    /api/v2/jugadores               # Listar todos los jugadores
GET    /api/v2/jugadores/{id}          # Obtener jugador por ID
POST   /api/v2/jugadores               # Crear nuevo jugador
PUT    /api/v2/jugadores/{id}          # Actualizar jugador
DELETE /api/v2/jugadores/{id}          # Eliminar jugador
GET    /api/v2/jugadores/equipo/{id}   # Jugadores por equipo
```

#### ⚽ Partidos
```
GET    /api/v2/partidos                # Listar partidos
GET    /api/v2/partidos/{id}           # Obtener partido por ID
POST   /api/v2/partidos                # Crear partido
PUT    /api/v2/partidos/{id}           # Actualizar partido
DELETE /api/v2/partidos/{id}           # Eliminar partido
PUT    /api/v2/partidos/{id}/finalizar # Finalizar partido
PUT    /api/v2/partidos/{id}/alineacion # Actualizar alineación
```

#### 📊 Eventos
```
POST   /api/v2/eventos                 # Registrar evento
GET    /api/v2/eventos/partido/{id}    # Eventos por partido
GET    /api/v2/eventos/jugador/{id}    # Eventos por jugador
DELETE /api/v2/eventos/{id}            # Eliminar evento
```

#### 📈 Estadísticas
```
GET    /api/estadisticas/jugador/{id}              # Estadísticas de jugador
GET    /api/estadisticas/equipo/{id}               # Estadísticas de equipo
GET    /api/estadisticas/equipo/{id}/jugadores     # Estadísticas todos los jugadores
GET    /api/estadisticas/equipo/{id}/temporada/{t} # Estadísticas por temporada
```

## Servicios de Negocio

### Flujo de Servicio Genérico

```mermaid
sequenceDiagram
    participant C as Controller
    participant S as Service
    participant M as Mapper
    participant R as Repository
    participant DB as Database
    
    C->>S: Llamada al método
    S->>M: toEntity(DTO)
    M-->>S: Entity
    S->>S: Lógica de negocio
    S->>R: save/find/update/delete
    R->>DB: Query SQL
    DB-->>R: Resultado
    R-->>S: Entity
    S->>M: toDto(Entity)
    M-->>S: DTO
    S-->>C: Respuesta DTO
```

### Servicios Clave

#### JugadorService
- `guardarJugador(Jugador)`: Registra nuevo jugador
- `obtenerJugadoresPorEquipo(Long)`: Lista jugadores del equipo
- `actualizarJugador(Long, Jugador)`: Actualiza datos del jugador
- `eliminarJugador(Long)`: Elimina jugador y sus estadísticas

#### PartidoService
- `crearPartido(Partido)`: Crea nuevo partido
- `finalizarPartido(Long)`: Finaliza partido y actualiza estadísticas
- `actualizarAlineacion(Long, List, List)`: Actualiza titulares/suplentes
- `desactivarPartidosAnteriores(Long)`: Desactiva otros partidos del equipo

#### EventoJugadorService
- `registrarEvento(EventoJugador)`: Registra evento en partido
- `obtenerEventosPorPartido(Long)`: Lista eventos del partido
- `eliminarEvento(Long)`: Elimina evento y recalcula estadísticas

#### EstadisticasService
- `obtenerEstadisticasJugador(Long)`: Obtiene estadísticas completas
- `obtenerEstadisticasEquipo(Long)`: Obtiene estadísticas del equipo
- `actualizarEstadisticas(Long)`: Recalcula estadísticas tras finalizar partido

## Modelo de Datos

### Diagrama de Entidades

```mermaid
erDiagram
    Usuario ||--o{ Equipo : posee
    Equipo ||--o{ Jugador : tiene
    Equipo ||--o{ Partido : juega
    Partido ||--o{ EventoJugador : registra
    Jugador ||--o{ EventoJugador : participa
    Jugador ||--|| EstadisticasJugador : tiene
    Equipo ||--|| EstadisticasEquipo : tiene
    
    Usuario {
        Long id PK
        String username
        String password
        String nombre
        String apellido
        String telefono
    }
    
    Equipo {
        Long id PK
        String nombre
        String categoria
        int tipoFutbol
        Long usuarioId FK
    }
    
    Jugador {
        Long id PK
        String nombre
        String apellido
        int edad
        String posicion
        Long equipoId FK
    }
    
    Partido {
        Long id PK
        Date fecha
        String rival
        String ubicacion
        Boolean partidoActivo
        int golesEquipo
        int golesRival
        String resultado
        Long equipoId FK
    }
    
    EventoJugador {
        Long id PK
        TipoEvento tipo
        int minuto
        Long partidoId FK
        Long jugadorId FK
    }
    
    EstadisticasJugador {
        Long id PK
        int partidosJugados
        int goles
        int asistencias
        int tarjetasAmarillas
        int tarjetasRojas
        int minutosJugados
        Long jugadorId FK
    }
    
    EstadisticasEquipo {
        Long id PK
        int partidosJugados
        int victorias
        int empates
        int derrotas
        int golesAFavor
        int golesEnContra
        Long equipoId FK
    }
```

### Relaciones Principales

1. **Usuario → Equipo (1:N)**
   - Un usuario puede tener múltiples equipos
   - Cada equipo pertenece a un único usuario

2. **Equipo → Jugador (1:N)**
   - Un equipo tiene múltiples jugadores
   - Cada jugador pertenece a un equipo

3. **Equipo → Partido (1:N)**
   - Un equipo juega múltiples partidos
   - Cada partido corresponde a un equipo

4. **Partido → EventoJugador (1:N)**
   - Un partido tiene múltiples eventos
   - Cada evento pertenece a un partido

5. **Jugador ↔ EstadisticasJugador (1:1)**
   - Cada jugador tiene un registro de estadísticas
   - Eliminación en cascada

## Flujos Principales

### 1. Flujo de Autenticación

```mermaid
sequenceDiagram
    participant U as Usuario
    participant FE as Frontend
    participant AC as AuthController
    participant US as UserDetailsService
    participant JWT as JwtUtils
    participant DB as Database
    
    U->>FE: Ingresa credenciales
    FE->>AC: POST /generate-token
    AC->>US: loadUserByUsername()
    US->>DB: findByUsername()
    DB-->>US: Usuario
    US-->>AC: UserDetails
    AC->>AC: Validar password
    AC->>JWT: generateToken(UserDetails)
    JWT-->>AC: Token JWT
    AC-->>FE: {token, username}
    FE->>FE: Guardar en localStorage
    FE-->>U: Redirigir a dashboard
```

### 2. Flujo de Creación de Partido

```mermaid
sequenceDiagram
    participant U as Usuario
    participant FE as Frontend
    participant PC as PartidoController
    participant PS as PartidoService
    participant PR as PartidoRepository
    participant DB as Database
    
    U->>FE: Completa formulario partido
    FE->>PC: POST /api/v2/partidos
    PC->>PS: crearPartido(PartidoDTO)
    PS->>PS: toEntity(DTO)
    PS->>PS: Validar datos
    PS->>PR: desactivarPartidosAnteriores()
    PR->>DB: UPDATE partidos SET activo=false
    PS->>PR: save(partido)
    PR->>DB: INSERT partido
    DB-->>PR: Partido guardado
    PR-->>PS: Entity
    PS->>PS: toDto(Entity)
    PS-->>PC: PartidoDTO
    PC-->>FE: 201 Created
    FE-->>U: Mostrar confirmación
```

### 3. Flujo de Registro de Eventos

```mermaid
sequenceDiagram
    participant U as Entrenador
    participant FE as Frontend
    participant EC as EventoController
    participant ES as EventoService
    participant ER as EventoRepository
    participant DB as Database
    
    U->>FE: Registra evento (gol/asistencia/etc)
    FE->>EC: POST /api/v2/eventos
    EC->>ES: registrarEvento(EventoDTO)
    ES->>ES: Validar partido activo
    ES->>ES: Validar jugador del equipo
    ES->>ES: toEntity(DTO)
    ES->>ER: save(evento)
    ER->>DB: INSERT evento_jugador
    DB-->>ER: Evento guardado
    ER-->>ES: Entity
    ES->>ES: toDto(Entity)
    ES-->>EC: EventoDTO
    EC-->>FE: 201 Created
    FE->>FE: Actualizar contador en UI
    FE-->>U: Mostrar confirmación
```

### 4. Flujo de Finalización de Partido

```mermaid
sequenceDiagram
    participant U as Entrenador
    participant FE as Frontend
    participant PC as PartidoController
    participant PS as PartidoService
    participant ES as EstadisticasService
    participant DB as Database
    
    U->>FE: Clic en "Finalizar Partido"
    FE->>PC: PUT /api/v2/partidos/{id}/finalizar
    PC->>PS: finalizarPartido(id)
    PS->>DB: Obtener eventos del partido
    DB-->>PS: Lista de eventos
    PS->>PS: Contar goles (GOL/GOL_RIVAL)
    PS->>PS: Calcular resultado (W/D/L)
    PS->>PS: partido.setPartidoActivo(false)
    PS->>DB: UPDATE partido
    PS->>ES: actualizarEstadisticas(partidoId)
    ES->>ES: Calcular estadísticas jugadores
    ES->>ES: Calcular estadísticas equipo
    ES->>DB: UPDATE estadisticas
    DB-->>ES: Confirmación
    ES-->>PS: Estadísticas actualizadas
    PS-->>PC: PartidoDTO
    PC-->>FE: 200 OK
    FE-->>U: Mostrar resumen del partido
```

### 5. Flujo de Cálculo de Estadísticas

```mermaid
flowchart TD
    A[Finalizar Partido] --> B{Obtener Eventos}
    B --> C[Iterar por Jugador]
    C --> D[Contar Goles]
    C --> E[Contar Asistencias]
    C --> F[Contar Tarjetas]
    C --> G[Contar Pases Clave]
    C --> H[Contar Tiros]
    C --> I[Contar Robos]
    C --> J[Calcular Minutos Jugados]
    
    D --> K[Actualizar EstadisticasJugador]
    E --> K
    F --> K
    G --> K
    H --> K
    I --> K
    J --> K
    
    K --> L[Actualizar EstadisticasEquipo]
    L --> M[Calcular Distribución Temporal]
    M --> N[Calcular Estado Marcador]
    N --> O[Determinar Perfiles]
    O --> P[Identificar Top Performers]
    P --> Q[Guardar en BD]
    Q --> R[Fin]
    
    style A fill:#6db33f
    style Q fill:#4479a1
    style R fill:#28a745
```

## 🔒 Seguridad

### Flujo de Seguridad JWT

```mermaid
sequenceDiagram
    participant C as Cliente
    participant JF as JwtFilter
    participant JU as JwtUtils
    participant UD as UserDetailsService
    participant SC as SecurityContext
    participant CTRL as Controller
    
    C->>JF: Request + Header Authorization
    JF->>JF: Extraer token del header
    JF->>JU: validateToken(token)
    JU-->>JF: Token válido
    JF->>JU: getUsernameFromToken(token)
    JU-->>JF: username
    JF->>UD: loadUserByUsername(username)
    UD-->>JF: UserDetails
    JF->>SC: setAuthentication(UserDetails)
    JF->>CTRL: Continuar request
    CTRL-->>C: Response
```

## 📊 Swagger/OpenAPI

Acceso a documentación interactiva:
- **URL:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs

## 🚀 Mejoras Implementadas

- ✅ Arquitectura modular con BaseController
- ✅ Reducción de código duplicado (70-80%)
- ✅ Sistema de estadísticas avanzado
- ✅ Análisis temporal y por estado de marcador
- ✅ Logging completo con SLF4J
- ✅ Tests unitarios con JUnit 5 y Mockito
- ✅ Documentación Swagger completa
- ✅ Validaciones de negocio robustas
- ✅ Manejo global de excepciones
