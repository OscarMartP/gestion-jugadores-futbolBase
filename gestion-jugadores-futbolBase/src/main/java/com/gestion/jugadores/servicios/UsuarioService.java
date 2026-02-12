package com.gestion.jugadores.servicios;

import java.util.Set;

import com.gestion.jugadores.modelo.Usuario;
import com.gestion.jugadores.modelo.UsuarioRol;

public interface UsuarioService {

	public Usuario guardarUsuario(Usuario usuario, Set<UsuarioRol> usuarioRoles) throws Exception;

	public Usuario obtenerUsuario(String username);

	public void eliminarUsuario(Long usuarioId);
}
