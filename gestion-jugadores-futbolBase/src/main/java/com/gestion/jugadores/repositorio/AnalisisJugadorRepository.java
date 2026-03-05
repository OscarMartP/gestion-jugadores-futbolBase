package com.gestion.jugadores.repositorio;

import com.gestion.jugadores.modelo.AnalisisJugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de análisis de jugadores generados por IA.
 */
@Repository
public interface AnalisisJugadorRepository extends JpaRepository<AnalisisJugador, Long> {
    
    /**
     * Obtiene todos los análisis de un jugador ordenados por fecha descendente.
     */
    @Query("SELECT a FROM AnalisisJugador a WHERE a.jugador.id = :jugadorId ORDER BY a.fechaGeneracion DESC")
    List<AnalisisJugador> findByJugadorIdOrderByFechaGeneracionDesc(@Param("jugadorId") Long jugadorId);
    
    /**
     * Obtiene el análisis más reciente de un jugador para una temporada específica.
     */
    @Query("SELECT a FROM AnalisisJugador a WHERE a.jugador.id = :jugadorId AND a.temporada = :temporada " +
           "ORDER BY a.fechaGeneracion DESC")
    Optional<AnalisisJugador> findLatestByJugadorIdAndTemporada(
            @Param("jugadorId") Long jugadorId, 
            @Param("temporada") String temporada);
    
    /**
     * Verifica si existe algún análisis para un jugador en una temporada.
     */
    boolean existsByJugadorIdAndTemporada(Long jugadorId, String temporada);
    
    /**
     * Cuenta el número total de análisis generados para un jugador.
     */
    long countByJugadorId(Long jugadorId);
}
