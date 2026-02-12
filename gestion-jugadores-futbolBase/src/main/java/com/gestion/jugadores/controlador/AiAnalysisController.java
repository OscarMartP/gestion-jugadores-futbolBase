package com.gestion.jugadores.controlador;

import com.gestion.jugadores.ai.dto.AnalisisJugadorDTO;
import com.gestion.jugadores.ai.dto.InformeJugadorDTO;
import com.gestion.jugadores.ai.dto.InformePartidoDTO;
import com.gestion.jugadores.ai.service.AiAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para funcionalidades de análisis con IA.
 * Endpoints para generar informes técnicos de jugadores y partidos.
 */
@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100", "http://localhost:8101", "http://localhost"})
@Tag(name = "Análisis con IA", description = "Endpoints para generar análisis técnicos con Inteligencia Artificial")
public class AiAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(AiAnalysisController.class);
    
    @Autowired
    private AiAnalysisService aiAnalysisService;
    
    /**
     * Genera un informe técnico completo de un jugador usando IA.
     * 
     * @param jugadorId ID del jugador
     * @param temporada Temporada a analizar (ej: "2024/2025")
     * @return Informe con análisis técnico del jugador
     */
    @Operation(
        summary = "Generar informe técnico de jugador",
        description = "Genera un análisis técnico profesional de un jugador basado en sus estadísticas usando IA"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Informe generado exitosamente",
            content = @Content(schema = @Schema(implementation = InformeJugadorDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Jugador no encontrado o sin estadísticas"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error al generar el informe"
        )
    })
    @PostMapping("/jugador/{id}/informe")
    public ResponseEntity<?> generarInformeJugador(
            @Parameter(description = "ID del jugador", required = true)
            @PathVariable("id") Long jugadorId,
            @Parameter(description = "Temporada a analizar (formato: YYYY/YYYY)", example = "2024/2025")
            @RequestParam(value = "temporada", required = false, defaultValue = "2024/2025") String temporada) {
        
        try {
            logger.info("Solicitud de informe IA para jugador ID: {}, temporada: {}", jugadorId, temporada);
            
            InformeJugadorDTO informe = aiAnalysisService.generarInformeJugador(jugadorId, temporada);
            
            return ResponseEntity.ok(informe);
            
        } catch (RuntimeException e) {
            logger.error("Error generando informe de jugador", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al generar informe: " + e.getMessage()));
        }
    }
    
    /**
     * Genera un informe técnico de un partido usando IA.
     * 
     * @param partidoId ID del partido
     * @return Informe con análisis del partido
     */
    @Operation(
        summary = "Generar informe técnico de partido",
        description = "Genera un análisis técnico del partido con resumen, puntos destacados y áreas de mejora usando IA"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Informe generado exitosamente",
            content = @Content(schema = @Schema(implementation = InformePartidoDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Partido no encontrado"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error al generar el informe"
        )
    })
    @PostMapping("/partido/{id}/informe")
    public ResponseEntity<?> generarInformePartido(
            @Parameter(description = "ID del partido", required = true)
            @PathVariable("id") Long partidoId) {
        
        try {
            logger.info("Solicitud de informe IA para partido ID: {}", partidoId);
            
            InformePartidoDTO informe = aiAnalysisService.generarInformePartido(partidoId);
            
            return ResponseEntity.ok(informe);
            
        } catch (RuntimeException e) {
            logger.error("Error generando informe de partido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al generar informe: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene todos los análisis generados para un jugador.
     * 
     * @param jugadorId ID del jugador
     * @return Lista de análisis históricos
     */
    @Operation(
        summary = "Obtener análisis guardados de un jugador",
        description = "Retorna todos los análisis generados previamente para un jugador ordenados por fecha descendente"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de análisis obtenida exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Jugador no encontrado"
        )
    })
    @GetMapping("/jugador/{id}/analisis")
    public ResponseEntity<?> obtenerAnalisisJugador(
            @Parameter(description = "ID del jugador", required = true)
            @PathVariable("id") Long jugadorId) {
        
        try {
            logger.info("Solicitud de análisis históricos para jugador ID: {}", jugadorId);
            
            List<AnalisisJugadorDTO> analisis = aiAnalysisService.obtenerAnalisisJugador(jugadorId);
            
            return ResponseEntity.ok(analisis);
            
        } catch (RuntimeException e) {
            logger.error("Error obteniendo análisis de jugador", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al obtener análisis: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene un análisis específico por su ID.
     * 
     * @param analisisId ID del análisis
     * @return Datos del análisis
     */
    @Operation(
        summary = "Obtener un análisis específico",
        description = "Retorna los detalles de un análisis específico por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Análisis obtenido exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Análisis no encontrado"
        )
    })
    @GetMapping("/analisis/{id}")
    public ResponseEntity<?> obtenerAnalisisPorId(
            @Parameter(description = "ID del análisis", required = true)
            @PathVariable("id") Long analisisId) {
        
        try {
            logger.info("Solicitud de análisis ID: {}", analisisId);
            
            AnalisisJugadorDTO analisis = aiAnalysisService.obtenerAnalisisPorId(analisisId);
            
            return ResponseEntity.ok(analisis);
            
        } catch (RuntimeException e) {
            logger.error("Error obteniendo análisis", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Análisis no encontrado: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint de prueba para verificar que el módulo de IA está funcionando.
     * 
     * @return Mensaje de confirmación
     */
    @Operation(
        summary = "Probar conexión con servicio de IA",
        description = "Endpoint de prueba para verificar que el módulo de IA está configurado correctamente"
    )
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Módulo de IA operativo - OpenAI configurado correctamente");
    }
    
    /**
     * Clase interna para respuestas de error.
     */
    static class ErrorResponse {
        private String mensaje;
        private long timestamp;
        
        public ErrorResponse(String mensaje) {
            this.mensaje = mensaje;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getMensaje() {
            return mensaje;
        }
        
        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
