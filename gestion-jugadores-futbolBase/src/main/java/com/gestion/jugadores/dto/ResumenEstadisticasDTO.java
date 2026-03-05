package com.gestion.jugadores.dto;

import java.util.List;

/**
 * DTO para resumen completo de estadísticas (equipo + jugadores)
 */
public class ResumenEstadisticasDTO {
    
    private EstadisticasEquipoDTO estadisticasEquipo;
    private List<EstadisticasJugadorDTO> topGoleadores;
    private List<EstadisticasJugadorDTO> topAsistentes;
    private List<EstadisticasJugadorDTO> menosTargetas;
    private Integer totalJugadores;
    
    // Constructores
    public ResumenEstadisticasDTO() {}
    
    public ResumenEstadisticasDTO(EstadisticasEquipoDTO estadisticasEquipo, 
                                  List<EstadisticasJugadorDTO> topGoleadores,
                                  List<EstadisticasJugadorDTO> topAsistentes) {
        this.estadisticasEquipo = estadisticasEquipo;
        this.topGoleadores = topGoleadores;
        this.topAsistentes = topAsistentes;
    }
    
    // Getters y Setters
    public EstadisticasEquipoDTO getEstadisticasEquipo() {
        return estadisticasEquipo;
    }

    public void setEstadisticasEquipo(EstadisticasEquipoDTO estadisticasEquipo) {
        this.estadisticasEquipo = estadisticasEquipo;
    }

    public List<EstadisticasJugadorDTO> getTopGoleadores() {
        return topGoleadores;
    }

    public void setTopGoleadores(List<EstadisticasJugadorDTO> topGoleadores) {
        this.topGoleadores = topGoleadores;
    }

    public List<EstadisticasJugadorDTO> getTopAsistentes() {
        return topAsistentes;
    }

    public void setTopAsistentes(List<EstadisticasJugadorDTO> topAsistentes) {
        this.topAsistentes = topAsistentes;
    }

    public List<EstadisticasJugadorDTO> getMenosTargetas() {
        return menosTargetas;
    }

    public void setMenosTargetas(List<EstadisticasJugadorDTO> menosTargetas) {
        this.menosTargetas = menosTargetas;
    }

    public Integer getTotalJugadores() {
        return totalJugadores;
    }

    public void setTotalJugadores(Integer totalJugadores) {
        this.totalJugadores = totalJugadores;
    }
}
