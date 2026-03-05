# 📊 Referencia de API REST

## 🔐 Autenticación

### POST /api/auth/generate-token
**Descripción:** Login y generación de JWT token

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin"
}
```

**Códigos de Estado:**
- `200 OK` - Login exitoso
- `401 Unauthorized` - Credenciales inválidas
- `400 Bad Request` - Datos faltantes

---

### GET /api/auth/actual-usuario
**Descripción:** Obtener información del usuario actual

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 1,
  "username": "admin",
  "nombre": "Administrador",
  "apellido": "Sistema",
  "telefono": "123456789"
}
```

---

## 👥 Jugadores

### GET /api/v2/jugadores
**Descripción:** Listar todos los jugadores del usuario actual

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "edad": 15,
    "posicion": "Delantero",
    "equipoId": 1,
    "equipoNombre": "Juvenil A"
  }
]
```

---

### GET /api/v2/jugadores/{id}
**Descripción:** Obtener jugador por ID

**Path Params:** `id` (Long)

**Response:**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "edad": 15,
  "posicion": "Delantero",
  "equipoId": 1,
  "equipoNombre": "Juvenil A"
}
```

**Códigos de Estado:**
- `200 OK` - Jugador encontrado
- `404 Not Found` - Jugador no existe
- `401 Unauthorized` - Token inválido

---

### POST /api/v2/jugadores
**Descripción:** Crear nuevo jugador

**Request Body:**
```json
{
  "nombre": "Carlos",
  "apellido": "López",
  "edad": 14,
  "posicion": "Mediocampista",
  "equipoId": 1
}
```

**Response:**
```json
{
  "id": 5,
  "nombre": "Carlos",
  "apellido": "López",
  "edad": 14,
  "posicion": "Mediocampista",
  "equipoId": 1,
  "equipoNombre": "Juvenil A"
}
```

**Códigos de Estado:**
- `201 Created` - Jugador creado exitosamente
- `400 Bad Request` - Datos inválidos
- `401 Unauthorized` - Token inválido

---

### PUT /api/v2/jugadores/{id}
**Descripción:** Actualizar jugador existente

**Path Params:** `id` (Long)

**Request Body:**
```json
{
  "nombre": "Carlos",
  "apellido": "López García",
  "edad": 15,
  "posicion": "Mediocampista Defensivo",
  "equipoId": 1
}
```

**Response:** Mismo formato que POST

**Códigos de Estado:**
- `200 OK` - Actualización exitosa
- `404 Not Found` - Jugador no existe
- `400 Bad Request` - Datos inválidos

---

### DELETE /api/v2/jugadores/{id}
**Descripción:** Eliminar jugador (y sus estadísticas)

**Path Params:** `id` (Long)

**Response:** `204 No Content`

**Códigos de Estado:**
- `204 No Content` - Eliminación exitosa
- `404 Not Found` - Jugador no existe
- `401 Unauthorized` - Token inválido

---

## ⚽ Partidos

### GET /api/v2/partidos
**Descripción:** Listar todos los partidos

**Query Params:**
- `equipoId` (opcional) - Filtrar por equipo

**Response:**
```json
[
  {
    "id": 1,
    "fecha": "2026-01-19T15:00:00",
    "rival": "Escuela Deportiva Sur",
    "ubicacion": "Estadio Municipal",
    "partidoActivo": false,
    "golesEquipo": 3,
    "golesRival": 1,
    "resultado": "VICTORIA",
    "equipoId": 1,
    "titularesIds": [1, 2, 3, 4, 5, 6, 7],
    "suplentesIds": [8, 9, 10]
  }
]
```

---

### POST /api/v2/partidos
**Descripción:** Crear nuevo partido

**Request Body:**
```json
{
  "fecha": "2026-01-20T16:00:00",
  "rival": "Club Deportivo Norte",
  "ubicacion": "Cancha Principal",
  "equipoId": 1
}
```

