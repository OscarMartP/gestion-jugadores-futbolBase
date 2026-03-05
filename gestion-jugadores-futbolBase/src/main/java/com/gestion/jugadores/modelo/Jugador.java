package com.gestion.jugadores.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

//Modelo Jugador
@Entity
@Table(name = "jugadores")
public class Jugador {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre", length = 60, nullable = false)
	@NotBlank(message = "El nombre es obligatorio")
	@Size(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
	private String nombre;

	@Column(name = "apellido", length = 60, nullable = false)
	@NotBlank(message = "El apellido es obligatorio")
	@Size(min = 2, max = 60, message = "El apellido debe tener entre 2 y 60 caracteres")
	private String apellido;

	@Column(name = "posicion", length = 60, nullable = false)
	@NotBlank(message = "La posición es obligatoria")
	private String posicion;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "equipo_id", nullable = false)
	@JsonBackReference
	private Equipo equipo;

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getPosicion() {
		return posicion;
	}
	public void setPosicion(String posicion) {
		// Validar que la posición sea válida según el enum Posicion
		if (posicion != null && !Posicion.esValido(posicion)) {
			throw new IllegalArgumentException("Posición inválida: " + posicion + 
				". Valores permitidos: POR, LD, LI, CEN, MC, MCO, EXD, EXIZ, DC");
		}
		this.posicion = posicion;
	}
	public Equipo getEquipo() {
		return equipo;
	}
	public void setEquipo(Equipo equipo) {
		this.equipo = equipo;
	}
	public Jugador(Long id, String nombre, String apellido, String posicion, Equipo equipo) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.posicion = posicion;
		this.equipo = equipo;
	}
	public Jugador() {
		super();
	} 
	
	@JsonProperty("equipoId")
	public Long getEquipoId() {
	    return equipo != null ? equipo.getId() : null;
	}

	
	
	

}
