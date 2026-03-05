package com.gestion.jugadores.ai.service.impl;

import com.gestion.jugadores.ai.dto.AnalisisJugadorDTO;
import com.gestion.jugadores.ai.dto.InformeJugadorDTO;
import com.gestion.jugadores.ai.dto.InformePartidoDTO;
import com.gestion.jugadores.ai.port.AiAnalysisPort;
import com.gestion.jugadores.ai.service.AiAnalysisService;
import com.gestion.jugadores.dto.EstadisticasJugadorDTO;
import com.gestion.jugadores.dto.EstadisticasPartidoDTO;
import com.gestion.jugadores.modelo.AnalisisJugador;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.repositorio.AnalisisJugadorRepository;
import com.gestion.jugadores.servicios.EstadisticasService;
import com.gestion.jugadores.servicios.JugadorService;
import com.gestion.jugadores.servicios.PartidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de análisis con IA.
 * Orquesta la obtención de datos del dominio y la generación de análisis con IA.
 */
@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(AiAnalysisServiceImpl.class);
    private static final Integer MAX_TOKENS_JUGADOR = 600;
    private static final Integer MAX_TOKENS_PARTIDO = 800;
    
    @Autowired
    private AiAnalysisPort aiAnalysisPort;
    
    @Autowired
    private EstadisticasService estadisticasService;
    
    @Autowired
    private JugadorService jugadorService;
    
    @Autowired
    private PartidoService partidoService;
    
    @Autowired
    private AnalisisJugadorRepository analisisJugadorRepository;
    
    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String modeloIa;
    
    @Override
    @Transactional
    public InformeJugadorDTO generarInformeJugador(Long jugadorId, String temporada) {
        logger.info("Generando informe con IA para jugador ID: {}, temporada: {}", jugadorId, temporada);
        
        try {
            // 1. Obtener datos del jugador
            Jugador jugador = jugadorService.obtenerJugadorPorId(jugadorId);
            if (jugador == null) {
                throw new RuntimeException("Jugador no encontrado con ID: " + jugadorId);
            }
            
            // 2. Obtener estadísticas
            EstadisticasJugadorDTO stats = estadisticasService.obtenerEstadisticasJugador(jugadorId, temporada);
            if (stats == null) {
                throw new RuntimeException("No hay estadísticas disponibles para el jugador en la temporada: " + temporada);
            }
            
            // 3. Construir prompt estructurado
            String prompt = construirPromptJugador(jugador, stats);
            
            // 4. Generar análisis con IA
            String analisisTecnico = aiAnalysisPort.generateAnalysis(prompt, MAX_TOKENS_JUGADOR);
            
            // 5. Guardar análisis en base de datos
            AnalisisJugador analisisEntity = new AnalisisJugador();
            analisisEntity.setJugador(jugador);
            analisisEntity.setTemporada(temporada);
            analisisEntity.setAnalisisTecnico(analisisTecnico);
            analisisEntity.setModeloIa(modeloIa);
            // TODO: Capturar tokens reales del response de OpenAI si está disponible
            analisisEntity.setTokensUsados(MAX_TOKENS_JUGADOR);
            
            analisisEntity = analisisJugadorRepository.save(analisisEntity);
            logger.info("Análisis guardado en BD con ID: {}", analisisEntity.getId());
            
            // 6. Construir y retornar DTO
            InformeJugadorDTO informe = new InformeJugadorDTO();
            informe.setJugadorId(jugadorId);
            informe.setNombreCompleto(jugador.getNombre() + " " + jugador.getApellido());
            informe.setPosicion(jugador.getPosicion());
            informe.setTemporada(temporada);
            informe.setAnalisisTecnico(analisisTecnico);
            
            logger.info("Informe generado exitosamente para jugador ID: {}", jugadorId);
            return informe;
            
        } catch (Exception e) {
            logger.error("Error generando informe de jugador ID: {}", jugadorId, e);
            throw new RuntimeException("Error al generar informe de jugador: " + e.getMessage(), e);
        }
    }
    
    @Override
    public InformePartidoDTO generarInformePartido(Long partidoId) {
        logger.info("Generando informe con IA para partido ID: {}", partidoId);
        
        try {
            // 1. Obtener datos del partido
            Partido partido = partidoService.obtenerPartidoPorId(partidoId);
            if (partido == null) {
                throw new RuntimeException("Partido no encontrado con ID: " + partidoId);
            }
            
            // 2. Obtener estadísticas del partido
            EstadisticasPartidoDTO stats = estadisticasService.obtenerEstadisticasPartido(partidoId);
            
            // 3. Construir prompt estructurado
            String prompt = construirPromptPartido(partido, stats);
            
            // 4. Generar análisis con IA
            String analisisCompleto = aiAnalysisPort.generateAnalysis(prompt, MAX_TOKENS_PARTIDO);
            
            // 5. Parsear respuesta (asumiendo formato estructurado)
            InformePartidoDTO informe = new InformePartidoDTO();
            informe.setPartidoId(partidoId);
            informe.setTitulo(partido.getTitulo());
            informe.setFecha(partido.getFecha());
            informe.setResultado(partido.getResultado());
            informe.setResumenTactico(analisisCompleto);
            
            // Intentar separar secciones si la IA las generó (opcional)
            String[] secciones = parsearSecciones(analisisCompleto);
            if (secciones.length >= 2) {
                informe.setPuntosDestacados(secciones[0]);
                informe.setAreasMejora(secciones[1]);
            }
            
            logger.info("Informe generado exitosamente para partido ID: {}", partidoId);
            return informe;
            
        } catch (Exception e) {
            logger.error("Error generando informe de partido ID: {}", partidoId, e);
            throw new RuntimeException("Error al generar informe de partido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Construye un prompt estructurado para análisis de jugador.
     */
    private String construirPromptJugador(Jugador jugador, EstadisticasJugadorDTO stats) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Analiza el rendimiento del siguiente jugador de fútbol base:\n\n");
        prompt.append("DATOS DEL JUGADOR:\n");
        prompt.append("Nombre: ").append(jugador.getNombre()).append(" ").append(jugador.getApellido()).append("\n");
        prompt.append("Posición: ").append(jugador.getPosicion()).append("\n");
        prompt.append("Temporada: ").append(stats.getTemporada()).append("\n\n");
        
        prompt.append("ESTADÍSTICAS:\n");
        prompt.append("Partidos jugados: ").append(stats.getPartidosJugados()).append("\n");
        prompt.append("Partidos titular: ").append(stats.getPartidosTitular()).append("\n");
        prompt.append("Minutos totales: ").append(stats.getMinutosJugados()).append("\n");
        prompt.append("Goles: ").append(stats.getTotalGoles()).append("\n");
        prompt.append("Asistencias: ").append(stats.getTotalAsistencias()).append("\n");
        prompt.append("Pases clave: ").append(stats.getTotalPasesClave()).append(" (")
              .append(String.format("%.2f", stats.getPasesClaveP90())).append(" por 90')\n");
        prompt.append("Tiros a puerta: ").append(stats.getTotalTirosAPuerta()).append(" (")
              .append(String.format("%.2f", stats.getTirosAPuertaP90())).append(" por 90')\n");
        prompt.append("Robos: ").append(stats.getTotalRobos()).append(" (")
              .append(String.format("%.2f", stats.getRobosP90())).append(" por 90')\n");
        prompt.append("Pérdidas: ").append(stats.getTotalPerdidas()).append(" (")
              .append(String.format("%.2f", stats.getPerdidasP90())).append(" por 90')\n");
        prompt.append("Tarjetas amarillas: ").append(stats.getTarjetasAmarillas()).append("\n");
        prompt.append("Tarjetas rojas: ").append(stats.getTarjetasRojas()).append("\n");
        
        if (stats.getRating() != null) {
            prompt.append("Rating promedio: ").append(String.format("%.2f", stats.getRating())).append("\n");
        }
        
        prompt.append("\nINSTRUCCIONES:\n");
        prompt.append("Proporciona un análisis técnico profesional de máximo 200 palabras que incluya:\n");
        prompt.append("1. Evaluación de rendimiento general\n");
        prompt.append("2. Puntos fuertes principales\n");
        prompt.append("3. Áreas de mejora específicas\n");
        prompt.append("4. Conclusión orientada al desarrollo del jugador\n");
        prompt.append("\nUsa lenguaje técnico futbolístico apropiado para fútbol base.");
        
        return prompt.toString();
    }
    
    /**
     * Construye un prompt estructurado para análisis de partido.
     */
    private String construirPromptPartido(Partido partido, EstadisticasPartidoDTO stats) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Analiza el siguiente partido de fútbol base:\n\n");
        prompt.append("DATOS DEL PARTIDO:\n");
        prompt.append("Título: ").append(partido.getTitulo()).append("\n");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        prompt.append("Fecha: ").append(partido.getFecha().format(formatter)).append("\n");
        prompt.append("Duración: ").append(partido.getDuracion()).append(" minutos\n");
        prompt.append("Resultado: ").append(partido.getResultado()).append("\n");
        prompt.append("Marcador: ").append(partido.getGolesEquipo()).append(" - ").append(partido.getGolesRival()).append("\n\n");
        
        if (stats != null) {
            prompt.append("ESTADÍSTICAS DEL EQUIPO:\n");
            prompt.append("Total goles: ").append(stats.getTotalGoles()).append("\n");
            prompt.append("Total asistencias: ").append(stats.getTotalAsistencias()).append("\n");
            prompt.append("Pases clave: ").append(stats.getTotalPasesClave()).append("\n");
            prompt.append("Tiros a puerta: ").append(stats.getTotalTirosAPuerta()).append("\n");
            prompt.append("Tarjetas amarillas: ").append(stats.getTotalTarjetasAmarillas()).append("\n");
            prompt.append("Tarjetas rojas: ").append(stats.getTotalTarjetasRojas()).append("\n\n");
        }
        
        prompt.append("INSTRUCCIONES:\n");
        prompt.append("Genera un informe técnico estructurado de máximo 300 palabras con:\n\n");
        prompt.append("**PUNTOS DESTACADOS:**\n");
        prompt.append("(Aspectos positivos del partido)\n\n");
        prompt.append("**ÁREAS DE MEJORA:**\n");
        prompt.append("(Aspectos a trabajar en entrenamientos)\n\n");
        prompt.append("Usa lenguaje técnico futbolístico apropiado para entrenadores de fútbol base.");
        
        return prompt.toString();
    }
    
    /**
     * Intenta parsear secciones del análisis (puntos destacados y áreas de mejora).
     */
    private String[] parsearSecciones(String analisis) {
        String[] secciones = new String[2];
        
        // Intenta dividir por marcadores comunes
        if (analisis.contains("ÁREAS DE MEJORA") || analisis.contains("**ÁREAS DE MEJORA")) {
            String[] partes = analisis.split("\\*\\*ÁREAS DE MEJORA\\*\\*|ÁREAS DE MEJORA");
            if (partes.length >= 2) {
                secciones[0] = partes[0].replace("**PUNTOS DESTACADOS:**", "").trim();
                secciones[1] = partes[1].trim();
                return secciones;
            }
        }
        
        // Si no se puede dividir, retorna todo como resumen
        secciones[0] = analisis;
        secciones[1] = "";
        return secciones;
    }
    
    @Override
    public List<AnalisisJugadorDTO> obtenerAnalisisJugador(Long jugadorId) {
        logger.info("Obteniendo análisis históricos para jugador ID: {}", jugadorId);
        
        List<AnalisisJugador> analisis = analisisJugadorRepository.findByJugadorIdOrderByFechaGeneracionDesc(jugadorId);
        
        return analisis.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public AnalisisJugadorDTO obtenerAnalisisPorId(Long analisisId) {
        logger.info("Obteniendo análisis ID: {}", analisisId);
        
        AnalisisJugador analisis = analisisJugadorRepository.findById(analisisId)
                .orElseThrow(() -> new RuntimeException("Análisis no encontrado con ID: " + analisisId));
        
        return convertirADTO(analisis);
    }
    
    /**
     * Convierte una entidad AnalisisJugador a DTO.
     */
    private AnalisisJugadorDTO convertirADTO(AnalisisJugador entity) {
        Jugador jugador = entity.getJugador();
        String nombreCompleto = jugador.getNombre() + " " + jugador.getApellido();
        
        return new AnalisisJugadorDTO(
                entity.getId(),
                jugador.getId(),
                nombreCompleto,
                jugador.getPosicion(),
                entity.getAnalisisTecnico(),
                entity.getTemporada(),
                entity.getFechaGeneracion(),
                entity.getTokensUsados(),
                entity.getModeloIa()
        );
    }
}
