package com.gestion.jugadores.ai.dto;

/**
 * DTO para el informe de análisis técnico de un jugador generado por IA.
 */
public class InformeJugadorDTO {
    
    private Long jugadorId;
    private String nombreCompleto;
    private String posicion;
    private String analisisTecnico;
    private String temporada;
    
    // Constructores
    public InformeJugadorDTO() {
    }
    
    public InformeJugadorDTO(Long jugadorId, String nombreCompleto, String posicion, 
                             String analisisTecnico, String temporada) {
        this.jugadorId = jugadorId;
        this.nombreCompleto = nombreCompleto;
        this.posicion = posicion;
        this.analisisTecnico = analisisTecnico;
        this.temporada = temporada;
    }
    
    // Getters y Setters
    public Long getJugadorId() {
        return jugadorId;
    }
    
    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getPosicion() {
        return posicion;
    }
    
    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }
    
    public String getAnalisisTecnico() {
        return analisisTecnico;
    }
    
    public void setAnalisisTecnico(String analisisTecnico) {
        this.analisisTecnico = analisisTecnico;
    }
    
    public String getTemporada() {
        return temporada;
    }
    
    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }
}
