package com.gestion.jugadores.controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.modelo.Usuario;
import com.gestion.jugadores.repositorio.JugadorRepositorio;
import com.gestion.jugadores.servicios.EquipoService;
import com.gestion.jugadores.servicios.JugadorService;
// Logica Jugador
@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:4200")
public class JugadorControlador {

	@Autowired
	private JugadorRepositorio repositorio;
	
	@Autowired
	private JugadorService jugadorService;
	
	@Autowired
	private EquipoService equipoService;
	
	
	// Metodo para listar todos los jugadores
	@GetMapping("/jugadores")
	public List<Jugador> listarTodosLosJugadores() {
		return repositorio.findAll();
	}
	
	// Filtrado de jugadores
	@GetMapping("/jugadores/equipo/{equipoId}")
	public List<Jugador> filtrarJugadoresPorEquipo(@PathVariable Long equipoId) {
		return repositorio.findByEquipo_Id(equipoId);
	}

	
	@PostMapping("/jugadores")
	public ResponseEntity<Jugador> guardarJugador(@RequestBody Jugador jugador) {
	    if (jugador.getEquipo() == null || jugador.getEquipo().getId() == null) {
	        throw new RuntimeException("Debe proporcionar un equipo válido para el jugador");
	    }

	    // Recuperar el Equipo real
	    Equipo equipo = equipoService.obtenerEquipoPorId(jugador.getEquipo().getId());

	    jugador.setEquipo(equipo);

	    Jugador jugadorGuardado = repositorio.save(jugador);
	    return ResponseEntity.ok(jugadorGuardado);
	}



	
	 @GetMapping("/equipos/{usuarioId}")
	    public List<Equipo> getEquiposPorUsuario(@PathVariable Long usuarioId) {
	        return equipoService.obtenerEquiposPorUsuario(usuarioId);
	    }


	// este metodo sirve para buscar un jugador
	@GetMapping("/jugadores/{id}")
	public ResponseEntity<Jugador> obtenerJugadorPorId(@PathVariable Long id) {
		Jugador jugador = repositorio.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el jugador con el ID : " + id));
		return ResponseEntity.ok(jugador);
	}

	// este metodo sirve para actualizar jugador
	@PutMapping("/jugadores/{id}")
	public ResponseEntity<Jugador> actualizarJugador(@PathVariable Long id,@RequestBody Jugador detallesJugador) {
		Jugador jugador = repositorio.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el jugador con el ID : " + id));

		jugador.setNombre(detallesJugador.getNombre());
		jugador.setApellido(detallesJugador.getApellido());
		jugador.setPosicion(detallesJugador.getPosicion());

		Jugador jugadorActualizado = repositorio.save(jugador);
		return ResponseEntity.ok(jugadorActualizado);
	}

	// este metodo sirve para eliminar un jugador
	@DeleteMapping("/jugadores/{id}")
	public ResponseEntity<Map<String, Boolean>> eliminarJugador(@PathVariable Long id) {
		Jugador jugador = repositorio.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el jugador con el ID : " + id));

		repositorio.delete(jugador);
		Map<String, Boolean> respuesta = new HashMap<>();
		respuesta.put("eliminar", Boolean.TRUE);
		return ResponseEntity.ok(respuesta);
	}
	
	@PostMapping("/equipo")
	public void guardarEquipo(@RequestBody Integer id) {
		//equipoService.createEquipo(id);
	}

}
