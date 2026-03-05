package com.gestion.jugadores.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.gestion.jugadores.dto.JugadorDTO;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.modelo.Equipo;

/**
 * MapStruct mapper for Jugador entity <-> JugadorDTO
 * Handles conversion between Jugador entity and JugadorDTO
 */
@Mapper(componentModel = "spring")
public interface JugadorMapper {

    @Mapping(source = "equipo.id", target = "equipoId")
    @Mapping(target = "equipo", ignore = true)
    JugadorDTO toDto(Jugador jugador);

    @Mapping(source = "equipoId", target = "equipo", qualifiedByName = "equipoIdToEquipo")
    Jugador toEntity(JugadorDTO jugadorDTO);

    @Named("equipoIdToEquipo")
    default Equipo equipoIdToEquipo(Long equipoId) {
        if (equipoId == null) {
            return null;
        }
        Equipo equipo = new Equipo();
        equipo.setId(equipoId);
        return equipo;
    }

}
