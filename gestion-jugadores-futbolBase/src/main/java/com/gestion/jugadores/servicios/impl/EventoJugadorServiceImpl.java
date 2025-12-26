package com.gestion.jugadores.servicios.impl;

import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.modelo.EventoJugador;
import com.gestion.jugadores.modelo.EventoResumenDTO;
import com.gestion.jugadores.repositorio.EventoJugadorRepository;
import com.gestion.jugadores.servicios.EventoJugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoJugadorServiceImpl implements EventoJugadorService {

    @Autowired
    private EventoJugadorRepository eventoJugadorRepository;

    @Override
    public EventoJugador registrarEvento(EventoJugador evento) {
        return eventoJugadorRepository.save(evento);
    }

    @Override
    public List<EventoJugador> obtenerEventosPorJugador(Long jugadorId) {
        return eventoJugadorRepository.findByJugador_Id(jugadorId); // CAMBIO
    }

    @Override
    public List<EventoJugador> obtenerEventosPorPartido(Long partidoId) {
        return eventoJugadorRepository.findByPartido_Id(partidoId); // CAMBIO
    }
    
    @Override
    public List<EventoResumenDTO> resumenEventosPorJugador(Long jugadorId) {
        List<EventoJugador> eventos = eventoJugadorRepository.findByJugador_Id(jugadorId);
        return eventos.stream().map(e -> {
            EventoResumenDTO dto = new EventoResumenDTO();
            dto.setTipoEvento(e.getTipoEvento());
            dto.setMinuto(e.getMinuto());
            dto.setPartidoId(e.getPartido().getId());
            dto.setFechaPartido(e.getPartido().getFecha().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    // ========== MÉTODOS CRUD ADICIONALES ==========

    @Override
    public EventoJugador obtenerEventoPorId(Long id) {
        return eventoJugadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el evento con ID: " + id));
    }

    @Override
    public List<EventoJugador> obtenerTodosLosEventos() {
        return eventoJugadorRepository.findAll();
    }

    @Override
    public EventoJugador actualizarEvento(Long id, EventoJugador evento) {
        EventoJugador existente = obtenerEventoPorId(id);
        existente.setTipoEvento(evento.getTipoEvento());
        existente.setMinuto(evento.getMinuto());
        // Jugador y Partido no se actualizan para mantener integridad
        return eventoJugadorRepository.save(existente);
    }

    @Override
    public void eliminarEvento(Long id) {
        EventoJugador evento = obtenerEventoPorId(id);
        eventoJugadorRepository.delete(evento);
    }
}
