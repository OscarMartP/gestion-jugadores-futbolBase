package com.gestion.jugadores.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.gestion.jugadores.dto.EquipoDTO;
import com.gestion.jugadores.modelo.Equipo;

/**
 * MapStruct mapper for Equipo entity <-> EquipoDTO
 * Handles conversion between Equipo entity and EquipoDTO
 */
@Mapper(componentModel = "spring")
public interface EquipoMapper {

    @Mapping(target = "jugadores", ignore = true)
    EquipoDTO toDto(Equipo equipo);

    @Mapping(target = "jugadores", ignore = true)
    Equipo toEntity(EquipoDTO equipoDTO);

}
