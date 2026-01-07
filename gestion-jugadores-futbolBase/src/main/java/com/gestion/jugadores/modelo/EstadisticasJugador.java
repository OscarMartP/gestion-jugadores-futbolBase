package com.gestion.jugadores.modelo;

import javax.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

/**
 * Entidad para almacenar estadísticas agregadas de jugadores por temporada
 * Permite consultas rápidas sin recalcular desde eventos cada vez
 */
@Entity
@Table(name = "estadisticas_jugadores", indexes = {
    @Index(name = "idx_jugador_temporada", columnList = "jugador_id, temporada")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"jugador_id", "temporada"})
})
public class EstadisticasJugador {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Jugador jugador;
    
    @Column(name = "temporada", nullable = false, length = 20)
    private String temporada; // Ej: "2024-2025"
    
    // Estadísticas de goles
    @Column(name = "total_goles")
    private Integer totalGoles = 0;
    
    @Column(name = "goles_en_casa")
    private Integer golesEnCasa = 0;
    
    @Column(name = "goles_fuera")
    private Integer golesFuera = 0;
    
    // Estadísticas de asistencias
    @Column(name = "total_asistencias")
    private Integer totalAsistencias = 0;
    
    // Tarjetas
    @Column(name = "tarjetas_amarillas")
    private Integer tarjetasAmarillas = 0;
    
    @Column(name = "tarjetas_rojas")
    private Integer tarjetasRojas = 0;
    
    // Estadísticas específicas de porteros
    @Column(name = "paradas")
    private Integer paradas = 0;
    
    // Partidos
    @Column(name = "partidos_jugados")
    private Integer partidosJugados = 0;
    
    @Column(name = "partidos_titular")
    private Integer partidosTitular = 0;
    
    @Column(name = "minutos_jugados")
    private Integer minutosJugados = 0;
    
    // Métricas calculadas
    @Column(name = "promedio_goles")
    private Double promedioGoles = 0.0; // goles / partidos
    
    @Column(name = "promedio_asistencias")
    private Double promedioAsistencias = 0.0;
    
    @Column(name = "rating")
    private Double rating = 0.0; // Rating general del jugador (calculado)
    
    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
    
    // Constructores
    public EstadisticasJugador() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
    
    public EstadisticasJugador(Jugador jugador, String temporada) {
        this.jugador = jugador;
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

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
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

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }
    
    /**
     * Calcula y actualiza las métricas derivadas (promedios y rating)
     */
    public void calcularMetricas() {
        if (partidosJugados > 0) {
            this.promedioGoles = (double) totalGoles / partidosJugados;
            this.promedioAsistencias = (double) totalAsistencias / partidosJugados;
            
            // Rating básico: (goles * 3 + asistencias * 2) / partidos - (tarjetas rojas * 2 + tarjetas amarillas * 0.5)
            double puntosPositivos = (totalGoles * 3.0 + totalAsistencias * 2.0) / partidosJugados;
            double puntosNegativos = (tarjetasRojas * 2.0 + tarjetasAmarillas * 0.5);
            this.rating = Math.max(0, puntosPositivos - puntosNegativos);
        } else {
            this.promedioGoles = 0.0;
            this.promedioAsistencias = 0.0;
            this.rating = 0.0;
        }
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
