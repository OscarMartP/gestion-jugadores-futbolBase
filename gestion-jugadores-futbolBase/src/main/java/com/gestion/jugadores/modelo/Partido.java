package com.gestion.jugadores.modelo;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;
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

	 @ElementCollection(fetch = FetchType.LAZY)
	 @CollectionTable(name = "partido_titulares", joinColumns = @JoinColumn(name = "partido_id"))
	 @Column(name = "jugador_id")
	 private List<Long> titulares;

	 @ElementCollection(fetch = FetchType.LAZY)
	 @CollectionTable(name = "partido_suplentes", joinColumns = @JoinColumn(name = "partido_id"))
	 @Column(name = "jugador_id")
	 private List<Long> suplentes;

	 public Partido() {}

	 public Partido(Equipo equipo, LocalDateTime fecha, Integer duracion, Boolean partidoActivo) {
	        this.equipo = equipo;
	        this.fecha = fecha;
	        this.duracion = duracion;
			this.partidoActivo = partidoActivo;
			this.titulares = new java.util.ArrayList<>();
			this.suplentes = new java.util.ArrayList<>();
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

	 public List<Long> getTitulares() { return titulares; }
	 public void setTitulares(List<Long> titulares) { this.titulares = titulares; }

	 public List<Long> getSuplentes() { return suplentes; }
	 public void setSuplentes(List<Long> suplentes) { this.suplentes = suplentes; }

	 /**
	  * Valida que no haya jugadores duplicados entre titulares y suplentes
	  * @throws IllegalArgumentException si hay jugadores duplicados
	  */
	 public void validarAlineacion() {
		 if (titulares == null || suplentes == null) {
			 return; // No hay alineación que validar
		 }

		 // Buscar intersección entre titulares y suplentes
		 List<Long> duplicados = titulares.stream()
			 .filter(suplentes::contains)
			 .collect(java.util.stream.Collectors.toList());

		 if (!duplicados.isEmpty()) {
			 throw new IllegalArgumentException(
				 "Los siguientes jugadores están duplicados en titulares y suplentes: " + duplicados
			 );
		 }
	 }
	}


