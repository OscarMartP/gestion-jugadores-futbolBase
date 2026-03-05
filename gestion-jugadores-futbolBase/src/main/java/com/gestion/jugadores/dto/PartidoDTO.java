package com.gestion.jugadores.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Partido entity
 * Used to transfer partido data between frontend and backend
 */
public class PartidoDTO {

    private Long id;
    private EquipoDTO equipo;
    private LocalDateTime fecha;
    private Boolean partidoActivo;
    private Integer duracion;
    private String titulo;
    private String resultado;
    private Integer golesEquipo;
    private Integer golesRival;
    private List<Long> titulares;
    private List<Long> suplentes;
    private List<EventoJugadorDTO> eventos;

    public PartidoDTO() {}

    public PartidoDTO(Long id, EquipoDTO equipo, LocalDateTime fecha, Boolean partidoActivo, Integer duracion) {
        this.id = id;
        this.equipo = equipo;
        this.fecha = fecha;
        this.partidoActivo = partidoActivo;
        this.duracion = duracion;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EquipoDTO getEquipo() {
        return equipo;
    }

    public void setEquipo(EquipoDTO equipo) {
        this.equipo = equipo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Boolean getPartidoActivo() {
        return partidoActivo;
    }

    public void setPartidoActivo(Boolean partidoActivo) {
        this.partidoActivo = partidoActivo;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<EventoJugadorDTO> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoJugadorDTO> eventos) {
        this.eventos = eventos;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Integer getGolesEquipo() {
        return golesEquipo;
    }

    public void setGolesEquipo(Integer golesEquipo) {
        this.golesEquipo = golesEquipo;
    }

    public Integer getGolesRival() {
        return golesRival;
    }

    public void setGolesRival(Integer golesRival) {
        this.golesRival = golesRival;
    }

    public List<Long> getTitulares() {
        return titulares;
    }

    public void setTitulares(List<Long> titulares) {
        this.titulares = titulares;
    }

    public List<Long> getSuplentes() {
        return suplentes;
    }

    public void setSuplentes(List<Long> suplentes) {
        this.suplentes = suplentes;
    }

    @Override
    public String toString() {
        return "PartidoDTO{" +
                "id=" + id +
                ", equipo=" + equipo +
                ", fecha=" + fecha +
                ", partidoActivo=" + partidoActivo +
                ", duracion=" + duracion +
                ", titulo='" + titulo + '\'' +
                ", resultado='" + resultado + '\'' +
                ", golesEquipo=" + golesEquipo +
                ", golesRival=" + golesRival +
                '}';
    }
}
