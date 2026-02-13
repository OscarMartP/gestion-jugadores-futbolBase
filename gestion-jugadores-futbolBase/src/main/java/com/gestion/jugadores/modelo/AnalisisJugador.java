package com.gestion.jugadores.modelo;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un análisis de IA generado para un jugador.
 * Almacena el historial de informes técnicos generados.
 */
@Entity
@Table(name = "analisis_jugadores")
public class AnalisisJugador {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;
    
    @Column(name = "temporada", length = 20, nullable = false)
    private String temporada;
    
    @Column(name = "analisis_tecnico", nullable = false, columnDefinition = "TEXT")
    private String analisisTecnico;
    
    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;
    
    @Column(name = "tokens_usados")
    private Integer tokensUsados;
    
    @Column(name = "modelo_ia", length = 50)
    private String modeloIa;
    
    // Constructores
    public AnalisisJugador() {
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    public AnalisisJugador(Jugador jugador, String temporada, String analisisTecnico) {
        this();
        this.jugador = jugador;
        this.temporada = temporada;
        this.analisisTecnico = analisisTecnico;
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
    
    public String getAnalisisTecnico() {
        return analisisTecnico;
    }
    
    public void setAnalisisTecnico(String analisisTecnico) {
        this.analisisTecnico = analisisTecnico;
    }
    
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
    
    public Integer getTokensUsados() {
        return tokensUsados;
    }
    
    public void setTokensUsados(Integer tokensUsados) {
        this.tokensUsados = tokensUsados;
    }
    
    public String getModeloIa() {
        return modeloIa;
    }
    
    public void setModeloIa(String modeloIa) {
        this.modeloIa = modeloIa;
    }
}
