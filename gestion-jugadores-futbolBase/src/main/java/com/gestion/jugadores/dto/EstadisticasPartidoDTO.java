package com.gestion.jugadores.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para transferir estadísticas individuales de un partido
 */
public class EstadisticasPartidoDTO {
    
    // Información básica del partido
    private Long id;
    private Long equipoId;
    private String equipoNombre;
    private LocalDateTime fecha;
    private String titulo;
    private Integer duracion;
    
    // Resultado
    private String resultado; // "VICTORIA", "EMPATE", "DERROTA"
    private Integer golesEquipo;
    private Integer golesRival;
    
    // Eventos del partido por jugador
    private List<EventoJugadorResumen> eventosPorJugador;
    
    // Totales del partido
    private Integer totalGoles;
    private Integer totalAsistencias;
    private Integer totalPasesClave;
    private Integer totalTirosAPuerta;  // Incluye goles + tiros a puerta
    private Integer totalTarjetasAmarillas;
    private Integer totalTarjetasRojas;
    
    // Estadísticas de posesión y tiros
    private Integer tirosRecibidos; // goles del rival
    
    // Distribución temporal de eventos
    private DistribucionTemporal distribucionGoles;
    private DistribucionTemporal distribucionAsistencias;
    private DistribucionTemporal distribucionTarjetas;
    private DistribucionTemporal distribucionTirosRecibidos;
    
    // Inner class para resumen de eventos por jugador
    public static class EventoJugadorResumen {
        private Long jugadorId;
        private String jugadorNombre;
        private Integer goles;
        private Integer asistencias;
        private Integer pasesClave;
        private Integer tarjetasAmarillas;
        private Integer tarjetasRojas;
        private Integer robos;
        private Integer tirosAPuerta;
        
        // Constructors, getters, setters
        public EventoJugadorResumen() {}
        
        public Long getJugadorId() { return jugadorId; }
        public void setJugadorId(Long jugadorId) { this.jugadorId = jugadorId; }
        
        public String getJugadorNombre() { return jugadorNombre; }
        public void setJugadorNombre(String jugadorNombre) { this.jugadorNombre = jugadorNombre; }
        
        public Integer getGoles() { return goles; }
        public void setGoles(Integer goles) { this.goles = goles; }
        
        public Integer getAsistencias() { return asistencias; }
        public void setAsistencias(Integer asistencias) { this.asistencias = asistencias; }
        
        public Integer getPasesClave() { return pasesClave; }
        public void setPasesClave(Integer pasesClave) { this.pasesClave = pasesClave; }
        
        public Integer getTarjetasAmarillas() { return tarjetasAmarillas; }
        public void setTarjetasAmarillas(Integer tarjetasAmarillas) { this.tarjetasAmarillas = tarjetasAmarillas; }
        
        public Integer getTarjetasRojas() { return tarjetasRojas; }
        public void setTarjetasRojas(Integer tarjetasRojas) { this.tarjetasRojas = tarjetasRojas; }
        
        public Integer getRobos() { return robos; }
        public void setRobos(Integer robos) { this.robos = robos; }
        
        public Integer getTirosAPuerta() { return tirosAPuerta; }
        public void setTirosAPuerta(Integer tirosAPuerta) { this.tirosAPuerta = tirosAPuerta; }
    }
    
    // Inner class para distribución temporal
    public static class DistribucionTemporal {
        private Integer intervalo0_15;
        private Integer intervalo16_30;
        private Integer intervalo31_45;
        private Integer intervalo46_60;
        private Integer intervalo61_75;
        private Integer intervalo76_90;
        
        // Constructors, getters, setters
        public DistribucionTemporal() {}
        
        public Integer getIntervalo0_15() { return intervalo0_15; }
        public void setIntervalo0_15(Integer intervalo0_15) { this.intervalo0_15 = intervalo0_15; }
        
        public Integer getIntervalo16_30() { return intervalo16_30; }
        public void setIntervalo16_30(Integer intervalo16_30) { this.intervalo16_30 = intervalo16_30; }
        
