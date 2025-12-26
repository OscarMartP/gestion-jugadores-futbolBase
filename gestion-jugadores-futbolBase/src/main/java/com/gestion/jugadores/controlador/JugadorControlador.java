package com.gestion.jugadores.controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;

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

import com.gestion.jugadores.dto.JugadorDTO;
import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.mapper.JugadorMapper;
import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.modelo.Usuario;
import com.gestion.jugadores.repositorio.JugadorRepositorio;
import com.gestion.jugadores.servicios.EquipoService;
import com.gestion.jugadores.servicios.JugadorService;
// Logica Jugador
// DESACTIVADO: Usando JugadorControladorV2 con arquitectura modular
// @RestController
// @RequestMapping("/api/v1/")
// @CrossOrigin(origins = "http://localhost:4200")
public class JugadorControlador {

	@Autowired
	private JugadorRepositorio repositorio;
	
	@Autowired
	private JugadorService jugadorService;
	
	@Autowired
	private EquipoService equipoService;
	
	@Autowired
	private JugadorMapper jugadorMapper;
    
	@Autowired
	private com.gestion.jugadores.repositorio.UsuarioRepository usuarioRepository;
	
	
	// Metodo para listar jugadores
	// Si se proporciona 'equipoId' devuelve jugadores de ese equipo
	// Si no se proporciona, devuelve jugadores de todos los equipos del usuario autenticado
	@GetMapping("/jugadores")
	public ResponseEntity<List<JugadorDTO>> listarJugadores(@RequestParam(required = false) Long equipoId, Authentication authentication) {
		if (equipoId != null) {
			List<Jugador> jugadores = jugadorService.obtenerPorEquipo(equipoId);
			return ResponseEntity.ok(jugadores.stream()
				.map(jugadorMapper::toDto)
				.collect(Collectors.toList()));
		}

		if (authentication == null) {
			return ResponseEntity.status(401).build();
		}

		String username = authentication.getName();
		com.gestion.jugadores.modelo.Usuario usuario = usuarioRepository.findByUsername(username);
		if (usuario == null) {
			return ResponseEntity.status(404).build();
		}

		List<Jugador> jugadores = jugadorService.obtenerPorUsuario(usuario.getId());
		return ResponseEntity.ok(jugadores.stream()
			.map(jugadorMapper::toDto)
			.collect(Collectors.toList()));
	}

	
	@PostMapping("/jugadores")
	public ResponseEntity<JugadorDTO> guardarJugador(@RequestBody JugadorDTO jugadorDTO) {
	    // Validar que se proporcione equipoId
	    if (jugadorDTO.getEquipoId() == null) {
	        throw new RuntimeException("Debe proporcionar un equipoId válido para el jugador");
	    }

	    // Recuperar el Equipo real de la base de datos
	    Equipo equipo = equipoService.obtenerEquipoPorId(jugadorDTO.getEquipoId());
	    if (equipo == null) {
	        throw new ResourceNotFoundException("No existe el equipo con el ID: " + jugadorDTO.getEquipoId());
	    }

	    // Crear la entidad Jugador con los datos del DTO
	    Jugador jugador = new Jugador();
	    jugador.setNombre(jugadorDTO.getNombre());
	    jugador.setApellido(jugadorDTO.getApellido());
	    jugador.setPosicion(jugadorDTO.getPosicion());
	    jugador.setEquipo(equipo);

	    // Guardar el jugador
	    Jugador jugadorGuardado = repositorio.save(jugador);
	    return ResponseEntity.ok(jugadorMapper.toDto(jugadorGuardado));
	}

	
	 @GetMapping("/equipos/{usuarioId}")
	    public List<Equipo> getEquiposPorUsuario(@PathVariable Long usuarioId) {
	        return equipoService.obtenerEquiposPorUsuario(usuarioId);
	    }


	// este metodo sirve para buscar un jugador
	@GetMapping("/jugadores/{id}")
	public ResponseEntity<JugadorDTO> obtenerJugadorPorId(@PathVariable Long id) {
		Jugador jugador = repositorio.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el jugador con el ID : " + id));
		return ResponseEntity.ok(jugadorMapper.toDto(jugador));
	}

	// este metodo sirve para actualizar jugador
	@PutMapping("/jugadores/{id}")
	public ResponseEntity<JugadorDTO> actualizarJugador(@PathVariable Long id, @RequestBody JugadorDTO detallesJugadorDTO) {
		Jugador jugador = repositorio.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el jugador con el ID : " + id));

		jugador.setNombre(detallesJugadorDTO.getNombre());
		jugador.setApellido(detallesJugadorDTO.getApellido());
		jugador.setPosicion(detallesJugadorDTO.getPosicion());

		Jugador jugadorActualizado = repositorio.save(jugador);
		return ResponseEntity.ok(jugadorMapper.toDto(jugadorActualizado));
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
