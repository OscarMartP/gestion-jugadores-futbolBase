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
    
    // Estadísticas de pases clave del equipo
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
    private Integer pasesClavePerdiendo = 0;
    
    // Pases clave por 90 minutos (promedio del equipo)
    @Column(name = "pases_clave_por_90")
    private Double pasesClaveP90 = 0.0;
    
    // Mayor pasador del equipo (jugador con más pases clave)
    @Column(name = "mayor_pasador", length = 100)
    private String mayorPasador;
    
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
            
            // Pases clave por 90 minutos (asumiendo 90 minutos por partido en promedio) - null-safe
            int pasesClave = (totalPasesClave != null) ? totalPasesClave : 0;
            int minutosEstimados = partidosJugados * 90;
            if (minutosEstimados > 0 && pasesClave > 0) {
                this.pasesClaveP90 = ((double) pasesClave / minutosEstimados) * 90.0;
            } else {
                this.pasesClaveP90 = 0.0;
            }
        } else {
            this.promedioGolesFavor = 0.0;
            this.promedioGolesContra = 0.0;
            this.efectividad = 0.0;
            this.pasesClaveP90 = 0.0;
        }
        
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
