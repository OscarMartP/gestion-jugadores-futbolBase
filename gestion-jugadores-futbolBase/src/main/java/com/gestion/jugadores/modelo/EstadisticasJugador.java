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
    
    // Estadísticas de pases clave
    @Column(name = "total_pases_clave")
    private Integer totalPasesClave = 0;
    
    // Distribución temporal de pases clave (por tramos de 15 minutos)
    @Column(name = "pases_clave_0_15")
    private Integer pasesClave0_15 = 0;
    
    @Column(name = "pases_clave_16_30")
    private Integer pasesClave16_30 = 0;
    
    @Column(name = "pases_clave_31_45")
    private Integer pasesClave31_45 = 0;
    
    @Column(name = "pases_clave_46_60")
    private Integer pasesClave46_60 = 0;
    
    @Column(name = "pases_clave_61_75")
    private Integer pasesClave61_75 = 0;
    
    @Column(name = "pases_clave_76_90")
    private Integer pasesClave76_90 = 0;
    
    // Pases clave según estado del marcador
    @Column(name = "pases_clave_ganando")
    private Integer pasesClaveGanando = 0;
    
    @Column(name = "pases_clave_empatando")
    private Integer pasesClaveEmpatando = 0;
    
    @Column(name = "pases_clave_perdiendo")
    private Integer pasesClaveperdiendo = 0;
    
    // Métrica: pases clave por 90 minutos
    @Column(name = "pases_clave_por_90")
    private Double pasesClaveP90 = 0.0;
    
    // Estadísticas de tiros a puerta
    @Column(name = "total_tiros_a_puerta")
    private Integer totalTirosAPuerta = 0;
    
    // Distribución temporal de tiros a puerta (por tramos de 15 minutos)
    @Column(name = "tiros_a_puerta_0_15")
    private Integer tirosAPuerta0_15 = 0;
    
    @Column(name = "tiros_a_puerta_16_30")
    private Integer tirosAPuerta16_30 = 0;
    
    @Column(name = "tiros_a_puerta_31_45")
    private Integer tirosAPuerta31_45 = 0;
    
    @Column(name = "tiros_a_puerta_46_60")
    private Integer tirosAPuerta46_60 = 0;
    
    @Column(name = "tiros_a_puerta_61_75")
    private Integer tirosAPuerta61_75 = 0;
    
    @Column(name = "tiros_a_puerta_76_90")
    private Integer tirosAPuerta76_90 = 0;
    
    // Tiros a puerta según estado del marcador
    @Column(name = "tiros_a_puerta_ganando")
    private Integer tirosAPuertaGanando = 0;
    
    @Column(name = "tiros_a_puerta_empatando")
    private Integer tirosAPuertaEmpatando = 0;
    
    @Column(name = "tiros_a_puerta_perdiendo")
    private Integer tirosAPuertaPerdiendo = 0;
    
    // Métrica: tiros a puerta por 90 minutos
    @Column(name = "tiros_a_puerta_por_90")
    private Double tirosAPuertaP90 = 0.0;
    
    // Estadísticas de robos del jugador
    @Column(name = "total_robos")
    private Integer totalRobos = 0;
    
    // Distribución temporal de robos (por tramos de 15 minutos)
    @Column(name = "robos_0_15")
    private Integer robos0_15 = 0;
    
    @Column(name = "robos_16_30")
    private Integer robos16_30 = 0;
    
    @Column(name = "robos_31_45")
    private Integer robos31_45 = 0;
    
    @Column(name = "robos_46_60")
    private Integer robos46_60 = 0;
    
    @Column(name = "robos_61_75")
    private Integer robos61_75 = 0;
    
    @Column(name = "robos_76_90")
    private Integer robos76_90 = 0;
    
    // Robos según estado del marcador
    @Column(name = "robos_ganando")
    private Integer robosGanando = 0;
    
    @Column(name = "robos_empatando")
    private Integer robosEmpatando = 0;
    
    @Column(name = "robos_perdiendo")
    private Integer robosPerdiendo = 0;
    
    // Métrica: robos por 90 minutos
    @Column(name = "robos_por_90")
    private Double robosP90 = 0.0;
    
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
        return pasesClaveperdiendo;
    }

    public void setPasesClavePerdiendo(Integer pasesClavePerdiendo) {
        this.pasesClaveperdiendo = pasesClavePerdiendo;
    }

    public Double getPasesClaveP90() {
        return pasesClaveP90;
    }

    public void setPasesClaveP90(Double pasesClaveP90) {
        this.pasesClaveP90 = pasesClaveP90;
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
    
    /**
     * Calcula y actualiza las métricas derivadas (promedios y rating)
     */
    public void calcularMetricas() {
        if (partidosJugados > 0) {
            this.promedioGoles = (double) totalGoles / partidosJugados;
            this.promedioAsistencias = (double) totalAsistencias / partidosJugados;
            
            // Calcular pases clave por 90 minutos (null-safe)
            int pasesClave = (totalPasesClave != null) ? totalPasesClave : 0;
            if (minutosJugados > 0 && pasesClave > 0) {
                this.pasesClaveP90 = ((double) pasesClave / minutosJugados) * 90.0;
            } else {
                this.pasesClaveP90 = 0.0;
            }
            
            // Calcular tiros a puerta por 90 minutos (null-safe)
            int tirosAPuerta = (totalTirosAPuerta != null) ? totalTirosAPuerta : 0;
            if (minutosJugados > 0 && tirosAPuerta > 0) {
                this.tirosAPuertaP90 = ((double) tirosAPuerta / minutosJugados) * 90.0;
            } else {
                this.tirosAPuertaP90 = 0.0;
            }
            
            // Calcular robos por 90 minutos (null-safe)
            int robos = (totalRobos != null) ? totalRobos : 0;
            if (minutosJugados > 0 && robos > 0) {
                this.robosP90 = ((double) robos / minutosJugados) * 90.0;
            } else {
                this.robosP90 = 0.0;
            }
            
            // Rating básico: (goles * 3 + asistencias * 2 + pases clave * 1) / partidos - (tarjetas rojas * 2 + tarjetas amarillas * 0.5)
            double puntosPositivos = (totalGoles * 3.0 + totalAsistencias * 2.0 + pasesClave * 1.0) / partidosJugados;
            double puntosNegativos = (tarjetasRojas * 2.0 + tarjetasAmarillas * 0.5);
            this.rating = Math.max(0, puntosPositivos - puntosNegativos);
        } else {
            this.promedioGoles = 0.0;
            this.promedioAsistencias = 0.0;
            this.pasesClaveP90 = 0.0;
            this.tirosAPuertaP90 = 0.0;
            this.robosP90 = 0.0;
            this.rating = 0.0;
        }
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
