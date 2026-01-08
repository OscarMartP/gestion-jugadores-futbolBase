package com.gestion.jugadores.repositorio;

import com.gestion.jugadores.modelo.EstadisticasJugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticasJugadorRepository extends JpaRepository<EstadisticasJugador, Long> {
    
    /**
     * Buscar estadísticas de un jugador en una temporada específica
     */
    Optional<EstadisticasJugador> findByJugador_IdAndTemporada(Long jugadorId, String temporada);
    
    /**
     * Obtener estadísticas de todos los jugadores de un equipo en una temporada
     * Usando el camino de navegación jugador.equipo.id
     */
    List<EstadisticasJugador> findByJugador_Equipo_IdAndTemporada(Long equipoId, String temporada);
    
    /**
     * Obtener estadísticas de todos los jugadores de un equipo en una temporada
     */
    @Query("SELECT e FROM EstadisticasJugador e WHERE e.jugador.equipo.id = :equipoId AND e.temporada = :temporada ORDER BY e.totalGoles DESC")
    List<EstadisticasJugador> findByEquipoAndTemporada(@Param("equipoId") Long equipoId, @Param("temporada") String temporada);
    
    /**
     * Top goleadores de un equipo en una temporada
     */
    @Query("SELECT e FROM EstadisticasJugador e WHERE e.jugador.equipo.id = :equipoId AND e.temporada = :temporada ORDER BY e.totalGoles DESC")
    List<EstadisticasJugador> findTopGoleadoresByEquipo(@Param("equipoId") Long equipoId, @Param("temporada") String temporada);
    
    /**
     * Top asistentes de un equipo en una temporada
     */
    @Query("SELECT e FROM EstadisticasJugador e WHERE e.jugador.equipo.id = :equipoId AND e.temporada = :temporada ORDER BY e.totalAsistencias DESC")
    List<EstadisticasJugador> findTopAsistentesByEquipo(@Param("equipoId") Long equipoId, @Param("temporada") String temporada);
    
    /**
     * Jugadores con mejor rating
     */
    @Query("SELECT e FROM EstadisticasJugador e WHERE e.jugador.equipo.id = :equipoId AND e.temporada = :temporada ORDER BY e.rating DESC")
    List<EstadisticasJugador> findByMejorRating(@Param("equipoId") Long equipoId, @Param("temporada") String temporada);
    
    /**
     * Jugadores con menos tarjetas
     */
    @Query("SELECT e FROM EstadisticasJugador e WHERE e.jugador.equipo.id = :equipoId AND e.temporada = :temporada ORDER BY (e.tarjetasAmarillas + e.tarjetasRojas) ASC")
    List<EstadisticasJugador> findByMenosTargetas(@Param("equipoId") Long equipoId, @Param("temporada") String temporada);
    
    /**
     * Todas las estadísticas de una temporada
     */
    List<EstadisticasJugador> findByTemporada(String temporada);
}
