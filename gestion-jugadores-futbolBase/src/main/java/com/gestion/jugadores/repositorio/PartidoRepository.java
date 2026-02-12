package com.gestion.jugadores.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gestion.jugadores.modelo.Partido;

public interface PartidoRepository extends JpaRepository<Partido, Long> {

    List<Partido> findByEquipo_Id(Long equipoId);

    // Buscar partidos activos por equipo
    List<Partido> findByEquipoIdAndPartidoActivo(Long equipoId, Boolean partidoActivo);
    
    // Buscar todos los partidos activos
    List<Partido> findByPartidoActivo(Boolean partidoActivo);

    // Desactivar en bloque otros partidos activos del equipo (excluir el partido indicado)
    @Modifying
    @Query("UPDATE Partido p SET p.partidoActivo = false WHERE p.equipo.id = :equipoId AND p.partidoActivo = true AND p.id <> :excludeId")
    int deactivateOtherActiveByEquipoId(@Param("equipoId") Long equipoId, @Param("excludeId") Long excludeId);

}

