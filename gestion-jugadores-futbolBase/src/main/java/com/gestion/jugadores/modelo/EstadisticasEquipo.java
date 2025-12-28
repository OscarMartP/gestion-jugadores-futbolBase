package com.gestion.jugadores.modelo;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para almacenar estadísticas agregadas de equipos por temporada
 * Facilita análisis de rendimiento global del equipo
 */
@Entity
@Table(name = "estadisticas_equipos", indexes = {
    @Index(name = "idx_equipo_temporada", columnList = "equipo_id, temporada")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"equipo_id", "temporada"})
})
public class EstadisticasEquipo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;
    
    @Column(name = "temporada", nullable = false, length = 20)
    private String temporada;
    
    // Partidos
    @Column(name = "partidos_jugados")
    private Integer partidosJugados = 0;
    
    @Column(name = "partidos_ganados")
    private Integer partidosGanados = 0;
    
    @Column(name = "partidos_empatados")
    private Integer partidosEmpatados = 0;
    
    @Column(name = "partidos_perdidos")
    private Integer partidosPerdidos = 0;
    
    // Goles
    @Column(name = "goles_favor")
    private Integer golesFavor = 0;
    
    @Column(name = "goles_contra")
    private Integer golesContra = 0;
    
    @Column(name = "diferencia_goles")
    private Integer diferenciaGoles = 0; // golesFavor - golesContra
    
    // Puntos (3 por victoria, 1 por empate)
    @Column(name = "puntos")
    private Integer puntos = 0;
    
    // Tarjetas
    @Column(name = "tarjetas_amarillas")
    private Integer tarjetasAmarillas = 0;
    
    @Column(name = "tarjetas_rojas")
    private Integer tarjetasRojas = 0;
    
    // Métricas calculadas
    @Column(name = "promedio_goles_favor")
    private Double promedioGolesFavor = 0.0;
    
    @Column(name = "promedio_goles_contra")
    private Double promedioGolesContra = 0.0;
    
    @Column(name = "efectividad") // % victorias
    private Double efectividad = 0.0;
    
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
    
    // Constructores
    public EstadisticasEquipo() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    public EstadisticasEquipo(Equipo equipo, String temporada) {
        this.equipo = equipo;
        this.temporada = temporada;
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
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

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
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

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
    
    /**
     * Calcula y actualiza las métricas derivadas
     */
    public void calcularMetricas() {
        // Diferencia de goles
        this.diferenciaGoles = this.golesFavor - this.golesContra;
        
        // Puntos: 3 por victoria, 1 por empate
        this.puntos = (this.partidosGanados * 3) + this.partidosEmpatados;
        
        if (partidosJugados > 0) {
            // Promedios
            this.promedioGolesFavor = (double) golesFavor / partidosJugados;
            this.promedioGolesContra = (double) golesContra / partidosJugados;
            
            // Efectividad: % de victorias
            this.efectividad = ((double) partidosGanados / partidosJugados) * 100;
        } else {
            this.promedioGolesFavor = 0.0;
            this.promedioGolesContra = 0.0;
            this.efectividad = 0.0;
        }
        
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
