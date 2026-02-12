package com.gestion.jugadores.ai.service;

import com.gestion.jugadores.ai.dto.AnalisisJugadorDTO;
import com.gestion.jugadores.ai.dto.InformeJugadorDTO;
import com.gestion.jugadores.ai.dto.InformePartidoDTO;

import java.util.List;

/**
 * Servicio para generar análisis técnicos con IA.
 */
public interface AiAnalysisService {
    
    /**
     * Genera un informe técnico completo de un jugador basado en sus estadísticas.
     * El informe se guarda en la base de datos.
     * 
     * @param jugadorId ID del jugador
     * @param temporada Temporada a analizar (ej: "2024/2025")
     * @return InformeJugadorDTO con análisis técnico generado
     */
    InformeJugadorDTO generarInformeJugador(Long jugadorId, String temporada);
    
    /**
     * Genera un informe técnico de un partido con resumen, puntos destacados y áreas de mejora.
     * 
     * @param partidoId ID del partido
     * @return InformePartidoDTO con análisis del partido
     */
    InformePartidoDTO generarInformePartido(Long partidoId);
    
    /**
     * Obtiene todos los análisis generados para un jugador.
     * 
     * @param jugadorId ID del jugador
     * @return Lista de análisis ordenados por fecha descendente
     */
    List<AnalisisJugadorDTO> obtenerAnalisisJugador(Long jugadorId);
    
    /**
     * Obtiene un análisis específico por su ID.
     * 
     * @param analisisId ID del análisis
     * @return AnalisisJugadorDTO con los datos del análisis
     */
    AnalisisJugadorDTO obtenerAnalisisPorId(Long analisisId);
}
