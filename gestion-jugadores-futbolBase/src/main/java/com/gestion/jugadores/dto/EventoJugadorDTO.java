package com.gestion.jugadores.dto;

/**
 * DTO for EventoJugador entity
 * Used to transfer evento jugador data between frontend and backend
 */
public class EventoJugadorDTO {

    private Long id;
    private Long jugadorId;
    private Long partidoId;
    private String tipoEvento;
    private Integer minuto;
    private Boolean fueTitular;
    private Integer minutosJugados;

    public EventoJugadorDTO() {}

    public EventoJugadorDTO(Long id, Long jugadorId, Long partidoId, String tipoEvento, Integer minuto) {
        this.id = id;
        this.jugadorId = jugadorId;
        this.partidoId = partidoId;
        this.tipoEvento = tipoEvento;
        this.minuto = minuto;
    }

    // Getters and Setters

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

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Integer getMinuto() {
        return minuto;
    }

    public void setMinuto(Integer minuto) {
        this.minuto = minuto;
    }

    public Boolean getFueTitular() {
        return fueTitular;
    }

    public void setFueTitular(Boolean fueTitular) {
        this.fueTitular = fueTitular;
    }

    public Integer getMinutosJugados() {
        return minutosJugados;
    }

    public void setMinutosJugados(Integer minutosJugados) {
        this.minutosJugados = minutosJugados;
    }

    @Override
    public String toString() {
        return "EventoJugadorDTO{" +
                "id=" + id +
                ", jugadorId=" + jugadorId +
                ", partidoId=" + partidoId +
                ", tipoEvento='" + tipoEvento + '\'' +
                ", minuto=" + minuto +
                ", fueTitular=" + fueTitular +
                ", minutosJugados=" + minutosJugados +
                '}';
    }
}
