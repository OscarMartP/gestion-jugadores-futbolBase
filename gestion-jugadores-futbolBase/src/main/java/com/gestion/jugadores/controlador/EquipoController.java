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

import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.repositorio.EquipoRepository;
import com.gestion.jugadores.servicios.EquipoService;
//Logica Equipo
@RestController
@CrossOrigin("*")
@RequestMapping("/equipos")
public class EquipoController {

	@Autowired
	private EquipoService equipoService;
	
	@PostMapping("/registrar/{userId}")
	public ResponseEntity<Equipo> registrarEquipo(
	    @RequestBody Equipo equipo,
	    @PathVariable Long userId) {
	    
	    Equipo equipoRegistrado = equipoService.registrarEquipo(equipo, userId);
	    return ResponseEntity.ok(equipoRegistrado);
	}

	/*@GetMapping("/{nombre}")
    public Equipo obtenerEquipoPorNombre(@PathVariable String nombre) {
        return equipoService.obtenerEquipoPorNombre(nombre);
    }*/
	
	@GetMapping("/usuario/{userId}")
    public List<Equipo> obtenerEquiposPorUsuario(@PathVariable Long userId) {
        return equipoService.obtenerEquiposPorUsuario(userId);
    }
}
