package com.gestion.jugadores.servicios;

import com.gestion.jugadores.modelo.EventoJugador;
import com.gestion.jugadores.modelo.EventoResumenDTO;

import java.util.List;

public interface EventoJugadorService {
	EventoJugador registrarEvento(EventoJugador evento);
    List<EventoJugador> obtenerEventosPorJugador(Long jugadorId);
    List<EventoJugador> obtenerEventosPorPartido(Long partidoId);
    List<EventoResumenDTO> resumenEventosPorJugador(Long jugadorId);
    
    // Métodos CRUD adicionales para BaseController
    EventoJugador obtenerEventoPorId(Long id);
    List<EventoJugador> obtenerTodosLosEventos();
    EventoJugador actualizarEvento(Long id, EventoJugador evento);
    void eliminarEvento(Long id);
}
