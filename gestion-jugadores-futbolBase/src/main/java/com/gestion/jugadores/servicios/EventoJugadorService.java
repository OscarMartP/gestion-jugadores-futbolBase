package com.gestion.jugadores.servicios;

import com.gestion.jugadores.modelo.EventoJugador;
import com.gestion.jugadores.modelo.EventoResumenDTO;

import java.util.List;

public interface EventoJugadorService {
	EventoJugador registrarEvento(EventoJugador evento);
    List<EventoJugador> obtenerEventosPorJugador(Long jugadorId);
    List<EventoJugador> obtenerEventosPorPartido(Long partidoId);
 // EventoJugadorService.java
    public List<EventoResumenDTO> resumenEventosPorJugador(Long jugadorId);

}
