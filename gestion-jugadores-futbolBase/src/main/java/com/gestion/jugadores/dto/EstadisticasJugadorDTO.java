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
    
    // Estadísticas de pases clave
    private Integer totalPasesClave;
    private Integer pasesClave0_15;
    private Integer pasesClave16_30;
    private Integer pasesClave31_45;
    private Integer pasesClave46_60;
    private Integer pasesClave61_75;
    private Integer pasesClave76_90;
    private Integer pasesClaveGanando;
    private Integer pasesClaveEmpatando;
    private Integer pasesClavePerdiendo;
    private Double pasesClaveP90;
    
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
    
    // Getters y Setters para pases clave
    public Integer getTotalPasesClave() {
        return totalPasesClave;
    }

    public void setTotalPasesClave(Integer totalPasesClave) {
        this.totalPasesClave = totalPasesClave;
    }

    public Integer getPasesClave0_15() {
        return pasesClave0_15;
    }

    public void setPasesClave0_15(Integer pasesClave0_15) {
        this.pasesClave0_15 = pasesClave0_15;
    }

    public Integer getPasesClave16_30() {
        return pasesClave16_30;
    }

    public void setPasesClave16_30(Integer pasesClave16_30) {
        this.pasesClave16_30 = pasesClave16_30;
    }

    public Integer getPasesClave31_45() {
        return pasesClave31_45;
    }

    public void setPasesClave31_45(Integer pasesClave31_45) {
        this.pasesClave31_45 = pasesClave31_45;
    }

    public Integer getPasesClave46_60() {
        return pasesClave46_60;
    }

    public void setPasesClave46_60(Integer pasesClave46_60) {
        this.pasesClave46_60 = pasesClave46_60;
    }

    public Integer getPasesClave61_75() {
        return pasesClave61_75;
    }

    public void setPasesClave61_75(Integer pasesClave61_75) {
        this.pasesClave61_75 = pasesClave61_75;
    }

    public Integer getPasesClave76_90() {
        return pasesClave76_90;
    }

    public void setPasesClave76_90(Integer pasesClave76_90) {
        this.pasesClave76_90 = pasesClave76_90;
    }

    public Integer getPasesClaveGanando() {
        return pasesClaveGanando;
    }

    public void setPasesClaveGanando(Integer pasesClaveGanando) {
        this.pasesClaveGanando = pasesClaveGanando;
    }

    public Integer getPasesClaveEmpatando() {
        return pasesClaveEmpatando;
    }

    public void setPasesClaveEmpatando(Integer pasesClaveEmpatando) {
        this.pasesClaveEmpatando = pasesClaveEmpatando;
    }

    public Integer getPasesClavePerdiendo() {
        return pasesClavePerdiendo;
    }

    public void setPasesClavePerdiendo(Integer pasesClavePerdiendo) {
        this.pasesClavePerdiendo = pasesClavePerdiendo;
    }

    public Double getPasesClaveP90() {
        return pasesClaveP90;
    }

    public void setPasesClaveP90(Double pasesClaveP90) {
        this.pasesClaveP90 = pasesClaveP90;
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
