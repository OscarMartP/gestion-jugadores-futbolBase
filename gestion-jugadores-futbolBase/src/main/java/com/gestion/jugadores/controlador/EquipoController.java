package com.gestion.jugadores.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.servicios.EquipoService;

//Logica Equipo
@RestController
@CrossOrigin("*")
@RequestMapping("/equipos")
public class EquipoController {

	@Autowired
	private EquipoService equipoService;
    
	// Registrar equipo indicando explícitamente el userId (mantener compatibilidad)
	@PostMapping("/registrar/{userId}")
	public ResponseEntity<Equipo> registrarEquipo(
		@RequestBody Equipo equipo,
		@PathVariable Long userId) {
		Equipo equipoRegistrado = equipoService.registrarEquipo(equipo, userId);
		return ResponseEntity.ok(equipoRegistrado);
	}

	// Registrar equipo para el usuario autenticado
	@PostMapping("/registrar")
	public ResponseEntity<Equipo> registrarEquipoParaUsuarioAutenticado(
			@RequestBody Equipo equipo,
			Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String username = authentication.getName();
		Equipo equipoRegistrado = equipoService.registrarEquipoParaUsername(equipo, username);
		return ResponseEntity.status(HttpStatus.CREATED).body(equipoRegistrado);
	}

	@GetMapping("/usuario/{userId}")
	public List<Equipo> obtenerEquiposPorUsuario(@PathVariable Long userId) {
		return equipoService.obtenerEquiposPorUsuario(userId);
	}

	// Obtener equipo por ID
	@GetMapping("/{id}")
	public ResponseEntity<Equipo> obtenerEquipoPorId(@PathVariable Long id) {
		Equipo equipo = equipoService.obtenerEquipoPorId(id);
		if (equipo == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(equipo);
	}

	// Obtener equipos del usuario autenticado
	@GetMapping("/me")
	public ResponseEntity<List<Equipo>> obtenerEquiposDelUsuarioAutenticado(Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String username = authentication.getName();
		List<Equipo> equipos = equipoService.obtenerEquiposPorUsername(username);
		return ResponseEntity.ok(equipos);
	}
}
