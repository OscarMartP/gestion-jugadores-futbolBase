package com.gestion.jugadores.mapper;

import org.mapstruct.Mapper;
import com.gestion.jugadores.dto.UsuarioDTO;
import com.gestion.jugadores.modelo.Usuario;

/**
 * MapStruct mapper for Usuario entity <-> UsuarioDTO
 * Handles conversion between Usuario entity and UsuarioDTO
 * Excludes sensitive data like passwords
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioDTO toDto(Usuario usuario);

    Usuario toEntity(UsuarioDTO usuarioDTO);

}
