# 🌐 Frontend Web - Angular

## 📋 Índice

1. [Arquitectura General](#arquitectura-general)
2. [Estructura de Componentes](#estructura-de-componentes)
3. [Servicios](#servicios)
4. [Rutas y Navegación](#rutas-y-navegación)
5. [Flujos de Usuario](#flujos-de-usuario)

## Arquitectura General

```mermaid
graph TB
    subgraph "Presentación"
        COMP[Componentes Angular]
        TEMP[Templates HTML]
    end
    
    subgraph "Lógica de Negocio"
        SERV[Servicios]
        GUARD[Guards]
        INT[Interceptores]
    end
    
    subgraph "Comunicación"
        HTTP[HttpClient]
        API[Backend API]
    end
    
    COMP --> TEMP
    COMP --> SERV
    SERV --> GUARD
    SERV --> INT
    INT --> HTTP
    HTTP --> API
    
    style COMP fill:#61dafb
    style SERV fill:#ffd700
    style API fill:#6db33f
```

## Estructura de Componentes

```
src/app/
├── components/              # Componentes reutilizables
│   ├── navbar/             # Barra de navegación
│   └── modal/              # Modales reutilizables
│
├── pages/                   # Páginas principales
│   ├── login/              # Página de login
│   ├── perfil/             # Perfil de usuario
│   └── unauthorized/       # Página de acceso denegado
│
├── lista-jugadores/        # Gestión de jugadores
│   ├── lista-jugadores.component.ts
│   ├── lista-jugadores.component.html
│   └── lista-jugadores.component.css
│
├── registrar-jugador/      # Formulario nuevo jugador
│   ├── registrar-jugador.component.ts
│   └── registrar-jugador.component.html
│
├── actualizar-jugador/     # Formulario editar jugador
│   ├── actualizar-jugador.component.ts
│   └── actualizar-jugador.component.html
│
├── jugador-detalles/       # Vista detallada jugador
│   ├── jugador-detalles.component.ts
│   └── jugador-detalles.component.html
│
├── partido-crear/          # Crear nuevo partido
│   ├── partido-crear.component.ts
│   └── partido-crear.component.html
│
├── partido-modo/           # Modo partido (en vivo)
│   ├── partido-modo.component.ts
│   ├── partido-modo.component.html
│   └── partido-modo.component.css
│
├── historial-partidos/     # Lista de partidos
│   ├── historial-partidos.component.ts
│   └── historial-partidos.component.html
│
├── graficos/               # Dashboard de estadísticas
│   ├── graficos.component.ts
│   └── graficos.component.html
│
├── services/               # Servicios
│   ├── auth.service.ts
│   ├── auth.guard.ts
│   ├── auth.interceptor.ts
│   ├── jugador.service.ts
│   ├── equipo.service.ts
│   ├── partido.service.ts
│   └── evento-jugador.service.ts
│
├── models/                 # Modelos TypeScript
│   ├── auth.model.ts
│   ├── jugador.ts
│   ├── equipo.ts
│   ├── partido.ts
│   └── evento-jugador.ts
│
├── app-routing.module.ts   # Configuración de rutas
├── app.module.ts           # Módulo principal
└── app.component.ts        # Componente raíz
```

## Arquitectura de Componentes

```mermaid
graph TD
    APP[App Component]
    APP --> NAV[Navbar Component]
    APP --> ROUTER[Router Outlet]
    
    ROUTER --> LOGIN[Login Page]
    ROUTER --> TABS[Tabs Layout]
    
    TABS --> LIST[Lista Jugadores]
    TABS --> HIST[Historial Partidos]
    TABS --> GRAF[Gráficos Estadísticas]
    
    LIST --> DETALLE[Jugador Detalles]
    LIST --> EDIT[Actualizar Jugador]
    LIST --> NEW[Registrar Jugador]
    
    HIST --> CREATE[Crear Partido]
    HIST --> MODO[Modo Partido]
    
    MODO --> ALINEACION[Selección Alineación]
    MODO --> EVENTOS[Registro Eventos]
    MODO --> SUST[Sustituciones]
    
    style APP fill:#61dafb
    style TABS fill:#ffd700
    style MODO fill:#ff6b6b
```

## Servicios

### AuthService

```mermaid
sequenceDiagram
    participant C as Component
    participant AS as AuthService
    participant LS as LocalStorage
    participant API as Backend API
    
    C->>AS: login(username, password)
    AS->>API: POST /auth/generate-token
    API-->>AS: {token, username}
    AS->>LS: Guardar token
    AS->>LS: Guardar username
    AS-->>C: Observable<any>
    
    C->>AS: isLoggedIn()
    AS->>LS: Obtener token
    LS-->>AS: token
    AS->>AS: Validar token no nulo
    AS-->>C: boolean
    
    C->>AS: logout()
    AS->>LS: Remover token
    AS->>LS: Remover username
    AS-->>C: void
```

**Métodos principales:**
```typescript
- login(username, password): Observable<any>
- logout(): void
- isLoggedIn(): boolean
- getToken(): string | null
- getUsername(): string | null
- getCurrentUser(): Observable<any>
```

### JugadorService

```mermaid
sequenceDiagram
    participant C as Component
    participant JS as JugadorService
    participant INT as Interceptor
    participant API as Backend API
    
    C->>JS: listarJugadores()
    JS->>INT: GET /jugadores + Token
    INT->>API: HTTP Request
    API-->>INT: List<JugadorDTO>
    INT-->>JS: Observable
    JS-->>C: Observable<Jugador[]>
    
    C->>JS: guardarJugador(jugador)
    JS->>INT: POST /jugadores + Token
    INT->>API: HTTP Request
    API-->>INT: JugadorDTO
    INT-->>JS: Observable
    JS-->>C: Observable<Jugador>
```

**Métodos principales:**
```typescript
- listarJugadores(): Observable<Jugador[]>
- guardarJugador(jugador: Jugador): Observable<Jugador>
- obtenerJugadorPorId(id: number): Observable<Jugador>
- actualizarJugador(id: number, jugador: Jugador): Observable<Jugador>
- eliminarJugador(id: number): Observable<void>
- obtenerJugadoresPorEquipo(equipoId: number): Observable<Jugador[]>
```

### PartidoService

**Métodos principales:**
```typescript
- crearPartido(partido: Partido): Observable<Partido>
- obtenerPartidos(): Observable<Partido[]>
- obtenerPartidoPorId(id: number): Observable<Partido>
- actualizarPartido(id: number, partido: Partido): Observable<Partido>
- finalizarPartido(id: number): Observable<Partido>
- actualizarAlineacion(id: number, alineacion: any): Observable<Partido>
- eliminarPartido(id: number): Observable<void>
```

### EventoJugadorService

**Métodos principales:**
```typescript
- registrarEvento(evento: EventoJugador): Observable<EventoJugador>
- obtenerEventosPorPartido(partidoId: number): Observable<EventoJugador[]>
- obtenerEventosPorJugador(jugadorId: number): Observable<EventoJugador[]>
- eliminarEvento(id: number): Observable<void>
```

## Guards e Interceptores

### AuthGuard

```mermaid
flowchart TD
    A[Usuario intenta acceder a ruta] --> B{AuthGuard.canActivate}
    B --> C{¿Usuario autenticado?}
    C -->|Sí| D[Permitir acceso]
    C -->|No| E[Redirigir a /login]
    D --> F[Cargar componente]
    E --> G[Mostrar página login]
    
    style D fill:#28a745
    style E fill:#dc3545
```

### GuestGuard

```mermaid
flowchart TD
    A[Usuario intenta acceder a login] --> B{GuestGuard.canActivate}
    B --> C{¿Usuario autenticado?}
    C -->|No| D[Permitir acceso a login]
    C -->|Sí| E[Redirigir a /tabs]
    D --> F[Mostrar formulario login]
    E --> G[Mostrar dashboard]
    
    style D fill:#28a745
    style E fill:#ffc107
```

### AuthInterceptor

```mermaid
sequenceDiagram
    participant C as Component
    participant INT as AuthInterceptor
    participant AS as AuthService
    participant API as Backend
    
    C->>INT: HTTP Request
    INT->>AS: getToken()
    AS-->>INT: JWT Token
    INT->>INT: Agregar header Authorization
    INT->>API: Request + Bearer Token
    API-->>INT: Response
    INT-->>C: Observable<Response>
    
    Note over INT,API: Si token inválido o expirado
    API-->>INT: 401 Unauthorized
    INT->>INT: Interceptar error
    INT->>AS: logout()
    INT->>C: Redirigir a /login
```

## Rutas y Navegación

### Configuración de Rutas

```mermaid
graph LR
    ROOT[/] --> LOGIN[/login]
    ROOT --> TABS[/tabs]
    
    TABS --> TAB1[/tabs/jugadores]
    TABS --> TAB2[/tabs/partidos]
    TABS --> TAB3[/tabs/estadisticas]
    
    TAB1 --> NEW[/registrar-jugador]
    TAB1 --> EDIT[/actualizar-jugador/:id]
    TAB1 --> DETAIL[/jugador-detalles/:id]
    
    TAB2 --> CREATE[/partido-crear]
    TAB2 --> MODO[/partido-modo/:id]
    
    ROOT --> PERFIL[/perfil]
    ROOT --> UNAUTH[/unauthorized]
    
    style LOGIN fill:#ffc107
    style TABS fill:#61dafb
    style MODO fill:#ff6b6b
```

### Tabla de Rutas

| Ruta | Componente | Guard | Descripción |
|------|-----------|-------|-------------|
| `/` | - | - | Redirige a `/login` |
| `/login` | LoginPage | GuestGuard | Página de autenticación |
| `/tabs` | TabsPage | AuthGuard | Layout principal con tabs |
| `/tabs/jugadores` | ListaJugadores | AuthGuard | Lista de jugadores |
| `/tabs/partidos` | HistorialPartidos | AuthGuard | Historial de partidos |
| `/tabs/estadisticas` | Graficos | AuthGuard | Dashboard estadísticas |
| `/registrar-jugador` | RegistrarJugador | AuthGuard | Formulario nuevo jugador |
| `/actualizar-jugador/:id` | ActualizarJugador | AuthGuard | Formulario editar jugador |
| `/jugador-detalles/:id` | JugadorDetalles | AuthGuard | Vista detallada jugador |
| `/partido-crear` | PartidoCrear | AuthGuard | Formulario nuevo partido |
| `/partido-modo/:id` | PartidoModo | AuthGuard | Modo partido en vivo |
| `/perfil` | PerfilPage | AuthGuard | Perfil del usuario |
| `/unauthorized` | UnauthorizedPage | - | Acceso denegado |

## Flujos de Usuario

### 1. Flujo de Login

```mermaid
sequenceDiagram
    participant U as Usuario
    participant L as Login Component
    participant AS as AuthService
    participant API as Backend
    participant R as Router
    
    U->>L: Ingresa credenciales
    U->>L: Clic en "Iniciar Sesión"
    L->>L: Validar formulario
    L->>AS: login(username, password)
    AS->>API: POST /auth/generate-token
    API-->>AS: {token, username}
    AS->>AS: Guardar en localStorage
    AS-->>L: Login exitoso
    L->>R: navigate(['/tabs'])
    R-->>U: Mostrar dashboard
```

### 2. Flujo de Gestión de Jugadores

```mermaid
flowchart TD
    A[Lista Jugadores] --> B{Acción del usuario}
    B -->|Nuevo| C[Formulario Registrar]
    B -->|Editar| D[Formulario Actualizar]
    B -->|Ver| E[Detalles Jugador]
    B -->|Eliminar| F{Confirmar?}
    
    C --> G[Completar datos]
    G --> H[Enviar al backend]
    H --> I[Mostrar mensaje éxito]
    I --> A
    
    D --> J[Cargar datos actuales]
    J --> K[Modificar campos]
    K --> H
    
    E --> L[Mostrar información]
    L --> M[Ver estadísticas]
    L --> N[Botón editar]
    N --> D
    
    F -->|Sí| O[Eliminar del backend]
    F -->|No| A
    O --> P[Actualizar lista]
    P --> A
    
    style A fill:#61dafb
    style C fill:#28a745
    style D fill:#ffc107
    style F fill:#dc3545
```

### 3. Flujo de Creación de Partido

```mermaid
sequenceDiagram
    participant U as Usuario
    participant PC as Partido Crear
    participant PS as PartidoService
    participant R as Router
    
    U->>PC: Accede a formulario
    PC->>PC: Cargar equipos disponibles
    U->>PC: Completa formulario
    Note over U,PC: Rival, fecha, ubicación, equipo
    U->>PC: Clic en "Crear Partido"
    PC->>PC: Validar datos
    PC->>PS: crearPartido(partido)
    PS-->>PC: Partido creado
    PC->>PC: Mostrar mensaje éxito
    PC->>R: navigate(['/partido-modo', partidoId])
    R-->>U: Abrir modo partido
```

### 4. Flujo de Modo Partido (En Vivo)

```mermaid
flowchart TD
    START[Iniciar Modo Partido] --> LOAD[Cargar datos partido]
    LOAD --> ALIN{¿Alineación definida?}
    
    ALIN -->|No| SELECT[Seleccionar Alineación]
    SELECT --> TITUL[Elegir titulares]
    TITUL --> SUPL[Elegir suplentes]
    SUPL --> SAVE[Guardar alineación]
    SAVE --> MAIN
    
    ALIN -->|Sí| MAIN[Panel Principal]
    
    MAIN --> EVENTS{Eventos disponibles}
    EVENTS --> GOL[Registrar Gol]
    EVENTS --> ASIST[Registrar Asistencia]
    EVENTS --> TARJ[Registrar Tarjeta]
    EVENTS --> PASE[Registrar Pase Clave]
    EVENTS --> TIRO[Registrar Tiro]
    EVENTS --> ROBO[Registrar Robo]
    EVENTS --> PARADA[Registrar Parada]
    EVENTS --> GOLRIV[Registrar Gol Rival]
    
    GOL --> UPDATE[Actualizar marcador]
    ASIST --> UPDATE
    TARJ --> UPDATE
    PASE --> UPDATE
    TIRO --> UPDATE
    ROBO --> UPDATE
    PARADA --> UPDATE
    GOLRIV --> UPDATE
    
    UPDATE --> MAIN
    
    MAIN --> SUST[Realizar Sustitución]
    SUST --> MODAL[Modal sustitución]
    MODAL --> SELE[Seleccionar jugadores]
    SELE --> CONF[Confirmar cambio]
    CONF --> MAIN
    
    MAIN --> FIN{¿Finalizar?}
    FIN -->|No| MAIN
    FIN -->|Sí| FINAL[Finalizar Partido]
    FINAL --> CALC[Calcular estadísticas]
    CALC --> RES[Mostrar resumen]
    RES --> END[Volver a historial]
    
    style START fill:#6db33f
    style MAIN fill:#61dafb
    style UPDATE fill:#ffc107
    style FINAL fill:#dc3545
    style END fill:#28a745
```

### 5. Flujo de Registro de Eventos

```mermaid
sequenceDiagram
    participant U as Entrenador
    participant PM as Partido Modo
    participant MODAL as Modal Evento
    participant ES as EventoService
    participant API as Backend
    
    U->>PM: Clic en botón evento (ej: Gol)
    PM->>MODAL: Abrir modal
    MODAL->>MODAL: Cargar jugadores activos
    U->>MODAL: Seleccionar jugador
    U->>MODAL: Ingresar minuto
    U->>MODAL: Clic en "Registrar"
    MODAL->>MODAL: Validar datos
    MODAL->>ES: registrarEvento(evento)
    ES->>API: POST /eventos
    API-->>ES: Evento creado
    ES-->>MODAL: Confirmación
    MODAL->>MODAL: Cerrar modal
    MODAL->>PM: Actualizar contadores
    PM->>PM: Actualizar marcador
    PM->>PM: Actualizar lista eventos
    PM-->>U: Mostrar notificación
```

### 6. Flujo de Sustituciones

```mermaid
sequenceDiagram
    participant U as Entrenador
    participant PM as Partido Modo
    participant MODAL as Modal Sustitución
    participant PS as PartidoService
    
    U->>PM: Clic en "Sustitución"
    PM->>MODAL: Abrir modal
    MODAL->>MODAL: Cargar titulares actuales
    MODAL->>MODAL: Cargar suplentes disponibles
    U->>MODAL: Seleccionar jugador que sale
    U->>MODAL: Seleccionar jugador que entra
    U->>MODAL: Ingresar minuto
    U->>MODAL: Clic en "Confirmar"
    MODAL->>MODAL: Validar selección
    MODAL->>PS: registrarEvento(SUSTITUCION)
    PS-->>MODAL: Evento registrado
    MODAL->>MODAL: Actualizar listas locales
    MODAL->>MODAL: Cerrar modal
    MODAL->>PM: Actualizar alineación
    PM->>PM: Mover jugador sale → suplentes
    PM->>PM: Mover jugador entra → titulares
    PM-->>U: Mostrar cambio realizado
```

### 7. Flujo de Visualización de Estadísticas

```mermaid
flowchart TD
    A[Dashboard Estadísticas] --> B[Selector de Equipo]
    B --> C[Cargar estadísticas]
    
    C --> D{Tipo de vista}
    D --> E[Estadísticas Equipo]
    D --> F[Estadísticas Jugadores]
    
    E --> G[Partidos jugados]
    E --> H[Victorias/Empates/Derrotas]
    E --> I[Goles favor/contra]
    E --> J[Top performers]
    
    F --> K[Lista jugadores]
    K --> L[Por cada jugador]
    L --> M[Goles y asistencias]
    L --> N[Tarjetas]
    L --> O[Minutos jugados]
    L --> P[Pases/Tiros/Robos]
    
    G --> Q[Gráficos visuales]
    H --> Q
    I --> Q
    M --> Q
    N --> Q
    O --> Q
    P --> Q
    
    Q --> R{Seleccionar otro equipo?}
    R -->|Sí| B
    R -->|No| S[Fin]
    
    style A fill:#61dafb
    style D fill:#ffc107
    style Q fill:#6db33f
```

## Componentes Clave

### NavbarComponent

**Responsabilidades:**
- Mostrar logo y título de la aplicación
- Mostrar nombre de usuario actual
- Botón de cerrar sesión
- Navegación entre secciones

### PartidoModoComponent

**Características principales:**
- ⚡ Modo tiempo real con actualización automática
- 📊 Marcador en vivo (goles equipo vs rival)
- 🔄 Sistema de sustituciones ilimitadas
- 📝 Registro de 8 tipos de eventos
- 👥 Gestión de alineación (titulares/suplentes)
- ⏱️ Control de minutos jugados
- 🎯 Botones de acción rápida
- ✅ Finalización y cálculo automático de estadísticas

### GraficosComponent

**Características principales:**
- 📈 Visualización con Chart.js
- 🎯 Selector de equipo dinámico
- 📊 Métricas clave del equipo
- 👤 Estadísticas individuales de jugadores
- 🏆 Rankings y comparativas
- 📅 Filtro por temporada

## 🎨 Estilos y UI

- **Framework CSS:** Bootstrap 5
- **Componentes:** Angular Material (modales, forms)
- **Iconos:** Bootstrap Icons / Font Awesome
- **Responsive:** Mobile-first design
- **Temas:** Colores del club personalizables

## 🚀 Optimizaciones

- ✅ Lazy loading de módulos
- ✅ Preloading strategy de rutas
- ✅ Change detection OnPush en componentes críticos
- ✅ Unsubscribe automático con takeUntil
- ✅ Manejo de errores con toast notifications
- ✅ Caché de datos en servicios
- ✅ Validaciones reactivas en formularios
