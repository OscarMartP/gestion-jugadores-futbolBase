package com.gestion.jugadores.servicios.impl;

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
    
 // EventoJugadorServiceImpl.java
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

}
