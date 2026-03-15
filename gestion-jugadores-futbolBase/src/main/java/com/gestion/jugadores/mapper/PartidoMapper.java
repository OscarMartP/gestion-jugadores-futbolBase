package com.gestion.jugadores.mapper;

import org.mapstruct.Mapper;
import com.gestion.jugadores.dto.PartidoDTO;
import com.gestion.jugadores.modelo.Partido;

/**
 * MapStruct mapper for Partido entity <-> PartidoDTO
 * Handles conversion between Partido entity and PartidoDTO
 */
@Mapper(componentModel = "spring")
public interface PartidoMapper {

    // When mapping Partido -> PartidoDTO, avoid mapping Equipo.jugadores to prevent recursion
    @org.mapstruct.Mapping(target = "equipo.jugadores", ignore = true)
    @org.mapstruct.Mapping(target = "eventos", ignore = true)
    PartidoDTO toDto(Partido partido);

    @org.mapstruct.Mapping(target = "partidoActivo", defaultValue = "false")
    Partido toEntity(PartidoDTO partidoDTO);

}