        public Integer getIntervalo31_45() { return intervalo31_45; }
        public void setIntervalo31_45(Integer intervalo31_45) { this.intervalo31_45 = intervalo31_45; }
        
        public Integer getIntervalo46_60() { return intervalo46_60; }
        public void setIntervalo46_60(Integer intervalo46_60) { this.intervalo46_60 = intervalo46_60; }
        
        public Integer getIntervalo61_75() { return intervalo61_75; }
        public void setIntervalo61_75(Integer intervalo61_75) { this.intervalo61_75 = intervalo61_75; }
        
        public Integer getIntervalo76_90() { return intervalo76_90; }
        public void setIntervalo76_90(Integer intervalo76_90) { this.intervalo76_90 = intervalo76_90; }
    }
    
    // Constructors, getters, setters
    public EstadisticasPartidoDTO() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEquipoId() { return equipoId; }
    public void setEquipoId(Long equipoId) { this.equipoId = equipoId; }
    
    public String getEquipoNombre() { return equipoNombre; }
    public void setEquipoNombre(String equipoNombre) { this.equipoNombre = equipoNombre; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
    
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    
    public Integer getGolesEquipo() { return golesEquipo; }
    public void setGolesEquipo(Integer golesEquipo) { this.golesEquipo = golesEquipo; }
    
    public Integer getGolesRival() { return golesRival; }
    public void setGolesRival(Integer golesRival) { this.golesRival = golesRival; }
    
    public List<EventoJugadorResumen> getEventosPorJugador() { return eventosPorJugador; }
    public void setEventosPorJugador(List<EventoJugadorResumen> eventosPorJugador) { this.eventosPorJugador = eventosPorJugador; }
    
    public Integer getTotalGoles() { return totalGoles; }
    public void setTotalGoles(Integer totalGoles) { this.totalGoles = totalGoles; }
    
    public Integer getTotalAsistencias() { return totalAsistencias; }
    public void setTotalAsistencias(Integer totalAsistencias) { this.totalAsistencias = totalAsistencias; }
    
    public Integer getTotalPasesClave() { return totalPasesClave; }
    public void setTotalPasesClave(Integer totalPasesClave) { this.totalPasesClave = totalPasesClave; }
    
    public Integer getTotalTirosAPuerta() { return totalTirosAPuerta; }
    public void setTotalTirosAPuerta(Integer totalTirosAPuerta) { this.totalTirosAPuerta = totalTirosAPuerta; }
    
    public Integer getTotalTarjetasAmarillas() { return totalTarjetasAmarillas; }
    public void setTotalTarjetasAmarillas(Integer totalTarjetasAmarillas) { this.totalTarjetasAmarillas = totalTarjetasAmarillas; }
    
    public Integer getTotalTarjetasRojas() { return totalTarjetasRojas; }
    public void setTotalTarjetasRojas(Integer totalTarjetasRojas) { this.totalTarjetasRojas = totalTarjetasRojas; }
    
    public Integer getTirosRecibidos() { return tirosRecibidos; }
    public void setTirosRecibidos(Integer tirosRecibidos) { this.tirosRecibidos = tirosRecibidos; }
    
    public DistribucionTemporal getDistribucionGoles() { return distribucionGoles; }
    public void setDistribucionGoles(DistribucionTemporal distribucionGoles) { this.distribucionGoles = distribucionGoles; }
    
    public DistribucionTemporal getDistribucionAsistencias() { return distribucionAsistencias; }
    public void setDistribucionAsistencias(DistribucionTemporal distribucionAsistencias) { this.distribucionAsistencias = distribucionAsistencias; }
    
    public DistribucionTemporal getDistribucionTarjetas() { return distribucionTarjetas; }
    public void setDistribucionTarjetas(DistribucionTemporal distribucionTarjetas) { this.distribucionTarjetas = distribucionTarjetas; }
    
    public DistribucionTemporal getDistribucionTirosRecibidos() { return distribucionTirosRecibidos; }
    public void setDistribucionTirosRecibidos(DistribucionTemporal distribucionTirosRecibidos) { this.distribucionTirosRecibidos = distribucionTirosRecibidos; }
}
