package com.gestion.jugadores.servicios;

import com.gestion.jugadores.dto.EstadisticasJugadorDTO;
import com.gestion.jugadores.dto.EstadisticasEquipoDTO;
import com.gestion.jugadores.dto.ResumenEstadisticasDTO;
import java.util.List;

/**
 * Servicio para gestión de estadísticas de jugadores y equipos
 */
public interface EstadisticasService {
    
    // ========== ESTADÍSTICAS DE JUGADORES ==========
    
    /**
     * Obtener estadísticas de un jugador en una temporada
     */
    EstadisticasJugadorDTO obtenerEstadisticasJugador(Long jugadorId, String temporada);
    
    /**
     * Obtener estadísticas de todos los jugadores de un equipo
     */
    List<EstadisticasJugadorDTO> obtenerEstadisticasJugadoresEquipo(Long equipoId, String temporada);
    
    /**
     * Top goleadores de un equipo
     */
    List<EstadisticasJugadorDTO> obtenerTopGoleadores(Long equipoId, String temporada, int limite);
    
    /**
     * Top asistentes de un equipo
     */
    List<EstadisticasJugadorDTO> obtenerTopAsistentes(Long equipoId, String temporada, int limite);
    
    /**
     * Jugadores con mejor rating
     */
    List<EstadisticasJugadorDTO> obtenerMejorRating(Long equipoId, String temporada, int limite);
    
    // ========== ESTADÍSTICAS DE EQUIPOS ==========
    
    /**
     * Obtener estadísticas de un equipo
     */
    EstadisticasEquipoDTO obtenerEstadisticasEquipo(Long equipoId, String temporada);
    
    /**
     * Obtener resumen completo de estadísticas (equipo + tops)
     */
    ResumenEstadisticasDTO obtenerResumenEquipo(Long equipoId, String temporada);
    
    // ========== ACTUALIZACIÓN DE ESTADÍSTICAS ==========
    
    /**
     * Recalcular y actualizar estadísticas de un jugador
     */
    void actualizarEstadisticasJugador(Long jugadorId, String temporada);
    
    /**
     * Recalcular y actualizar estadísticas de un equipo
     */
    void actualizarEstadisticasEquipo(Long equipoId, String temporada);
    
    /**
     * Actualizar todas las estadísticas de una temporada
     */
    void actualizarTodasLasEstadisticas(String temporada);
    
    /**
     * Actualizar estadísticas después de registrar un evento
     */
    void actualizarDespuesDeEvento(Long jugadorId, Long partidoId);
}
