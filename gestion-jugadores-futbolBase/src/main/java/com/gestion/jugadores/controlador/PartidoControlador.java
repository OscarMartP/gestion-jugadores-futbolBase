package com.gestion.jugadores.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.servicios.PartidoService;
//Logica Jugador 
@RestController
@RequestMapping("/api/v1/partidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PartidoControlador {
	@Autowired
    private PartidoService partidoService;

    @PostMapping
    public ResponseEntity<Partido> crearPartido(@RequestBody Partido partido) {
        Partido creado = partidoService.crearPartido(partido);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<Partido>> obtenerPartidos(@PathVariable Long equipoId) {
        List<Partido> lista = partidoService.obtenerPartidosPorEquipo(equipoId);
        return ResponseEntity.ok(lista);
    }
}
