package com.gestion.jugadores.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * DTO for Jugador entity
 * Used to transfer jugador data between frontend and backend
 */
public class JugadorDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String posicion;
    private Long equipoId;
    private EquipoDTO equipo;

    public JugadorDTO() {}

    public JugadorDTO(Long id, String nombre, String apellido, String posicion, Long equipoId) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.posicion = posicion;
        this.equipoId = equipoId;
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
        this.posicion = posicion;
    }

    public Long getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Long equipoId) {
        this.equipoId = equipoId;
    }

    public EquipoDTO getEquipo() {
        return equipo;
    }

    public void setEquipo(EquipoDTO equipo) {
        this.equipo = equipo;
    }

    @Override
    public String toString() {
        return "JugadorDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", posicion='" + posicion + '\'' +
                ", equipoId=" + equipoId +
                '}';
    }
}
