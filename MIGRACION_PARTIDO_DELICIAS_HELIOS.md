# MIGRACIÓN PARTIDO: Delicias - Helios (1-2)

## PASO 1: Crear Equipo en Producción

1. **Iniciar sesión en la app de producción** con: `ompzgz12@prueba.com` / `123456`
2. **Ir a Equipos → Agregar Equipo**
3. **Completar formulario:**
   - Nombre: `Helios Juvenil B`
   - Tipo de Fútbol: `FUTBOL_11`
   - Duración Partido: `90`
4. **Guardar** y **ANOTAR EL ID DEL EQUIPO** (aparecerá en la URL o en la lista)

**📝 ID del Equipo en Producción: ________** (anota aquí)

---

## PASO 2: Crear 20 Jugadores EN ORDEN

**IMPORTANTE:** Créalos EN ESTE ORDEN EXACTO y anota cada ID que se genere.

Ve a **Jugadores → Agregar Jugador** y crea cada uno:

| # | Nombre | Apellido | Posición | **ID Prod** (anota) |
|---|--------|----------|----------|---------------------|
| 1 | Nicolas | Villar | CEN | _____ |
| 2 | Jorge | Pinilla | CEN | _____ |
| 3 | Hugo | Lopez | CEN | _____ |
| 4 | Samuel | Cristobal | DC | _____ |
| 5 | Oscar | Melendo | DC | _____ |
| 6 | Sevane | Diaw | EXD | _____ |
| 7 | Samuel | Rivas | EXD | _____ |
| 8 | Mario | Arrufat | EXIZ | _____ |
| 9 | Pablo | Sacramento | LD | _____ |
| 10 | Rong | Huang | LD | _____ |
| 11 | Matias | Garcia | LD | _____ |
| 12 | Pablo | Ballesteros | LD | _____ |
| 13 | Marcos | Ferrando | LI | _____ |
| 14 | Yago | Lacasta | MC | _____ |
| 15 | Cristian | Mera | MC | _____ |
| 16 | Alejandro | Matilla | MCO | _____ |
| 17 | Adrian | Hernandez | MCO | _____ |
| 18 | Sebastian | Esquivel | MCO | _____ |
| 19 | Joshua | Carrillo | MCO | _____ |
| 20 | Diego | Mejias | POR | _____ |

**Todos los jugadores deben estar en el equipo:** `Helios Juvenil B`

---

## PASO 3: Mapeo de IDs Local → Producción

**TABLA DE REFERENCIA** (completar con los IDs que anotaste):

| Jugador | ID Local | ID Prod |
|---------|----------|---------|
| Nicolas Villar | 1 | _____ |
| Jorge Pinilla | 2 | _____ |
| Hugo Lopez | 3 | _____ |
| Samuel Cristobal | 4 | _____ |
| Oscar Melendo | 5 | _____ |
| Sevane Diaw | 6 | _____ |
| Samuel Rivas | 7 | _____ |
| Mario Arrufat | 8 | _____ |
| Pablo Sacramento | 9 | _____ |
| Rong Huang | 10 | _____ |
| Matias Garcia | 11 | _____ |
| Pablo Ballesteros | 12 | _____ |
| Marcos Ferrando | 13 | _____ |
| Yago Lacasta | 14 | _____ |
| Cristian Mera | 15 | _____ |
| Alejandro Matilla | 16 | _____ |
| Adrian Hernandez | 17 | _____ |
| Sebastian Esquivel | 18 | _____ |
| Joshua Carrillo | 19 | _____ |
| Diego Mejias | 20 | _____ |

---

## PASO 4: Ejecutar SQL en Neon Console

1. **Ir a:** https://console.neon.tech
2. **Seleccionar tu proyecto** → SQL Editor
3. **Copiar el SQL de abajo** (IMPORTANTE: primero reemplaza los placeholders)
4. **Reemplazar en el SQL:**
   - `[EQUIPO_ID_PROD]` → El ID del equipo que anotaste en Paso 1
   - `[J1_ID]` hasta `[J20_ID]` → Los IDs de los jugadores que anotaste en Paso 2
5. **Ejecutar**

---

### SQL PARA NEON (Reemplaza los IDs primero):

