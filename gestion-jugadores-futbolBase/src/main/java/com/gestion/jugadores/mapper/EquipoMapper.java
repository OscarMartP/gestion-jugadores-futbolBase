package com.gestion.jugadores.mapper;

import org.mapstruct.Mapper;
import com.gestion.jugadores.dto.EquipoDTO;
import com.gestion.jugadores.modelo.Equipo;

/**
 * MapStruct mapper for Equipo entity <-> EquipoDTO
 * Handles conversion between Equipo entity and EquipoDTO
 */
@Mapper(componentModel = "spring")
public interface EquipoMapper {

    EquipoDTO toDto(Equipo equipo);

    Equipo toEntity(EquipoDTO equipoDTO);

}
