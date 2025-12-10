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

    PartidoDTO toDto(Partido partido);

    Partido toEntity(PartidoDTO partidoDTO);

}
