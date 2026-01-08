package com.gestion.jugadores.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.jugadores.modelo.EventoJugador;
import com.gestion.jugadores.modelo.EventoJugadorDTO;
import com.gestion.jugadores.modelo.EventoResumenDTO;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.servicios.EventoJugadorService;
import com.gestion.jugadores.servicios.JugadorService;
import com.gestion.jugadores.servicios.PartidoService;
//Logica Evento Jugador 
// DESACTIVADO: Usando EventoJugadorControladorV2 con arquitectura modular
// @RestController
// @RequestMapping("/api/v1/eventos")
// @CrossOrigin(origins = "http://localhost:4200")
public class EventoJugadorControlador {
	@Autowired
    private EventoJugadorService eventoJugadorService;

	@Autowired
	private JugadorService jugadorService;

	@Autowired
	private PartidoService partidoService;

	@PostMapping
	public ResponseEntity<EventoJugador> registrarEvento(@RequestBody EventoJugadorDTO dto) {
	    Partido partido = partidoService.obtenerPartidoPorId(dto.partidoId);

	    EventoJugador evento = new EventoJugador();
	    
	    // Para eventos de gol_rival, el jugador puede ser null
	    if (dto.jugadorId != null) {
	        Jugador jugador = jugadorService.obtenerJugadorPorId(dto.jugadorId);
	        evento.setJugador(jugador);
	    } else {
	        evento.setJugador(null); // Gol del rival, no tiene jugador de nuestro equipo
	    }
	    
	    evento.setPartido(partido);
	    evento.setTipoEvento(dto.tipoEvento);
	    evento.setMinuto(dto.minuto);

	    EventoJugador registrado = eventoJugadorService.registrarEvento(evento);
	    return ResponseEntity.ok(registrado);
	}

    @GetMapping("/jugador/{jugadorId}")
    public ResponseEntity<List<EventoJugador>> eventosPorJugador(@PathVariable Long jugadorId) {
        return ResponseEntity.ok(eventoJugadorService.obtenerEventosPorJugador(jugadorId));
    }

    @GetMapping("/partido/{partidoId}")
    public ResponseEntity<List<EventoJugador>> eventosPorPartido(@PathVariable Long partidoId) {
        return ResponseEntity.ok(eventoJugadorService.obtenerEventosPorPartido(partidoId));
    }
    
 // EventoJugadorControlador.java
    @GetMapping("/resumen/jugador/{jugadorId}")
    public ResponseEntity<List<EventoResumenDTO>> resumenEventosPorJugador(@PathVariable Long jugadorId) {
        return ResponseEntity.ok(eventoJugadorService.resumenEventosPorJugador(jugadorId));
    }

}
