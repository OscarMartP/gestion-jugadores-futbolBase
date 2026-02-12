package com.gestion.jugadores.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestion.jugadores.modelo.Jugador;

@Repository
public interface JugadorRepositorio extends JpaRepository<Jugador, Long> {
    
	List<Jugador> findByEquipo_Id(Long equipoId);

	// Obtener todos los jugadores pertenecientes a equipos de un usuario
	// Se hace mediante la relación: Jugador -> Equipo -> Usuario
	@Query("SELECT j FROM Jugador j WHERE j.equipo.usuario.id = :usuarioId")
	List<Jugador> findByUsuarioId(@Param("usuarioId") Long usuarioId);

}
