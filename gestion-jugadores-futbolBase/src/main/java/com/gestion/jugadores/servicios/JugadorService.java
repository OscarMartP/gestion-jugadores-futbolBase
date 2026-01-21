package com.gestion.jugadores.servicios;

import java.util.List;

import com.gestion.jugadores.modelo.Jugador;

public interface JugadorService {

	Jugador saveJugador(Jugador jugador, Long equipoId);
	Jugador obtenerJugadorPorId(Long id);
	List<Jugador> obtenerPorEquipo(Long equipoId);
	List<Jugador> obtenerPorUsuario(Long usuarioId);

}
