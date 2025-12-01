package com.gestion.jugadores.servicios.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.repositorio.EquipoRepository;
import com.gestion.jugadores.repositorio.JugadorRepositorio;
import com.gestion.jugadores.servicios.JugadorService;

@Service
public class JugadoresServiceImpl implements JugadorService{

	@Autowired
    private JugadorRepositorio jugadorRepositorio;

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    public Jugador saveJugador(Jugador jugador, Long equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId).orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        jugador.setEquipo(equipo);
        return jugadorRepositorio.save(jugador);
    }
    
    public Jugador obtenerJugadorPorId(Long id) {
        return jugadorRepositorio.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Jugador no encontrado con ID: " + id));
    }

    /*@Override
    public Jugador crearJugador(Jugador jugador, Long equipoId) {
        // Validaciones adicionales pueden ir aquí
        return jugadorRepositorio.save(jugador);
    }*/

	/*@Override
	public List<Jugador> getJugadoresByUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}*/

	
}
