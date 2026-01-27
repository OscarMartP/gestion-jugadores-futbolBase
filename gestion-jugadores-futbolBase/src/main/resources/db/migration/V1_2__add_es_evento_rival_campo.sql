-- ========================================
-- MIGRACIÓN: Agregar campo es_evento_rival
-- Fecha: 27 Enero 2026
-- Objetivo: Solucionar inconsistencia en EventoJugador
--           jugador_id debe ser NOT NULL para integridad
--           eventos del rival se marcan con es_evento_rival=true
-- ========================================

-- 1. Agregar campo es_evento_rival (NOT NULL con valor por defecto)
ALTER TABLE eventos_jugador 
ADD COLUMN es_evento_rival BOOLEAN NOT NULL DEFAULT false 
COMMENT 'Indica si el evento pertenece al equipo rival (true) o al equipo propio (false)';

-- 2. Hacer jugador_id NOT NULL
ALTER TABLE eventos_jugador 
MODIFY COLUMN jugador_id BIGINT NOT NULL;

-- 3. Crear índice para mejorar consultas de estadísticas
CREATE INDEX idx_es_evento_rival ON eventos_jugador(es_evento_rival);
