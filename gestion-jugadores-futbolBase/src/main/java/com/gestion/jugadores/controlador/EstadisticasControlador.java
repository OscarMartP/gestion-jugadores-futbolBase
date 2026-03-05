package com.gestion.jugadores.controlador;

import com.gestion.jugadores.dto.EstadisticasEquipoDTO;
import com.gestion.jugadores.dto.EstadisticasJugadorDTO;
import com.gestion.jugadores.dto.EstadisticasPartidoDTO;
import com.gestion.jugadores.dto.ResumenEstadisticasDTO;
import com.gestion.jugadores.servicios.EstadisticasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/estadisticas")
@CrossOrigin(origins = "*")
@Tag(name = "Estadísticas", description = "API para gestión de estadísticas de jugadores y equipos")
public class EstadisticasControlador {

    @Autowired
    private EstadisticasService estadisticasService;

    // ========== ENDPOINTS DE ESTADÍSTICAS DE JUGADORES ==========

    @GetMapping("/jugador/{jugadorId}")
    @Operation(summary = "Obtener estadísticas de un jugador", 
               description = "Retorna las estadísticas completas de un jugador para una temporada específica")
    public ResponseEntity<EstadisticasJugadorDTO> obtenerEstadisticasJugador(
            @Parameter(description = "ID del jugador") @PathVariable Long jugadorId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        EstadisticasJugadorDTO estadisticas = estadisticasService.obtenerEstadisticasJugador(jugadorId, temporada);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/equipo/{equipoId}/jugadores")
    @Operation(summary = "Obtener estadísticas de todos los jugadores de un equipo",
               description = "Retorna una lista con las estadísticas de todos los jugadores del equipo")
    public ResponseEntity<List<EstadisticasJugadorDTO>> obtenerEstadisticasJugadoresEquipo(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        List<EstadisticasJugadorDTO> estadisticas = estadisticasService.obtenerEstadisticasJugadoresEquipo(equipoId, temporada);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/equipo/{equipoId}/top-goleadores")
    @Operation(summary = "Obtener top goleadores del equipo",
               description = "Retorna los jugadores con más goles del equipo ordenados descendentemente")
    public ResponseEntity<List<EstadisticasJugadorDTO>> obtenerTopGoleadores(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada,
            @Parameter(description = "Límite de resultados (por defecto 5)") @RequestParam(defaultValue = "5") int limite) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        List<EstadisticasJugadorDTO> topGoleadores = estadisticasService.obtenerTopGoleadores(equipoId, temporada, limite);
        return ResponseEntity.ok(topGoleadores);
    }

    @GetMapping("/equipo/{equipoId}/top-asistentes")
    @Operation(summary = "Obtener top asistentes del equipo",
               description = "Retorna los jugadores con más asistencias del equipo ordenados descendentemente")
    public ResponseEntity<List<EstadisticasJugadorDTO>> obtenerTopAsistentes(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada,
            @Parameter(description = "Límite de resultados (por defecto 5)") @RequestParam(defaultValue = "5") int limite) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        List<EstadisticasJugadorDTO> topAsistentes = estadisticasService.obtenerTopAsistentes(equipoId, temporada, limite);
        return ResponseEntity.ok(topAsistentes);
    }

    @GetMapping("/equipo/{equipoId}/mejor-rating")
    @Operation(summary = "Obtener jugadores con mejor rating",
               description = "Retorna los jugadores con mejor rating del equipo ordenados descendentemente")
    public ResponseEntity<List<EstadisticasJugadorDTO>> obtenerMejorRating(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada,
            @Parameter(description = "Límite de resultados (por defecto 5)") @RequestParam(defaultValue = "5") int limite) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        List<EstadisticasJugadorDTO> mejorRating = estadisticasService.obtenerMejorRating(equipoId, temporada, limite);
        return ResponseEntity.ok(mejorRating);
    }

    // ========== ENDPOINTS DE ESTADÍSTICAS DE EQUIPOS ==========

    @GetMapping("/equipo/{equipoId}")
    @Operation(summary = "Obtener estadísticas del equipo",
               description = "Retorna las estadísticas completas del equipo para una temporada específica")
    public ResponseEntity<EstadisticasEquipoDTO> obtenerEstadisticasEquipo(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        EstadisticasEquipoDTO estadisticas = estadisticasService.obtenerEstadisticasEquipo(equipoId, temporada);
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/equipo/{equipoId}/resumen")
    @Operation(summary = "Obtener resumen completo de estadísticas",
               description = "Retorna un resumen completo incluyendo estadísticas del equipo y top jugadores")
    public ResponseEntity<ResumenEstadisticasDTO> obtenerResumenEquipo(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        ResumenEstadisticasDTO resumen = estadisticasService.obtenerResumenEquipo(equipoId, temporada);
        return ResponseEntity.ok(resumen);
    }
    
    @GetMapping("/partido/{partidoId}")
    @Operation(summary = "Obtener estadísticas de un partido individual",
               description = "Retorna todas las estadísticas detalladas de un partido específico")
    public ResponseEntity<EstadisticasPartidoDTO> obtenerEstadisticasPartido(
            @Parameter(description = "ID del partido") @PathVariable Long partidoId) {
        
        EstadisticasPartidoDTO estadisticas = estadisticasService.obtenerEstadisticasPartido(partidoId);
        return ResponseEntity.ok(estadisticas);
    }

    // ========== ENDPOINTS DE ACTUALIZACIÓN ==========

    @PutMapping("/jugador/{jugadorId}/actualizar")
    @Operation(summary = "Actualizar estadísticas de un jugador",
               description = "Recalcula las estadísticas de un jugador desde sus eventos")
    public ResponseEntity<String> actualizarEstadisticasJugador(
            @Parameter(description = "ID del jugador") @PathVariable Long jugadorId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        estadisticasService.actualizarEstadisticasJugador(jugadorId, temporada);
        return ResponseEntity.ok("Estadísticas del jugador actualizadas correctamente");
    }

    @PutMapping("/equipo/{equipoId}/actualizar")
    @Operation(summary = "Actualizar estadísticas del equipo",
               description = "Recalcula las estadísticas del equipo desde sus partidos")
    public ResponseEntity<String> actualizarEstadisticasEquipo(
            @Parameter(description = "ID del equipo") @PathVariable Long equipoId,
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        estadisticasService.actualizarEstadisticasEquipo(equipoId, temporada);
        return ResponseEntity.ok("Estadísticas del equipo actualizadas correctamente");
    }

    @PutMapping("/actualizar-todas")
    @Operation(summary = "Actualizar todas las estadísticas",
               description = "Recalcula las estadísticas de todos los equipos y jugadores")
    public ResponseEntity<String> actualizarTodasLasEstadisticas(
            @Parameter(description = "Temporada (ejemplo: 2023-2024)") @RequestParam(required = false) String temporada) {
        
        if (temporada == null || temporada.isEmpty()) {
            temporada = obtenerTemporadaActual();
        }
        
        try {
            estadisticasService.actualizarTodasLasEstadisticas(temporada);
            return ResponseEntity.ok("Todas las estadísticas han sido actualizadas correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar estadísticas: " + e.getMessage());
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private String obtenerTemporadaActual() {
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        
        // Si estamos antes de julio, la temporada es año anterior - año actual
        // Si estamos después de julio, la temporada es año actual - año siguiente
        if (month < 7) {
            return (year - 1) + "-" + year;
        } else {
            return year + "-" + (year + 1);
        }
    }
}
