package com.gestion.jugadores.repositorio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestion.jugadores.modelo.EventoJugador;

public interface EventoJugadorRepository extends JpaRepository<EventoJugador, Long> {
    List<EventoJugador> findByJugador_Id(Long jugadorId);
    List<EventoJugador> findByPartido_Id(Long partidoId);
    List<EventoJugador> findByPartido_IdAndTipoEvento(Long partidoId, String tipoEvento);
    List<EventoJugador> findByJugador_IdAndPartido_Id(Long jugadorId, Long partidoId);
    
    // Filtrar eventos de un jugador por rango de fechas (para temporada)
    @Query("SELECT e FROM EventoJugador e WHERE e.jugador.id = :jugadorId AND e.partido.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<EventoJugador> findByJugadorIdAndFechaPartidoBetween(
        @Param("jugadorId") Long jugadorId, 
        @Param("fechaInicio") LocalDateTime fechaInicio, 
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    // Filtrar eventos de un equipo por rango de fechas (para temporada) - SOLO eventos del equipo propio, no del rival
    @Query("SELECT e FROM EventoJugador e WHERE e.partido.equipo.id = :equipoId AND e.partido.fecha BETWEEN :fechaInicio AND :fechaFin AND (e.esEventoRival = false OR e.esEventoRival IS NULL)")
    List<EventoJugador> findByEquipoIdAndFechaPartidoBetween(
        @Param("equipoId") Long equipoId, 
        @Param("fechaInicio") LocalDateTime fechaInicio, 
        @Param("fechaFin") LocalDateTime fechaFin
    );
}

