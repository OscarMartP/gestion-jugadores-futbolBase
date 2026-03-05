package com.gestion.jugadores.modelo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

//Modelo Usuario
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;
	private String password;
	private String nombre;
	private String apellido;
	private String email;
	private String perfil;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Equipo> equipos;
	
	private boolean enabled = true;

	//CREACION RELACIONES BBDD 
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "usuario")
	@JsonIgnore
	private Set<UsuarioRol> usuarioRoles = new HashSet<>();

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public List<Equipo> getEquipo() {
		return equipos;
	}

	public void setEquipo(List<Equipo> equipo) {
		this.equipos = equipo;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<UsuarioRol> getUsuarioRoles() {
		return usuarioRoles;
	}

	public void setUsuarioRoles(Set<UsuarioRol> usuarioRoles) {
		this.usuarioRoles = usuarioRoles;
	}

	public Usuario(Long id, String username, String password, String nombre, String apellido, String email,
			String perfil, List<Equipo> equipo, boolean enabled, Set<UsuarioRol> usuarioRoles) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.perfil = perfil;
		this.equipos = equipo;
		this.enabled = enabled;
		this.usuarioRoles = usuarioRoles;
	}

	public Usuario() {
		super();
	}

	public Usuario(Long id) {
		super();
		this.id = id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Authority> autoridades = new HashSet<>();
		this.usuarioRoles.forEach(usuarioRol -> {
			autoridades.add(new Authority(usuarioRol.getRol().getRolNombre()));
		});
		return autoridades;
	}
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	

	
	
	


}
