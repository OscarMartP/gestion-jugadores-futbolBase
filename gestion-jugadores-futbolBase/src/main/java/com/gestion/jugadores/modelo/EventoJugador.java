package com.gestion.jugadores.modelo;

import javax.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Modelo Evento Jugador
@Entity
@Table(name = "eventos_jugador", indexes = {
    @Index(name = "idx_jugador_id", columnList = "jugador_id"),
    @Index(name = "idx_partido_id", columnList = "partido_id"),
    @Index(name = "idx_tipo_evento", columnList = "tipo_evento")
})
public class EventoJugador {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)  // ✅ OBLIGATORIO para integridad de datos
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties({"eventos", "equipo", "hibernateLazyInitializer", "handler"})
    private Jugador jugador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties({"eventos", "equipo", "titulares", "suplentes", "hibernateLazyInitializer", "handler"})
    private Partido partido;

    @Column(name = "tipo_evento", nullable = false, length = 50)
    private String tipoEvento;

    @Column(nullable = false)
    private Integer minuto;

    // ✅ NUEVO: Campo para distinguir eventos del rival (sin jugador asociado)
    @Column(name = "es_evento_rival")
    private Boolean esEventoRival = false; // true para eventos del equipo rival (ej: gol_rival)

    // Campos adicionales para estadísticas
    @Column(name = "fue_titular")
    private Boolean fueTitular = false; // Si jugó como titular en este partido

    @Column(name = "minutos_jugados")
    private Integer minutosJugados; // Minutos que jugó en el partido

    // Campos específicos para sustituciones
    @Column(name = "jugador_sale_id")
    private Long jugadorSaleId; // ID del jugador que sale (para tipo_evento = 'sustitucion')

    @Column(name = "jugador_entra_id")
    private Long jugadorEntraId; // ID del jugador que entra (para tipo_evento = 'sustitucion')

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
    public Boolean getEsEventoRival() { return esEventoRival; }
    public Boolean getFueTitular() { return fueTitular; }
    public Integer getMinutosJugados() { return minutosJugados; }

    public void setId(Long id) { this.id = id; }
    public void setJugador(Jugador jugador) { this.jugador = jugador; }
    public void setPartido(Partido partido) { this.partido = partido; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public void setMinuto(Integer minuto) { this.minuto = minuto; }
    public void setEsEventoRival(Boolean esEventoRival) { this.esEventoRival = esEventoRival; }
    public void setFueTitular(Boolean fueTitular) { this.fueTitular = fueTitular; }
    public void setMinutosJugados(Integer minutosJugados) { this.minutosJugados = minutosJugados; }
    
    public Long getJugadorSaleId() { return jugadorSaleId; }
    public void setJugadorSaleId(Long jugadorSaleId) { this.jugadorSaleId = jugadorSaleId; }
    
    public Long getJugadorEntraId() { return jugadorEntraId; }
    public void setJugadorEntraId(Long jugadorEntraId) { this.jugadorEntraId = jugadorEntraId; }
}
