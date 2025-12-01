package com.gestion.jugadores.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.jugadores.modelo.Partido;

public interface PartidoRepository extends JpaRepository<Partido, Long> {
	List<Partido> findByEquipo_Id(Long equipoId);

}
