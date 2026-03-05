package com.gestion.jugadores.mapper;

import org.mapstruct.Mapper;
import com.gestion.jugadores.dto.EventoJugadorDTO;
import com.gestion.jugadores.modelo.EventoJugador;

/**
 * MapStruct mapper for EventoJugador entity <-> EventoJugadorDTO
 * Handles conversion between EventoJugador entity and EventoJugadorDTO
 */
@Mapper(componentModel = "spring")
public interface EventoJugadorMapper {

    EventoJugadorDTO toDto(EventoJugador eventoJugador);

    EventoJugador toEntity(EventoJugadorDTO eventoJugadorDTO);

}
