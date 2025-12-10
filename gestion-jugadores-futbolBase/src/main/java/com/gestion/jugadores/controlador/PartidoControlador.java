package com.gestion.jugadores.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

import com.gestion.jugadores.dto.PartidoDTO;
import com.gestion.jugadores.mapper.PartidoMapper;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.servicios.PartidoService;

//Logica Partido 
@RestController
@RequestMapping("/api/v1/partidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PartidoControlador {
	@Autowired
    private PartidoService partidoService;
    
    @Autowired
    private PartidoMapper partidoMapper;

    @PostMapping
    public ResponseEntity<PartidoDTO> crearPartido(@RequestBody PartidoDTO partidoDTO) {
        Partido partido = partidoMapper.toEntity(partidoDTO);
        Partido creado = partidoService.crearPartido(partido);
        return ResponseEntity.status(HttpStatus.CREATED).body(partidoMapper.toDto(creado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartidoDTO> obtenerPartido(@PathVariable Long id) {
        Partido partido = partidoService.obtenerPartidoPorId(id);
        return ResponseEntity.ok(partidoMapper.toDto(partido));
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidos(@PathVariable Long equipoId) {
        List<Partido> lista = partidoService.obtenerPartidosPorEquipo(equipoId);
        return ResponseEntity.ok(lista.stream()
            .map(partidoMapper::toDto)
            .collect(Collectors.toList()));
    }

    @GetMapping("/activos/equipo/{equipoId}")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosActivosPorEquipo(@PathVariable Long equipoId) {
        List<Partido> partidos = partidoService.obtenerPartidosActivosPorEquipo(equipoId);
        return ResponseEntity.ok(partidos.stream()
            .map(partidoMapper::toDto)
            .collect(Collectors.toList()));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PartidoDTO>> obtenerPartidosActivos() {
        List<Partido> partidos = partidoService.obtenerPartidosActivos();
        return ResponseEntity.ok(partidos.stream()
            .map(partidoMapper::toDto)
            .collect(Collectors.toList()));
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<PartidoDTO> activarPartido(@PathVariable Long id) {
        Partido partido = partidoService.activarPartido(id);
        return ResponseEntity.ok(partidoMapper.toDto(partido));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<PartidoDTO> desactivarPartido(@PathVariable Long id) {
        Partido partido = partidoService.desactivarPartido(id);
        return ResponseEntity.ok(partidoMapper.toDto(partido));
    }

    @GetMapping("/equipo/{equipoId}/tiene-activo")
    public ResponseEntity<Boolean> tienePartidoActivo(@PathVariable Long equipoId) {
        Boolean tieneActivo = partidoService.tienePartidoActivo(equipoId);
        return ResponseEntity.ok(tieneActivo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartidoDTO> actualizarPartido(@PathVariable Long id, @RequestBody PartidoDTO partidoDTO) {
        Partido partido = partidoMapper.toEntity(partidoDTO);
        Partido actualizado = partidoService.actualizarPartido(id, partido);
        return ResponseEntity.ok(partidoMapper.toDto(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPartido(@PathVariable Long id) {
        partidoService.eliminarPartido(id);
        return ResponseEntity.noContent().build();
    }
}
