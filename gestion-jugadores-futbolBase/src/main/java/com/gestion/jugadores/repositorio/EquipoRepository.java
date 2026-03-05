package com.gestion.jugadores.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.jugadores.modelo.Equipo;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {
	
	List<Equipo> findByUsuarioId(Long usuarioId);
    
    boolean existsByNombreAndUsuarioId(String nombre, Long usuarioId);
    
    Optional<Equipo> findByIdAndUsuarioId(Long id, Long usuarioId);

}					