package com.gestion.jugadores.dto;

import java.util.List;

/**
 * DTO for Equipo entity
 * Used to transfer equipo data between frontend and backend
 */
public class EquipoDTO {

    private Long id;
    private String nombre;
    private UsuarioDTO usuario;
    private Integer duracionPartido;
    private List<JugadorDTO> jugadores;

    public EquipoDTO() {}

    public EquipoDTO(Long id, String nombre, UsuarioDTO usuario, Integer duracionPartido) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.duracionPartido = duracionPartido;
    }

    // Getters and Setters

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

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public Integer getDuracionPartido() {
        return duracionPartido;
    }

    public void setDuracionPartido(Integer duracionPartido) {
        this.duracionPartido = duracionPartido;
    }

    public List<JugadorDTO> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<JugadorDTO> jugadores) {
        this.jugadores = jugadores;
    }

    @Override
    public String toString() {
        return "EquipoDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", duracionPartido=" + duracionPartido +
                '}';
    }
}
