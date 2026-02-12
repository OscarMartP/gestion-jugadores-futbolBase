package com.gestion.jugadores.repositorio;

import com.gestion.jugadores.modelo.EstadisticasEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticasEquipoRepository extends JpaRepository<EstadisticasEquipo, Long> {
    
    /**
     * Buscar estadísticas de un equipo en una temporada específica
     */
    Optional<EstadisticasEquipo> findByEquipo_IdAndTemporada(Long equipoId, String temporada);
    
    /**
     * Obtener todas las estadísticas de una temporada
     */
    List<EstadisticasEquipo> findByTemporada(String temporada);
    
    /**
     * Obtener todas las estadísticas de un equipo
     */
    List<EstadisticasEquipo> findByEquipo_Id(Long equipoId);
}
