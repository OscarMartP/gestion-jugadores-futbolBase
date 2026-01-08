package com.gestion.jugadores.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.jugadores.modelo.EventoJugador;

public interface EventoJugadorRepository extends JpaRepository<EventoJugador, Long> {
    List<EventoJugador> findByJugador_Id(Long jugadorId);
    List<EventoJugador> findByPartido_Id(Long partidoId);
    List<EventoJugador> findByPartido_IdAndTipoEvento(Long partidoId, String tipoEvento);
    List<EventoJugador> findByJugador_IdAndPartido_Id(Long jugadorId, Long partidoId);
}