**Response:**
```json
{
  "id": 5,
  "fecha": "2026-01-20T16:00:00",
  "rival": "Club Deportivo Norte",
  "ubicacion": "Cancha Principal",
  "partidoActivo": true,
  "golesEquipo": 0,
  "golesRival": 0,
  "resultado": null,
  "equipoId": 1,
  "titularesIds": [],
  "suplentesIds": []
}
```

---

### PUT /api/v2/partidos/{id}/finalizar
**Descripción:** Finalizar partido y calcular estadísticas

**Path Params:** `id` (Long)

**Response:** PartidoDTO con resultado final

**Proceso automático:**
1. Desactiva el partido (`partidoActivo = false`)
2. Cuenta goles desde eventos (GOL vs GOL_RIVAL)
3. Determina resultado (VICTORIA/EMPATE/DERROTA)
4. Actualiza estadísticas de todos los jugadores
5. Actualiza estadísticas del equipo
6. Calcula distribuciones temporales
7. Identifica top performers

---

### PUT /api/v2/partidos/{id}/alineacion
**Descripción:** Actualizar titulares y suplentes

**Request Body:**
```json
{
  "titularesIds": [1, 2, 3, 4, 5, 6, 7],
  "suplentesIds": [8, 9, 10, 11]
}
```

**Response:** PartidoDTO actualizado

---

## 📊 Eventos

### POST /api/v2/eventos
**Descripción:** Registrar evento en partido

**Request Body:**
```json
{
  "tipo": "GOL",
  "minuto": 25,
  "partidoId": 1,
  "jugadorId": 3,
  "jugadorSaleId": null,
  "jugadorEntraId": null
}
```

**Tipos de Evento:**
- `GOL` - Gol del equipo
- `ASISTENCIA` - Asistencia
- `TARJETA_AMARILLA` - Tarjeta amarilla
- `TARJETA_ROJA` - Tarjeta roja
- `PASE_CLAVE` - Pase clave
- `TIRO_A_PUERTA` - Tiro a puerta
- `ROBO` - Robo de balón
- `PARADA` - Parada del portero
- `GOL_RIVAL` - Gol del equipo rival
- `SUSTITUCION` - Sustitución (requiere jugadorSaleId y jugadorEntraId)

**Response:**
```json
{
  "id": 42,
  "tipo": "GOL",
  "minuto": 25,
  "partidoId": 1,
  "jugadorId": 3,
  "jugadorNombre": "Juan Pérez"
}
```

---

### GET /api/v2/eventos/partido/{partidoId}
**Descripción:** Listar eventos de un partido

**Response:**
```json
[
  {
    "id": 1,
    "tipo": "GOL",
    "minuto": 15,
    "jugadorId": 3,
    "jugadorNombre": "Juan Pérez"
  },
  {
    "id": 2,
    "tipo": "ASISTENCIA",
    "minuto": 15,
    "jugadorId": 5,
    "jugadorNombre": "Carlos López"
  }
]
```

---

### DELETE /api/v2/eventos/{id}
**Descripción:** Eliminar evento registrado

**Efecto:** Si el partido está activo, actualiza contadores en tiempo real

---

## 📈 Estadísticas

### GET /api/estadisticas/jugador/{jugadorId}
**Descripción:** Obtener estadísticas completas de un jugador

**Response:**
```json
{
  "id": 1,
  "jugadorId": 3,
  "jugadorNombre": "Juan Pérez",
  "partidosJugados": 10,
  "goles": 8,
  "asistencias": 5,
  "tarjetasAmarillas": 2,
  "tarjetasRojas": 0,
  "minutosJugados": 720,
  "pasesClaveTotal": 15,
  "tirosAPuertaTotal": 22,
  "robosTotal": 12,
  "paradasTotal": 0,
  "golesPor90": 1.0,
  "asistenciasPor90": 0.63,
  "pasesClaveGanando": 3,
  "pasesClaveEmpatando": 7,
  "pasesClaveEmpate": 5,
  "perfil": "LIDER"
}
```

---

### GET /api/estadisticas/equipo/{equipoId}
**Descripción:** Obtener estadísticas del equipo

