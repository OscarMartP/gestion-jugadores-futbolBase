package com.gestion.jugadores.modelo;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
//Modelo Partido
@Entity
@Table(name = "partidos")
public class Partido {

	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;

	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "equipo_id", nullable = false)
	 @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	 private Equipo equipo;

	 @Column(nullable = false)
	 private LocalDateTime fecha;

	 @Column(nullable = false)
	 private Integer duracion;

	 @Column(name = "titulo")
	 private String titulo;

	 @Column(name = "partido_activo", nullable = false)
    private Boolean partidoActivo = false;

	 // Campos para estadísticas
	 @Column(name = "resultado")
	 private String resultado; // "Victoria", "Derrota", "Empate", null si no ha terminado

	 @Column(name = "goles_equipo")
	 private Integer golesEquipo;

	 @Column(name = "goles_rival")
	 private Integer golesRival;

	 public Partido() {}

	 public Partido(Equipo equipo, LocalDateTime fecha, Integer duracion, Boolean partidoActivo) {
	        this.equipo = equipo;
	        this.fecha = fecha;
	        this.duracion = duracion;
			this.partidoActivo = partidoActivo;
	 }

	 public Long getId() { return id; }
	 public Equipo getEquipo() { return equipo; }
	 public LocalDateTime getFecha() { return fecha; }
	 public Integer getDuracion() { return duracion; }
	 public String getTitulo() { return titulo; }
	 public String getResultado() { return resultado; }
	 public Integer getGolesEquipo() { return golesEquipo; }
	 public Integer getGolesRival() { return golesRival; }
	  public Boolean getPartidoActivo() {
        return partidoActivo;
    }

	 public void setId(Long id) { this.id = id; }
	 public void setEquipo(Equipo equipo) { this.equipo = equipo; }
	 public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
	 public void setDuracion(Integer duracion) { this.duracion = duracion; }
	 public void setTitulo(String titulo) { this.titulo = titulo; }
	 public void setResultado(String resultado) { this.resultado = resultado; }
	 public void setGolesEquipo(Integer golesEquipo) { this.golesEquipo = golesEquipo; }
	 public void setGolesRival(Integer golesRival) { this.golesRival = golesRival; }
	  public void setPartidoActivo(Boolean partidoActivo) {
        this.partidoActivo = partidoActivo;
    }
	}


