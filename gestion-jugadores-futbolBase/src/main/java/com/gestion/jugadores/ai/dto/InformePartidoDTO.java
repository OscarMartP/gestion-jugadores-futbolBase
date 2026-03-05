package com.gestion.jugadores.ai.dto;

import java.time.LocalDateTime;

/**
 * DTO para el informe de análisis de un partido generado por IA.
 */
public class InformePartidoDTO {
    
    private Long partidoId;
    private String titulo;
    private LocalDateTime fecha;
    private String resultado;
    private String resumenTactico;
    private String puntosDestacados;
    private String areasMejora;
    
    // Constructores
    public InformePartidoDTO() {
    }
    
    public InformePartidoDTO(Long partidoId, String titulo, LocalDateTime fecha, 
                             String resultado, String resumenTactico, 
                             String puntosDestacados, String areasMejora) {
        this.partidoId = partidoId;
        this.titulo = titulo;
        this.fecha = fecha;
        this.resultado = resultado;
        this.resumenTactico = resumenTactico;
        this.puntosDestacados = puntosDestacados;
        this.areasMejora = areasMejora;
    }
    
    // Getters y Setters
    public Long getPartidoId() {
        return partidoId;
    }
    
    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public String getResultado() {
        return resultado;
    }
    
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    public String getResumenTactico() {
        return resumenTactico;
    }
    
    public void setResumenTactico(String resumenTactico) {
        this.resumenTactico = resumenTactico;
    }
    
    public String getPuntosDestacados() {
        return puntosDestacados;
    }
    
    public void setPuntosDestacados(String puntosDestacados) {
        this.puntosDestacados = puntosDestacados;
    }
    
    public String getAreasMejora() {
        return areasMejora;
    }
    
    public void setAreasMejora(String areasMejora) {
        this.areasMejora = areasMejora;
    }
}
