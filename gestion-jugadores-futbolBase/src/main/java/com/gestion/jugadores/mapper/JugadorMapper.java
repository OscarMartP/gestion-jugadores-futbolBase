package com.gestion.jugadores.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.gestion.jugadores.dto.JugadorDTO;
import com.gestion.jugadores.modelo.Jugador;

/**
 * MapStruct mapper for Jugador entity <-> JugadorDTO
 * Handles conversion between Jugador entity and JugadorDTO
 */
@Mapper(componentModel = "spring")
public interface JugadorMapper {

    @Mapping(source = "equipo.id", target = "equipoId")
    @Mapping(target = "equipo", ignore = true)
    JugadorDTO toDto(Jugador jugador);

    @Mapping(target = "equipo", ignore = true)
    Jugador toEntity(JugadorDTO jugadorDTO);

}
