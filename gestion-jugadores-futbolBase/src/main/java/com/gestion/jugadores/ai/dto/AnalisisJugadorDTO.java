package com.gestion.jugadores.ai.dto;

import java.time.LocalDateTime;

/**
 * DTO para representar análisis almacenados de un jugador con metadata.
 */
public class AnalisisJugadorDTO {
    
    private Long id;
    private Long jugadorId;
    private String nombreCompleto;
    private String posicion;
    private String analisisTecnico;
    private String temporada;
    private LocalDateTime fechaGeneracion;
    private Integer tokensUsados;
    private String modeloIa;
    
    // Constructores
    public AnalisisJugadorDTO() {
    }
    
    public AnalisisJugadorDTO(Long id, Long jugadorId, String nombreCompleto, String posicion,
                              String analisisTecnico, String temporada, LocalDateTime fechaGeneracion,
                              Integer tokensUsados, String modeloIa) {
        this.id = id;
        this.jugadorId = jugadorId;
        this.nombreCompleto = nombreCompleto;
        this.posicion = posicion;
        this.analisisTecnico = analisisTecnico;
        this.temporada = temporada;
        this.fechaGeneracion = fechaGeneracion;
        this.tokensUsados = tokensUsados;
        this.modeloIa = modeloIa;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
    
    public Integer getTokensUsados() {
        return tokensUsados;
    }
    
    public void setTokensUsados(Integer tokensUsados) {
        this.tokensUsados = tokensUsados;
    }
    
    public String getModeloIa() {
        return modeloIa;
    }
    
    public void setModeloIa(String modeloIa) {
        this.modeloIa = modeloIa;
    }
}
