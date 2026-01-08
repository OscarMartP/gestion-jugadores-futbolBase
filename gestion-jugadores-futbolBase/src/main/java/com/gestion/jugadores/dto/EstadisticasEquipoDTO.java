package com.gestion.jugadores.dto;

/**
 * DTO para transferir estadísticas de equipos
 */
public class EstadisticasEquipoDTO {
    
    private Long id;
    private Long equipoId;
    private String equipoNombre;
    private String temporada;
    
    private Integer partidosJugados;
    private Integer partidosGanados;
    private Integer partidosEmpatados;
    private Integer partidosPerdidos;
    private Integer puntos;
    
    private Integer golesFavor;
    private Integer golesContra;
    private Integer diferenciaGoles;
    
    private Integer tarjetasAmarillas;
    private Integer tarjetasRojas;
    
    // Estadísticas de pases clave del equipo
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
    private String mayorPasador;
    
    private Double promedioGolesFavor;
    private Double promedioGolesContra;
    private Double efectividad;
    
    // Constructores
    public EstadisticasEquipoDTO() {}
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Long equipoId) {
        this.equipoId = equipoId;
    }

    public String getEquipoNombre() {
        return equipoNombre;
    }

    public void setEquipoNombre(String equipoNombre) {
        this.equipoNombre = equipoNombre;
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }

    public Integer getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(Integer partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    public Integer getPartidosGanados() {
        return partidosGanados;
    }

    public void setPartidosGanados(Integer partidosGanados) {
        this.partidosGanados = partidosGanados;
    }

    public Integer getPartidosEmpatados() {
        return partidosEmpatados;
    }

    public void setPartidosEmpatados(Integer partidosEmpatados) {
        this.partidosEmpatados = partidosEmpatados;
    }

    public Integer getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public void setPartidosPerdidos(Integer partidosPerdidos) {
        this.partidosPerdidos = partidosPerdidos;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public Integer getGolesFavor() {
        return golesFavor;
    }

    public void setGolesFavor(Integer golesFavor) {
        this.golesFavor = golesFavor;
    }

    public Integer getGolesContra() {
        return golesContra;
    }

    public void setGolesContra(Integer golesContra) {
        this.golesContra = golesContra;
    }

    public Integer getDiferenciaGoles() {
        return diferenciaGoles;
    }

    public void setDiferenciaGoles(Integer diferenciaGoles) {
        this.diferenciaGoles = diferenciaGoles;
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

    public Double getPasesClaveP90() {
        return pasesClaveP90;
    }

    public void setPasesClaveP90(Double pasesClaveP90) {
        this.pasesClaveP90 = pasesClaveP90;
    }

    public String getMayorPasador() {
        return mayorPasador;
    }

    public void setMayorPasador(String mayorPasador) {
        this.mayorPasador = mayorPasador;
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

    public Double getPromedioGolesFavor() {
        return promedioGolesFavor;
    }

    public void setPromedioGolesFavor(Double promedioGolesFavor) {
        this.promedioGolesFavor = promedioGolesFavor;
    }

    public Double getPromedioGolesContra() {
        return promedioGolesContra;
    }

    public void setPromedioGolesContra(Double promedioGolesContra) {
        this.promedioGolesContra = promedioGolesContra;
    }

    public Double getEfectividad() {
        return efectividad;
    }

    public void setEfectividad(Double efectividad) {
        this.efectividad = efectividad;
    }
}
