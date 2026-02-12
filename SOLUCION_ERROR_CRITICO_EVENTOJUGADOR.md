# 🔧 Solución a Error Crítico: Inconsistencia en EventoJugador

**Fecha:** 27 Enero 2026  
**Problema:** jugador_id nullable permite eventos huérfanos que corrompen estadísticas

## 🚨 Problema Identificado

```java
@JoinColumn(name = "jugador_id", nullable = true)  // ❌ PROBLEMA
```

**Impacto:**
- ✗ Permite eventos sin jugador asociado
- ✗ Estadísticas inconsistentes
- ✗ Pérdida de integridad referencial
- ✗ Consultas complejas que fallan con NULL

## ✅ Solución Implementada

### 1. Modelo de Datos (EventoJugador.java)

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "jugador_id", nullable = false)  // ✅ OBLIGATORIO
@JsonIgnoreProperties({"eventos", "equipo", "hibernateLazyInitializer", "handler"})
private Jugador jugador;

// ✅ NUEVO: Campo para distinguir eventos del rival
@Column(name = "es_evento_rival")
private Boolean esEventoRival = false;
```

**Ventajas:**
- ✓ jugador_id siempre tiene valor (integridad garantizada)
- ✓ Eventos del rival marcados explícitamente con esEventoRival=true
- ✓ Eventos del equipo usan el jugador real
- ✓ Estadísticas precisas y confiables

### 2. DTO Actualizado (EventoJugadorDTO.java)

```java
private Long jugadorId;
private Boolean esEventoRival; // ✅ NUEVO campo
```

### 3. Controlador Actualizado (EventoJugadorControladorV2.java)

```java
// ✅ Determinar si es evento del rival
boolean esEventoRival = "GOL_RIVAL".equalsIgnoreCase(dto.tipoEvento) || 
                        Boolean.TRUE.equals(dto.esEventoRival);

if (esEventoRival) {
    // Para eventos del rival, usar jugador de referencia
    evento.setJugador(jugadoresEquipo.get(0));
    evento.setEsEventoRival(true); // ✅ Marca el evento como del rival
} else {
    // Evento del equipo - jugadorId es OBLIGATORIO
    if (dto.jugadorId == null) {
        throw new IllegalArgumentException("jugadorId es obligatorio");
    }
    evento.setJugador(jugador);
    evento.setEsEventoRival(false);
}
```

### 4. Migración SQL (V1_2__add_es_evento_rival_campo.sql)

```sql
-- 1. Agregar campo es_evento_rival
ALTER TABLE eventos_jugador 
ADD COLUMN es_evento_rival BOOLEAN DEFAULT false;

-- 2. Marcar eventos existentes del rival
UPDATE eventos_jugador 
SET es_evento_rival = true 
WHERE tipo_evento = 'GOL_RIVAL';

-- 3. Limpiar eventos huérfanos (asignar jugador de referencia)
UPDATE eventos_jugador e
SET jugador_id = (
    SELECT j.id FROM jugadores j 
    WHERE j.equipo_id = (SELECT p.equipo_id FROM partidos p WHERE p.id = e.partido_id)
    LIMIT 1
),
es_evento_rival = true
WHERE jugador_id IS NULL;

-- 4. Hacer jugador_id NOT NULL
ALTER TABLE eventos_jugador 
MODIFY COLUMN jugador_id BIGINT NOT NULL;
```

## 📊 Antes vs Después

### Antes (❌ Problemático)
```
eventos_jugador
├── jugador_id (NULLABLE) → NULL permitido
├── tipo_evento
└── minuto

Problema: eventos con jugador_id NULL rompen estadísticas
```

### Después (✅ Correcto)
```
eventos_jugador
├── jugador_id (NOT NULL) → Siempre tiene valor
├── tipo_evento
├── minuto
└── es_evento_rival (BOOLEAN) → Distingue eventos del rival

Ventaja: Integridad garantizada + eventos del rival identificados
```

## 🎯 Uso en Estadísticas

### Consulta de eventos del equipo propio
```java
List<EventoJugador> eventosEquipo = eventoRepository
    .findByPartidoIdAndEsEventoRival(partidoId, false);
// Todos tienen jugador real asociado
```

### Consulta de eventos del rival
```java
List<EventoJugador> eventosRival = eventoRepository
    .findByPartidoIdAndEsEventoRival(partidoId, true);
// jugador es solo referencia, el evento es del rival
```

## ⚠️ Pasos de Migración

1. **Backup de la base de datos**
   ```bash
   mysqldump -u root -p gestion_jugadores > backup_antes_migracion.sql
   ```

2. **Ejecutar migración SQL**
   ```bash
   mysql -u root -p gestion_jugadores < V1_2__add_es_evento_rival_campo.sql
   ```

3. **Verificar migración**
   ```sql
   SELECT COUNT(*) FROM eventos_jugador WHERE jugador_id IS NULL; -- Debe ser 0
   SELECT COUNT(*) FROM eventos_jugador WHERE es_evento_rival = true;
   ```

4. **Desplegar nuevo código**
   ```bash
   mvn clean install
   java -jar target/gestion-jugadores-futbolBase.jar
   ```

5. **Actualizar frontend móvil**
   - Incluir campo `esEventoRival` en requests de creación de eventos
   - Para eventos del rival: `esEventoRival: true`
   - Para eventos del equipo: `esEventoRival: false` (o no enviar)

## 📱 Cambios en Frontend Móvil

### Registrar evento del equipo
```typescript
eventoService.registrarEvento({
  jugadorId: 123,  // ✅ OBLIGATORIO
  partidoId: 456,
  tipoEvento: 'GOL',
  minuto: 23,
  esEventoRival: false  // Evento del equipo
});
```

### Registrar evento del rival
```typescript
eventoService.registrarEvento({
  jugadorId: null,  // No se envía
  partidoId: 456,
  tipoEvento: 'GOL_RIVAL',  // O cualquier tipo
  minuto: 67,
  esEventoRival: true  // ✅ Marca como evento del rival
});
```

## ✅ Validaciones Implementadas

1. **En Controlador:**
   ```java
   if (!esEventoRival && dto.jugadorId == null) {
       throw new IllegalArgumentException("jugadorId obligatorio para eventos del equipo");
   }
   ```

2. **En Base de Datos:**
   ```sql
   jugador_id BIGINT NOT NULL
   ```

3. **En Lógica:**
   - Eventos del rival: usa jugador de referencia + esEventoRival=true
   - Eventos del equipo: usa jugador real + esEventoRival=false

## 🎉 Beneficios

✓ **Integridad de datos garantizada**  
✓ **Estadísticas precisas y confiables**  
✓ **Consultas más simples y eficientes**  
✓ **Eventos del rival claramente identificados**  
✓ **Sin eventos huérfanos**  
✓ **Código más mantenible y robusto**

## 📝 Notas Importantes

- El jugador asignado a eventos del rival es solo referencia técnica
- El campo `esEventoRival` es la fuente de verdad para identificar eventos del rival
- Todas las consultas de estadísticas deben filtrar por `esEventoRival=false`
- La migración SQL maneja automáticamente datos existentes