**Response:**
```json
{
  "id": 1,
  "equipoId": 1,
  "equipoNombre": "Juvenil A",
  "partidosJugados": 15,
  "victorias": 10,
  "empates": 3,
  "derrotas": 2,
  "golesAFavor": 35,
  "golesEnContra": 12,
  "pasesClaveTotal": 120,
  "tirosAPuertaTotal": 180,
  "tirosRecibidosTotal": 95,
  "robosTotal": 85,
  "mayorPasador": "Juan Pérez (25)",
  "mayorTirador": "Carlos López (30)",
  "mayorRecuperador": "Luis Martínez (18)"
}
```

---

### GET /api/estadisticas/equipo/{equipoId}/jugadores
**Descripción:** Estadísticas de todos los jugadores del equipo

**Response:** Array de EstadisticasJugadorDTO

---

### GET /api/estadisticas/partido/{partidoId}
**Descripción:** Estadísticas detalladas de un partido individual

**Response:**
```json
{
  "partidoId": 1,
  "fecha": "2026-01-19T15:00:00",
  "rival": "Escuela Deportiva Sur",
  "resultado": "VICTORIA",
  "golesEquipo": 3,
  "golesRival": 1,
  "totalEventos": 47,
  "jugadores": [
    {
      "jugadorId": 3,
      "jugadorNombre": "Juan Pérez",
      "goles": 2,
      "asistencias": 1,
      "pasesClaves": 3,
      "tiros": 5,
      "robos": 2,
      "tarjetasAmarillas": 0,
      "tarjetasRojas": 0,
      "minutosJugados": 90
    }
  ],
  "distribucionGoles": {
    "0_15": 0,
    "16_30": 1,
    "31_45": 1,
    "46_60": 0,
    "61_75": 1,
    "76_90": 0
  }
}
```

---

## ⚙️ Equipos

### GET /api/equipos
**Descripción:** Listar equipos del usuario

**Response:**
```json
[
  {
    "id": 1,
    "nombre": "Juvenil A",
    "categoria": "Sub-17",
    "tipoFutbol": 11,
    "usuarioId": 1
  }
]
```

---

### POST /api/equipos/registrar
**Descripción:** Crear nuevo equipo

**Request Body:**
```json
{
  "nombre": "Infantil B",
  "categoria": "Sub-15",
  "tipoFutbol": 7
}
```

**Response:** EquipoDTO creado

---

## 🔍 Códigos de Estado HTTP

| Código | Significado | Uso |
|--------|-------------|-----|
| 200 | OK | Request exitoso (GET, PUT) |
| 201 | Created | Recurso creado (POST) |
| 204 | No Content | Eliminación exitosa (DELETE) |
| 400 | Bad Request | Datos inválidos o faltantes |
| 401 | Unauthorized | Token JWT inválido o expirado |
| 403 | Forbidden | Sin permisos para el recurso |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Conflicto (ej: jugador ya existe) |
| 500 | Server Error | Error interno del servidor |

---

## 🔒 Headers Requeridos

### Todas las peticiones (excepto login)
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
Accept: application/json
```

---

## 📝 Ejemplos con cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/generate-token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Crear Jugador
```bash
curl -X POST http://localhost:8080/api/v2/jugadores \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Pedro",
    "apellido": "Sánchez",
    "edad": 16,
    "posicion": "Defensa",
    "equipoId": 1
  }'
```

### Registrar Gol
```bash
curl -X POST http://localhost:8080/api/v2/eventos \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "GOL",
    "minuto": 35,
    "partidoId": 1,
    "jugadorId": 3
  }'
```

### Finalizar Partido
```bash
curl -X PUT http://localhost:8080/api/v2/partidos/1/finalizar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🌐 URLs Base

### Desarrollo
```
Backend API: http://localhost:8080/api
Swagger UI:  http://localhost:8080/swagger-ui.html
```

### Producción
```
Backend API: https://tudominio.com/api
Swagger UI:  https://tudominio.com/swagger-ui.html
```
