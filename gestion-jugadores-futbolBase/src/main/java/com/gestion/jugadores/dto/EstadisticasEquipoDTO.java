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
    
    // Estadísticas de tiros a puerta
    private Integer totalTirosAPuerta;
    private Integer tirosAPuerta0_15;
    private Integer tirosAPuerta16_30;
    private Integer tirosAPuerta31_45;
    private Integer tirosAPuerta46_60;
    private Integer tirosAPuerta61_75;
    private Integer tirosAPuerta76_90;
    private Integer tirosAPuertaGanando;
    private Integer tirosAPuertaEmpatando;
    private Integer tirosAPuertaPerdiendo;
    private Double tirosAPuertaP90;
    private String mayorTirador;
    private Integer totalTirosRecibidos;
    private Integer tirosRecibidos0_15;
    private Integer tirosRecibidos16_30;
    private Integer tirosRecibidos31_45;
    private Integer tirosRecibidos46_60;
    private Integer tirosRecibidos61_75;
    private Integer tirosRecibidos76_90;
    private Integer totalRobos;
    private Integer robos0_15;
    private Integer robos16_30;
    private Integer robos31_45;
    private Integer robos46_60;
    private Integer robos61_75;
    private Integer robos76_90;
    private Integer robosGanando;
    private Integer robosEmpatando;
    private Integer robosPerdiendo;
    private Double robosP90;
    private String mayorRecuperador;
    
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

    // Getters y Setters para tiros a puerta
    public Integer getTotalTirosAPuerta() {
        return totalTirosAPuerta;
    }

    public void setTotalTirosAPuerta(Integer totalTirosAPuerta) {
        this.totalTirosAPuerta = totalTirosAPuerta;
    }

    public Integer getTirosAPuerta0_15() {
        return tirosAPuerta0_15;
    }

    public void setTirosAPuerta0_15(Integer tirosAPuerta0_15) {
        this.tirosAPuerta0_15 = tirosAPuerta0_15;
    }

    public Integer getTirosAPuerta16_30() {
        return tirosAPuerta16_30;
    }

    public void setTirosAPuerta16_30(Integer tirosAPuerta16_30) {
        this.tirosAPuerta16_30 = tirosAPuerta16_30;
    }

    public Integer getTirosAPuerta31_45() {
        return tirosAPuerta31_45;
    }

    public void setTirosAPuerta31_45(Integer tirosAPuerta31_45) {
        this.tirosAPuerta31_45 = tirosAPuerta31_45;
    }

    public Integer getTirosAPuerta46_60() {
        return tirosAPuerta46_60;
    }

    public void setTirosAPuerta46_60(Integer tirosAPuerta46_60) {
        this.tirosAPuerta46_60 = tirosAPuerta46_60;
    }

    public Integer getTirosAPuerta61_75() {
        return tirosAPuerta61_75;
    }

    public void setTirosAPuerta61_75(Integer tirosAPuerta61_75) {
        this.tirosAPuerta61_75 = tirosAPuerta61_75;
    }

    public Integer getTirosAPuerta76_90() {
        return tirosAPuerta76_90;
    }

    public void setTirosAPuerta76_90(Integer tirosAPuerta76_90) {
        this.tirosAPuerta76_90 = tirosAPuerta76_90;
    }

    public Integer getTirosAPuertaGanando() {
        return tirosAPuertaGanando;
    }

    public void setTirosAPuertaGanando(Integer tirosAPuertaGanando) {
        this.tirosAPuertaGanando = tirosAPuertaGanando;
    }

    public Integer getTirosAPuertaEmpatando() {
        return tirosAPuertaEmpatando;
    }

    public void setTirosAPuertaEmpatando(Integer tirosAPuertaEmpatando) {
        this.tirosAPuertaEmpatando = tirosAPuertaEmpatando;
    }

    public Integer getTirosAPuertaPerdiendo() {
        return tirosAPuertaPerdiendo;
    }

    public void setTirosAPuertaPerdiendo(Integer tirosAPuertaPerdiendo) {
        this.tirosAPuertaPerdiendo = tirosAPuertaPerdiendo;
    }

    public Double getTirosAPuertaP90() {
        return tirosAPuertaP90;
    }

    public void setTirosAPuertaP90(Double tirosAPuertaP90) {
        this.tirosAPuertaP90 = tirosAPuertaP90;
    }

    public String getMayorTirador() {
        return mayorTirador;
    }

    public void setMayorTirador(String mayorTirador) {
        this.mayorTirador = mayorTirador;
    }

    public Integer getTotalTirosRecibidos() {
        return totalTirosRecibidos;
    }

    public void setTotalTirosRecibidos(Integer totalTirosRecibidos) {
        this.totalTirosRecibidos = totalTirosRecibidos;
    }

    public Integer getTirosRecibidos0_15() {
        return tirosRecibidos0_15;
    }

    public void setTirosRecibidos0_15(Integer tirosRecibidos0_15) {
        this.tirosRecibidos0_15 = tirosRecibidos0_15;
    }

    public Integer getTirosRecibidos16_30() {
        return tirosRecibidos16_30;
    }

    public void setTirosRecibidos16_30(Integer tirosRecibidos16_30) {
        this.tirosRecibidos16_30 = tirosRecibidos16_30;
    }

    public Integer getTirosRecibidos31_45() {
        return tirosRecibidos31_45;
    }

    public void setTirosRecibidos31_45(Integer tirosRecibidos31_45) {
        this.tirosRecibidos31_45 = tirosRecibidos31_45;
    }

    public Integer getTirosRecibidos46_60() {
        return tirosRecibidos46_60;
    }

    public void setTirosRecibidos46_60(Integer tirosRecibidos46_60) {
        this.tirosRecibidos46_60 = tirosRecibidos46_60;
    }

    public Integer getTirosRecibidos61_75() {
        return tirosRecibidos61_75;
    }

    public void setTirosRecibidos61_75(Integer tirosRecibidos61_75) {
        this.tirosRecibidos61_75 = tirosRecibidos61_75;
    }

    public Integer getTirosRecibidos76_90() {
        return tirosRecibidos76_90;
    }

    public void setTirosRecibidos76_90(Integer tirosRecibidos76_90) {
        this.tirosRecibidos76_90 = tirosRecibidos76_90;
    }

    public Integer getTotalRobos() {
        return totalRobos;
    }

    public void setTotalRobos(Integer totalRobos) {
        this.totalRobos = totalRobos;
    }

    public Integer getRobos0_15() {
        return robos0_15;
    }

    public void setRobos0_15(Integer robos0_15) {
        this.robos0_15 = robos0_15;
    }

    public Integer getRobos16_30() {
        return robos16_30;
    }

    public void setRobos16_30(Integer robos16_30) {
        this.robos16_30 = robos16_30;
    }

    public Integer getRobos31_45() {
        return robos31_45;
    }

    public void setRobos31_45(Integer robos31_45) {
        this.robos31_45 = robos31_45;
    }

    public Integer getRobos46_60() {
        return robos46_60;
    }

    public void setRobos46_60(Integer robos46_60) {
        this.robos46_60 = robos46_60;
    }

    public Integer getRobos61_75() {
        return robos61_75;
    }

    public void setRobos61_75(Integer robos61_75) {
        this.robos61_75 = robos61_75;
    }

    public Integer getRobos76_90() {
        return robos76_90;
    }

    public void setRobos76_90(Integer robos76_90) {
        this.robos76_90 = robos76_90;
    }

    public Integer getRobosGanando() {
        return robosGanando;
    }

    public void setRobosGanando(Integer robosGanando) {
        this.robosGanando = robosGanando;
    }

    public Integer getRobosEmpatando() {
        return robosEmpatando;
    }

    public void setRobosEmpatando(Integer robosEmpatando) {
        this.robosEmpatando = robosEmpatando;
    }

    public Integer getRobosPerdiendo() {
        return robosPerdiendo;
    }

    public void setRobosPerdiendo(Integer robosPerdiendo) {
        this.robosPerdiendo = robosPerdiendo;
    }

    public Double getRobosP90() {
        return robosP90;
    }

    public void setRobosP90(Double robosP90) {
        this.robosP90 = robosP90;
    }

    public String getMayorRecuperador() {
        return mayorRecuperador;
    }

    public void setMayorRecuperador(String mayorRecuperador) {
        this.mayorRecuperador = mayorRecuperador;
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
