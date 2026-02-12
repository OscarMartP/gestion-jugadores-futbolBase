package com.gestion.jugadores.modelo;

import org.springframework.security.core.GrantedAuthority;
//Modelo Permisos
public class Authority implements GrantedAuthority {
	private String authority;

	public Authority(String authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return this.authority;
	}

}
