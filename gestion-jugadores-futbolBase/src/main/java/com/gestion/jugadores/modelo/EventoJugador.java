package com.gestion.jugadores.modelo;

import javax.persistence.*;
//Modelo Evento Jugador
@Entity
@Table(name = "eventos_jugador")
public class EventoJugador {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @Column(nullable = false)
    private Integer minuto;

    public EventoJugador() {}

    public EventoJugador(Jugador jugador, Partido partido, String tipoEvento, Integer minuto) {
        this.jugador = jugador;
        this.partido = partido;
        this.tipoEvento = tipoEvento;
        this.minuto = minuto;
    }

    public Long getId() { return id; }
    public Jugador getJugador() { return jugador; }
    public Partido getPartido() { return partido; }
    public String getTipoEvento() { return tipoEvento; }
    public Integer getMinuto() { return minuto; }

    public void setId(Long id) { this.id = id; }
    public void setJugador(Jugador jugador) { this.jugador = jugador; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public void setMinuto(Integer minuto) { this.minuto = minuto; }
}
