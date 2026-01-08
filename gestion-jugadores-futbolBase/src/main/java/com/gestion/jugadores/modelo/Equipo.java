package com.gestion.jugadores.modelo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
//Modelo Equipo
@Entity
@Table(name = "equipo")
public class Equipo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "equipo_id")
	private Long id;


    private String nombre;

    @Column(name = "duracion_partido", nullable = false, columnDefinition = "integer default 90")
    private Integer duracionPartido = 90;

    @Column(name = "tipo_futbol", length = 20, nullable = false, columnDefinition = "varchar(20) default 'FUTBOL_11'")
    private String tipoFutbol = "FUTBOL_11"; // FUTBOL_7 o FUTBOL_11

    @OneToMany(mappedBy = "equipo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Jugador> jugadores = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    public Equipo() {}

    public Equipo(Long id, String nombre, Integer duracionPartido, String tipoFutbol, List<Jugador> jugadores, Usuario usuario) {
        this.id = id;
        this.nombre = nombre;
        this.duracionPartido = duracionPartido;
        this.tipoFutbol = tipoFutbol;
        this.jugadores = jugadores;
        this.usuario = usuario;
    }

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

    public Integer getDuracionPartido() {
        return duracionPartido;
    }

    public void setDuracionPartido(Integer duracionPartido) {
        this.duracionPartido = duracionPartido;
    }

    public String getTipoFutbol() {
        return tipoFutbol;
    }

    public void setTipoFutbol(String tipoFutbol) {
        this.tipoFutbol = tipoFutbol;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
} 
