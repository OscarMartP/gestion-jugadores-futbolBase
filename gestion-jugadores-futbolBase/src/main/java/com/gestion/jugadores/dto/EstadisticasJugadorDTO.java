package com.gestion.jugadores.dto;

/**
 * DTO para transferir estadísticas de jugadores
 */
public class EstadisticasJugadorDTO {
    
    private Long id;
    private Long jugadorId;
    private String jugadorNombre;
    private String jugadorApellido;
    private String posicion;
    private String temporada;
    
    private Integer totalGoles;
    private Integer golesEnCasa;
    private Integer golesFuera;
    private Integer totalAsistencias;
    private Integer tarjetasAmarillas;
    private Integer tarjetasRojas;
    private Integer paradas; // Específico para porteros
    private Integer partidosJugados;
    private Integer partidosTitular;
    private Integer minutosJugados;
    
    private Double promedioGoles;
    private Double promedioAsistencias;
    private Double rating;
    
    // Constructores
    public EstadisticasJugadorDTO() {}
    
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

    public String getJugadorNombre() {
        return jugadorNombre;
    }

    public void setJugadorNombre(String jugadorNombre) {
        this.jugadorNombre = jugadorNombre;
    }

    public String getJugadorApellido() {
        return jugadorApellido;
    }

    public void setJugadorApellido(String jugadorApellido) {
        this.jugadorApellido = jugadorApellido;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public Integer getTotalGoles() {
        return totalGoles;
    }

    public void setTotalGoles(Integer totalGoles) {
        this.totalGoles = totalGoles;
    }

    public Integer getGolesEnCasa() {
        return golesEnCasa;
    }

    public void setGolesEnCasa(Integer golesEnCasa) {
        this.golesEnCasa = golesEnCasa;
    }

    public Integer getGolesFuera() {
        return golesFuera;
    }

    public void setGolesFuera(Integer golesFuera) {
        this.golesFuera = golesFuera;
    }

    public Integer getTotalAsistencias() {
        return totalAsistencias;
    }

    public void setTotalAsistencias(Integer totalAsistencias) {
        this.totalAsistencias = totalAsistencias;
    }

    public Integer getTarjetasAmarillas() {
        return tarjetasAmarillas;
    }

    public void setTarjetasAmarillas(Integer tarjetasAmarillas) {
        this.tarjetasAmarillas = tarjetasAmarillas;
    }

    public Integer getTarjetasRojas() {
        return tarjetasRojas;
    }

    public void setTarjetasRojas(Integer tarjetasRojas) {
        this.tarjetasRojas = tarjetasRojas;
    }

    public Integer getParadas() {
        return paradas;
    }

    public void setParadas(Integer paradas) {
        this.paradas = paradas;
    }

    public Integer getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(Integer partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    public Integer getPartidosTitular() {
        return partidosTitular;
    }

    public void setPartidosTitular(Integer partidosTitular) {
        this.partidosTitular = partidosTitular;
    }

    public Integer getMinutosJugados() {
        return minutosJugados;
    }

    public void setMinutosJugados(Integer minutosJugados) {
        this.minutosJugados = minutosJugados;
    }

    public Double getPromedioGoles() {
        return promedioGoles;
    }

    public void setPromedioGoles(Double promedioGoles) {
        this.promedioGoles = promedioGoles;
    }

    public Double getPromedioAsistencias() {
        return promedioAsistencias;
    }

    public void setPromedioAsistencias(Double promedioAsistencias) {
        this.promedioAsistencias = promedioAsistencias;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
