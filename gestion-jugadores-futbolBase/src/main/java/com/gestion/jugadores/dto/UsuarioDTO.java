package com.gestion.jugadores.dto;

/**
 * DTO for Usuario entity
 * Used to transfer usuario data between frontend and backend
 * Excludes sensitive data like passwords
 */
public class UsuarioDTO {

    private Long id;
    private String username;
    private String email;

    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
