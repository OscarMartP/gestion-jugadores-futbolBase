package com.gestion.jugadores.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.jugadores.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

	public Usuario findByUsername(String username);
}