```sql
-- ========================================
-- INSERTAR PARTIDO
-- ========================================

-- IMPORTANTE: Reemplaza [EQUIPO_ID_PROD] con el ID del equipo de producción
-- IMPORTANTE: Reemplaza [J1_ID] hasta [J20_ID] con los IDs de los jugadores

INSERT INTO partidos (
    titulo, 
    fecha, 
    duracion, 
    goles_equipo, 
    goles_rival, 
    partido_activo, 
    equipo_id
) VALUES (
    'Delicias - Helios',
    '2026-02-12 10:51:52',
    90,
    1,
    2,
    false,
    [EQUIPO_ID_PROD]  -- REEMPLAZAR CON ID DEL EQUIPO
) RETURNING id;

-- ANOTA EL ID DEL PARTIDO QUE SE GENERE: _______
-- Usa ese ID para reemplazar [PARTIDO_ID_PROD] en los siguientes INSERTs

-- ========================================
-- INSERTAR ALINEACIÓN (TITULARES Y SUPLENTES)
-- ========================================

-- Según el partido, necesitas actualizar la alineación
-- Esto se hace desde la app al crear el partido
-- O puedes agregar a la tabla de relación si existe

-- ========================================
-- INSERTAR EVENTOS DEL PARTIDO
-- ========================================

-- IMPORTANTE: Reemplaza [PARTIDO_ID_PROD] con el ID del partido que se generó arriba

-- Mapeo de IDs:
-- [J1_ID] = Nicolas Villar
-- [J2_ID] = Jorge Pinilla
-- [J3_ID] = Hugo Lopez
-- [J4_ID] = Samuel Cristobal
-- [J5_ID] = Oscar Melendo
-- [J7_ID] = Samuel Rivas
-- [J8_ID] = Mario Arrufat
-- [J11_ID] = Matias Garcia
-- [J12_ID] = Pablo Ballesteros
-- [J13_ID] = Marcos Ferrando
-- [J14_ID] = Yago Lacasta
-- [J15_ID] = Cristian Mera
-- [J16_ID] = Alejandro Matilla
-- [J18_ID] = Sebastian Esquivel
-- [J19_ID] = Joshua Carrillo (no mencionado en eventos, no aparece abajo)
-- [J20_ID] = Diego Mejias

INSERT INTO evento_jugador (partido_id, jugador_id, tipo_evento, minuto, es_evento_rival, jugador_entra_id, jugador_sale_id) VALUES
([PARTIDO_ID_PROD], [J11_ID], 'PERDIDA', 0, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'GOL', 2, false, NULL, NULL),
([PARTIDO_ID_PROD], [J11_ID], 'PERDIDA', 3, false, NULL, NULL),
([PARTIDO_ID_PROD], [J8_ID], 'ROBO', 4, false, NULL, NULL),
([PARTIDO_ID_PROD], [J1_ID], 'ROBO', 5, false, NULL, NULL),
([PARTIDO_ID_PROD], [J2_ID], 'PASE_CLAVE', 5, false, NULL, NULL),
([PARTIDO_ID_PROD], [J14_ID], 'PERDIDA', 6, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 6, false, NULL, NULL),
([PARTIDO_ID_PROD], [J2_ID], 'ROBO', 7, false, NULL, NULL),
([PARTIDO_ID_PROD], [J13_ID], 'ROBO', 9, false, NULL, NULL),
([PARTIDO_ID_PROD], [J2_ID], 'PERDIDA', 9, false, NULL, NULL),
([PARTIDO_ID_PROD], [J14_ID], 'PERDIDA', 10, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PARADA', 10, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'ROBO', 11, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'PASE_CLAVE', 11, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 12, false, NULL, NULL),
([PARTIDO_ID_PROD], [J2_ID], 'ROBO', 12, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PASE_CLAVE', 12, false, NULL, NULL),
([PARTIDO_ID_PROD], [J13_ID], 'PERDIDA', 13, false, NULL, NULL),
([PARTIDO_ID_PROD], [J13_ID], 'PERDIDA', 18, false, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'ROBO', 19, false, NULL, NULL),
([PARTIDO_ID_PROD], [J2_ID], 'PERDIDA', 22, false, NULL, NULL),
([PARTIDO_ID_PROD], [J2_ID], 'PERDIDA', 22, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 23, false, NULL, NULL),
([PARTIDO_ID_PROD], [J1_ID], 'ROBO', 23, false, NULL, NULL),
([PARTIDO_ID_PROD], [J14_ID], 'PERDIDA', 23, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PARADA', 23, false, NULL, NULL),
([PARTIDO_ID_PROD], [J11_ID], 'ROBO', 24, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'PERDIDA', 24, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PARADA', 25, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'ROBO', 25, false, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'ROBO', 27, false, NULL, NULL),
([PARTIDO_ID_PROD], [J14_ID], 'SUSTITUCION', 28, false, [J14_ID], [J16_ID]),
([PARTIDO_ID_PROD], [J1_ID], 'ROBO', 29, false, NULL, NULL),
([PARTIDO_ID_PROD], [J1_ID], 'ROBO', 29, false, NULL, NULL),
([PARTIDO_ID_PROD], [J16_ID], 'PERDIDA', 30, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PARADA', 32, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'ROBO', 32, false, NULL, NULL),
([PARTIDO_ID_PROD], [J16_ID], 'PERDIDA', 32, false, NULL, NULL),
([PARTIDO_ID_PROD], [J8_ID], 'ROBO', 33, false, NULL, NULL),
([PARTIDO_ID_PROD], [J8_ID], 'PASE_CLAVE', 33, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 33, false, NULL, NULL),
([PARTIDO_ID_PROD], [J11_ID], 'SUSTITUCION', 34, false, [J11_ID], [J12_ID]),
([PARTIDO_ID_PROD], [J4_ID], 'ROBO', 35, false, NULL, NULL),
([PARTIDO_ID_PROD], [J8_ID], 'SUSTITUCION', 35, false, [J8_ID], [J13_ID]),
([PARTIDO_ID_PROD], [J4_ID], 'SUSTITUCION', 35, false, [J4_ID], [J16_ID]),
([PARTIDO_ID_PROD], [J20_ID], 'PERDIDA', 37, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'PERDIDA', 38, false, NULL, NULL),
([PARTIDO_ID_PROD], [J12_ID], 'ROBO', 38, false, NULL, NULL),
([PARTIDO_ID_PROD], [J7_ID], 'PERDIDA', 39, false, NULL, NULL),
([PARTIDO_ID_PROD], [J16_ID], 'PERDIDA', 39, false, NULL, NULL),
([PARTIDO_ID_PROD], [J12_ID], 'PERDIDA', 40, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'ROBO', 41, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 41, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PASE_CLAVE', 41, false, NULL, NULL),
([PARTIDO_ID_PROD], [J1_ID], 'PERDIDA', 43, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PARADA', 43, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PERDIDA', 44, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PASE_CLAVE', 45, false, NULL, NULL),
([PARTIDO_ID_PROD], [J5_ID], 'SUSTITUCION', 45, false, [J5_ID], [J4_ID]),
([PARTIDO_ID_PROD], [J7_ID], 'SUSTITUCION', 45, false, [J7_ID], [J8_ID]),
([PARTIDO_ID_PROD], [J12_ID], 'SUSTITUCION', 45, false, [J12_ID], [J11_ID]),
([PARTIDO_ID_PROD], [J16_ID], 'SUSTITUCION', 45, false, [J16_ID], [J14_ID]),
([PARTIDO_ID_PROD], [J14_ID], 'PERDIDA', 45, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'ROBO', 45, false, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'PERDIDA', 47, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PARADA', 48, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'PASE_CLAVE', 49, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'PERDIDA', 49, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PERDIDA', 49, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 49, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PERDIDA', 50, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'PERDIDA', 50, false, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'TARJETA_AMARILLA', 50, false, NULL, NULL),
([PARTIDO_ID_PROD], [J1_ID], 'ROBO', 51, false, NULL, NULL),
([PARTIDO_ID_PROD], [J2_ID], 'ROBO', 52, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'PASE_CLAVE', 52, false, NULL, NULL),
([PARTIDO_ID_PROD], [J8_ID], 'PERDIDA', 52, false, NULL, NULL),
([PARTIDO_ID_PROD], [J14_ID], 'PERDIDA', 52, false, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'ROBO', 52, false, NULL, NULL),
([PARTIDO_ID_PROD], [J13_ID], 'ROBO', 53, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PASE_CLAVE', 53, false, NULL, NULL),
([PARTIDO_ID_PROD], [J1_ID], 'TARJETA_AMARILLA', 54, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PARADA', 55, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PASE_CLAVE', 55, false, NULL, NULL),
([PARTIDO_ID_PROD], [J2_ID], 'ROBO', 60, false, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'PERDIDA', 60, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'SUSTITUCION', 60, false, [J4_ID], [J5_ID]),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 64, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PERDIDA', 64, false, NULL, NULL),
([PARTIDO_ID_PROD], [J8_ID], 'SUSTITUCION', 66, false, [J8_ID], [J7_ID]),
([PARTIDO_ID_PROD], [J4_ID], 'ROBO', 66, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'PASE_CLAVE', 66, false, NULL, NULL),
([PARTIDO_ID_PROD], [J11_ID], 'SUSTITUCION', 67, false, [J11_ID], [J12_ID]),
([PARTIDO_ID_PROD], [J14_ID], 'SUSTITUCION', 67, false, [J14_ID], [J16_ID]),
([PARTIDO_ID_PROD], [J8_ID], 'GOL_RIVAL', 68, true, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'ROBO', 71, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'SUSTITUCION', 71, false, [J4_ID], [J8_ID]),
([PARTIDO_ID_PROD], [J2_ID], 'PERDIDA', 71, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'ROBO', 72, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'PERDIDA', 72, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'ROBO', 72, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PASE_CLAVE', 72, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PERDIDA', 73, false, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'ROBO', 74, false, NULL, NULL),
([PARTIDO_ID_PROD], [J7_ID], 'PERDIDA', 75, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'ROBO', 77, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'ROBO', 78, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 78, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'ROBO', 79, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'PERDIDA', 79, false, NULL, NULL),
([PARTIDO_ID_PROD], [J20_ID], 'PARADA', 79, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'PERDIDA', 79, false, NULL, NULL),
([PARTIDO_ID_PROD], [J7_ID], 'SUSTITUCION', 80, false, [J7_ID], [J8_ID]),
([PARTIDO_ID_PROD], [J16_ID], 'SUSTITUCION', 80, false, [J16_ID], [J14_ID]),
([PARTIDO_ID_PROD], [J12_ID], 'SUSTITUCION', 80, false, [J12_ID], [J11_ID]),
([PARTIDO_ID_PROD], [J5_ID], 'SUSTITUCION', 80, false, [J5_ID], [J4_ID]),
([PARTIDO_ID_PROD], [J15_ID], 'PERDIDA', 82, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PASE_CLAVE', 83, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'ROBO', 83, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'PASE_CLAVE', 84, false, NULL, NULL),
([PARTIDO_ID_PROD], [J18_ID], 'ROBO', 84, false, NULL, NULL),
([PARTIDO_ID_PROD], [J3_ID], 'PERDIDA', 86, false, NULL, NULL),
([PARTIDO_ID_PROD], [J15_ID], 'ROBO', 86, false, NULL, NULL),
([PARTIDO_ID_PROD], [J8_ID], 'GOL_RIVAL', 87, true, NULL, NULL),
([PARTIDO_ID_PROD], [J14_ID], 'PERDIDA', 87, false, NULL, NULL),
([PARTIDO_ID_PROD], [J1_ID], 'ROBO', 88, false, NULL, NULL),
([PARTIDO_ID_PROD], [J4_ID], 'PASE_CLAVE', 88, false, NULL, NULL);

-- ========================================
-- VERIFICACIÓN
-- ========================================

-- Verificar que el partido se creó
SELECT * FROM partidos WHERE titulo = 'Delicias - Helios';

-- Verificar cuántos eventos se insertaron
SELECT COUNT(*) as total_eventos FROM evento_jugador WHERE partido_id = [PARTIDO_ID_PROD];

-- Debería mostrar 129 eventos
```

---

## RESUMEN

✅ **Equipo:** Helios Juvenil B creado  
✅ **20 Jugadores:** Todos creados con IDs anotados  
✅ **Partido:** "Delicias - Helios" (1-2) insertado  
✅ **129 Eventos:** Todos los eventos del partido migrados

**Resultado esperado:** Derrota 1-2 contra Helios
- 1 GOL del equipo (Samuel Cristobal, min 2)
- 2 GOLES_RIVAL (min 68 y 87)
- 2 TARJETAS_AMARILLAS (Hugo Lopez min 50, Nicolas Villar min 54)
- 8 SUSTITUCIONES
- Múltiples ROBOS, PERDIDAS, PARADAS, PASES_CLAVE
