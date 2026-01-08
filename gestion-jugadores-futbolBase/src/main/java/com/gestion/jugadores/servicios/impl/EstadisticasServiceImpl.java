package com.gestion.jugadores.servicios.impl;

import com.gestion.jugadores.dto.EstadisticasEquipoDTO;
import com.gestion.jugadores.dto.EstadisticasJugadorDTO;
import com.gestion.jugadores.dto.ResumenEstadisticasDTO;
import com.gestion.jugadores.modelo.*;
import com.gestion.jugadores.repositorio.*;
import com.gestion.jugadores.servicios.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EstadisticasServiceImpl implements EstadisticasService {
    
    @Autowired
    private EstadisticasJugadorRepository estadisticasJugadorRepository;
    
    @Autowired
    private EstadisticasEquipoRepository estadisticasEquipoRepository;
    
    @Autowired
    private EventoJugadorRepository eventoJugadorRepository;
    
    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private JugadorRepositorio jugadorRepositorio;
    
    @Autowired
    private EquipoRepository equipoRepository;
    
    // ========== ESTADÍSTICAS DE JUGADORES ==========
    
    @Override
    public EstadisticasJugadorDTO obtenerEstadisticasJugador(Long jugadorId, String temporada) {
        EstadisticasJugador stats = estadisticasJugadorRepository
            .findByJugador_IdAndTemporada(jugadorId, temporada)
            .orElseGet(() -> {
                // Si no existen, calcularlas
                actualizarEstadisticasJugador(jugadorId, temporada);
                return estadisticasJugadorRepository
                    .findByJugador_IdAndTemporada(jugadorId, temporada)
                    .orElse(null);
            });
        
        return convertirAJugadorDTO(stats);
    }
    
    @Override
    public List<EstadisticasJugadorDTO> obtenerEstadisticasJugadoresEquipo(Long equipoId, String temporada) {
        List<EstadisticasJugador> estadisticas = estadisticasJugadorRepository
            .findByEquipoAndTemporada(equipoId, temporada);
        
        return estadisticas.stream()
            .map(this::convertirAJugadorDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EstadisticasJugadorDTO> obtenerTopGoleadores(Long equipoId, String temporada, int limite) {
        List<EstadisticasJugador> top = estadisticasJugadorRepository
            .findTopGoleadoresByEquipo(equipoId, temporada);
        
        return top.stream()
            .limit(limite)
            .map(this::convertirAJugadorDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EstadisticasJugadorDTO> obtenerTopAsistentes(Long equipoId, String temporada, int limite) {
        List<EstadisticasJugador> top = estadisticasJugadorRepository
            .findTopAsistentesByEquipo(equipoId, temporada);
        
        return top.stream()
            .limit(limite)
            .map(this::convertirAJugadorDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<EstadisticasJugadorDTO> obtenerMejorRating(Long equipoId, String temporada, int limite) {
        List<EstadisticasJugador> top = estadisticasJugadorRepository
            .findByMejorRating(equipoId, temporada);
        
        return top.stream()
            .limit(limite)
            .map(this::convertirAJugadorDTO)
            .collect(Collectors.toList());
    }
    
    // ========== ESTADÍSTICAS DE EQUIPOS ==========
    
    @Override
    public EstadisticasEquipoDTO obtenerEstadisticasEquipo(Long equipoId, String temporada) {
        EstadisticasEquipo stats = estadisticasEquipoRepository
            .findByEquipo_IdAndTemporada(equipoId, temporada)
            .orElseGet(() -> {
                // Si no existen, calcularlas
                actualizarEstadisticasEquipo(equipoId, temporada);
                return estadisticasEquipoRepository
                    .findByEquipo_IdAndTemporada(equipoId, temporada)
                    .orElse(null);
            });
        
        return convertirAEquipoDTO(stats);
    }
    
    @Override
    public ResumenEstadisticasDTO obtenerResumenEquipo(Long equipoId, String temporada) {
        ResumenEstadisticasDTO resumen = new ResumenEstadisticasDTO();
        
        // Estadísticas del equipo
        resumen.setEstadisticasEquipo(obtenerEstadisticasEquipo(equipoId, temporada));
        
        // Top goleadores
        resumen.setTopGoleadores(obtenerTopGoleadores(equipoId, temporada, 5));
        
        // Top asistentes
        resumen.setTopAsistentes(obtenerTopAsistentes(equipoId, temporada, 5));
        
        // Jugadores con menos tarjetas
        List<EstadisticasJugador> menosTargetas = estadisticasJugadorRepository
            .findByMenosTargetas(equipoId, temporada);
        resumen.setMenosTargetas(menosTargetas.stream()
            .limit(5)
            .map(this::convertirAJugadorDTO)
            .collect(Collectors.toList()));
        
        // Total de jugadores
        resumen.setTotalJugadores((int) jugadorRepositorio.findByEquipo_Id(equipoId).stream().count());
        
        return resumen;
    }
    
    // ========== ACTUALIZACIÓN DE ESTADÍSTICAS ==========
    
    @Override
    public void actualizarEstadisticasJugador(Long jugadorId, String temporada) {
        Jugador jugador = jugadorRepositorio.findById(jugadorId)
            .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        
        // Buscar o crear estadísticas
        EstadisticasJugador stats = estadisticasJugadorRepository
            .findByJugador_IdAndTemporada(jugadorId, temporada)
            .orElse(new EstadisticasJugador(jugador, temporada));
        
        // Reiniciar contadores
        stats.setTotalGoles(0);
        stats.setGolesEnCasa(0);
        stats.setGolesFuera(0);
        stats.setTotalAsistencias(0);
        stats.setTarjetasAmarillas(0);
        stats.setTarjetasRojas(0);
        stats.setParadas(0);
        stats.setPartidosJugados(0);
        stats.setPartidosTitular(0);
        stats.setMinutosJugados(0);
        
        // Obtener todos los eventos del jugador en la temporada
        List<EventoJugador> eventos = eventoJugadorRepository.findByJugador_Id(jugadorId);
        
        for (EventoJugador evento : eventos) {
            String tipoEvento = evento.getTipoEvento().toUpperCase();
            
            // Contar por tipo de evento
            switch (tipoEvento) {
                case "GOL":
                case "GOLES":
                    stats.setTotalGoles(stats.getTotalGoles() + 1);
                    // TODO: Determinar si fue en casa o fuera según el partido
                    break;
                case "ASISTENCIA":
                case "ASISTENCIAS":
                    stats.setTotalAsistencias(stats.getTotalAsistencias() + 1);
                    break;
                case "TARJETA_AMARILLA":
                case "AMARILLA":
                    stats.setTarjetasAmarillas(stats.getTarjetasAmarillas() + 1);
                    break;
                case "TARJETA_ROJA":
                case "ROJA":
                    stats.setTarjetasRojas(stats.getTarjetasRojas() + 1);
                    break;
                case "PARADA":
                case "PARADAS":
                    stats.setParadas(stats.getParadas() + 1);
                    break;
            }
            
            // Contar partidos y minutos
            if (evento.getFueTitular() != null && evento.getFueTitular()) {
                stats.setPartidosTitular(stats.getPartidosTitular() + 1);
            }
            
            if (evento.getMinutosJugados() != null) {
                stats.setMinutosJugados(stats.getMinutosJugados() + evento.getMinutosJugados());
            }
        }
        
        // Contar partidos únicos donde jugó
        long partidosUnicos = eventos.stream()
            .map(e -> e.getPartido().getId())
            .distinct()
            .count();
        stats.setPartidosJugados((int) partidosUnicos);
        
        // Calcular métricas
        stats.calcularMetricas();
        
        // Guardar
        estadisticasJugadorRepository.save(stats);
    }
    
    @Override
    @Transactional
    public void actualizarEstadisticasEquipo(Long equipoId, String temporada) {
        System.out.println("=== INICIANDO ACTUALIZACIÓN DE ESTADÍSTICAS ===");
        System.out.println("Equipo ID: " + equipoId + ", Temporada: " + temporada);
        
        Equipo equipo = equipoRepository.findById(equipoId)
            .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        
        System.out.println("Equipo encontrado: " + equipo.getNombre());
        
        // Buscar o crear estadísticas
        EstadisticasEquipo stats = estadisticasEquipoRepository
            .findByEquipo_IdAndTemporada(equipoId, temporada)
            .orElse(new EstadisticasEquipo(equipo, temporada));
        
        // Reiniciar contadores
        stats.setPartidosJugados(0);
        stats.setPartidosGanados(0);
        stats.setPartidosEmpatados(0);
        stats.setPartidosPerdidos(0);
        stats.setGolesFavor(0);
        stats.setGolesContra(0);
        stats.setTarjetasAmarillas(0);
        stats.setTarjetasRojas(0);
        
        // Obtener todos los partidos del equipo (finalizados y activos)
        List<Partido> partidos = partidoRepository.findByEquipo_Id(equipoId);
        System.out.println("Total de partidos encontrados: " + partidos.size());
        
        int partidosContados = 0;
        for (Partido partido : partidos) {
            // Contar partidos finalizados (partidoActivo = false)
            if (!partido.getPartidoActivo()) {
                partidosContados++;
                stats.setPartidosJugados(stats.getPartidosJugados() + 1);
                
                // Si tiene resultado, contarlo
                if (partido.getResultado() != null && !partido.getResultado().isEmpty()) {
                    String resultado = partido.getResultado().toUpperCase();
                    switch (resultado) {
                        case "VICTORIA":
                            stats.setPartidosGanados(stats.getPartidosGanados() + 1);
                            break;
                        case "EMPATE":
                            stats.setPartidosEmpatados(stats.getPartidosEmpatados() + 1);
                            break;
                        case "DERROTA":
                            stats.setPartidosPerdidos(stats.getPartidosPerdidos() + 1);
                            break;
                    }
                }
                
                // Goles
                if (partido.getGolesEquipo() != null) {
                    stats.setGolesFavor(stats.getGolesFavor() + partido.getGolesEquipo());
                }
                if (partido.getGolesRival() != null) {
                    stats.setGolesContra(stats.getGolesContra() + partido.getGolesRival());
                }
            }
        }
        
        System.out.println("Partidos finalizados contados: " + partidosContados);
        
        // Contar tarjetas de todos los eventos del equipo
        List<Jugador> jugadores = jugadorRepositorio.findByEquipo_Id(equipoId);
        System.out.println("Total de jugadores del equipo: " + jugadores.size());
        
        for (Jugador jugador : jugadores) {
            List<EventoJugador> eventos = eventoJugadorRepository.findByJugador_Id(jugador.getId());
            for (EventoJugador evento : eventos) {
                String tipo = evento.getTipoEvento().toUpperCase();
                if (tipo.contains("AMARILLA")) {
                    stats.setTarjetasAmarillas(stats.getTarjetasAmarillas() + 1);
                } else if (tipo.contains("ROJA")) {
                    stats.setTarjetasRojas(stats.getTarjetasRojas() + 1);
                }
            }
        }
        
        // Calcular métricas
        stats.calcularMetricas();
        
        // Guardar estadísticas del equipo
        EstadisticasEquipo savedStats = estadisticasEquipoRepository.save(stats);
        System.out.println("Estadísticas del equipo guardadas: " + savedStats.getId());
        System.out.println("Partidos jugados: " + savedStats.getPartidosJugados());
        
        // Actualizar estadísticas de todos los jugadores del equipo
        System.out.println("=== ACTUALIZANDO ESTADÍSTICAS DE JUGADORES ===");
        for (Jugador jugador : jugadores) {
            try {
                actualizarEstadisticasJugador(jugador.getId(), temporada);
                System.out.println("Estadísticas actualizadas para jugador: " + jugador.getNombre() + " " + jugador.getApellido());
            } catch (Exception e) {
                System.err.println("Error al actualizar estadísticas del jugador " + jugador.getId() + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== ACTUALIZACIÓN COMPLETADA ===");
    }
    
    @Override
    public void actualizarTodasLasEstadisticas(String temporada) {
        // Actualizar estadísticas de todos los equipos
        List<Equipo> equipos = equipoRepository.findAll();
        for (Equipo equipo : equipos) {
            actualizarEstadisticasEquipo(equipo.getId(), temporada);
            
            // Actualizar estadísticas de todos los jugadores del equipo
            List<Jugador> jugadores = jugadorRepositorio.findByEquipo_Id(equipo.getId());
            for (Jugador jugador : jugadores) {
                actualizarEstadisticasJugador(jugador.getId(), temporada);
            }
        }
    }
    
    @Override
    public void actualizarDespuesDeEvento(Long jugadorId, Long partidoId) {
        // Usar temporada actual por defecto
        String temporada = LocalDateTime.now().getYear() + "-" + (LocalDateTime.now().getYear() + 1);
        
        // Actualizar estadísticas del jugador
        actualizarEstadisticasJugador(jugadorId, temporada);
        
        // Actualizar estadísticas del equipo del jugador
        Jugador jugador = jugadorRepositorio.findById(jugadorId).orElse(null);
        if (jugador != null && jugador.getEquipo() != null) {
            actualizarEstadisticasEquipo(jugador.getEquipo().getId(), temporada);
        }
    }
    
    // ========== MÉTODOS DE CONVERSIÓN ==========
    
    private EstadisticasJugadorDTO convertirAJugadorDTO(EstadisticasJugador stats) {
        if (stats == null) return null;
        
        EstadisticasJugadorDTO dto = new EstadisticasJugadorDTO();
        dto.setId(stats.getId());
        dto.setJugadorId(stats.getJugador().getId());
        dto.setJugadorNombre(stats.getJugador().getNombre());
        dto.setJugadorApellido(stats.getJugador().getApellido());
        dto.setPosicion(stats.getJugador().getPosicion());
        dto.setTemporada(stats.getTemporada());
        dto.setTotalGoles(stats.getTotalGoles());
        dto.setGolesEnCasa(stats.getGolesEnCasa());
        dto.setGolesFuera(stats.getGolesFuera());
        dto.setTotalAsistencias(stats.getTotalAsistencias());
        dto.setTarjetasAmarillas(stats.getTarjetasAmarillas());
        dto.setTarjetasRojas(stats.getTarjetasRojas());
        dto.setParadas(stats.getParadas());
        dto.setPartidosJugados(stats.getPartidosJugados());
        dto.setPartidosTitular(stats.getPartidosTitular());
        dto.setMinutosJugados(stats.getMinutosJugados());
        dto.setPromedioGoles(stats.getPromedioGoles());
        dto.setPromedioAsistencias(stats.getPromedioAsistencias());
        dto.setRating(stats.getRating());
        
        return dto;
    }
    
    private EstadisticasEquipoDTO convertirAEquipoDTO(EstadisticasEquipo stats) {
        if (stats == null) return null;
        
        EstadisticasEquipoDTO dto = new EstadisticasEquipoDTO();
        dto.setId(stats.getId());
        dto.setEquipoId(stats.getEquipo().getId());
        dto.setEquipoNombre(stats.getEquipo().getNombre());
        dto.setTemporada(stats.getTemporada());
        dto.setPartidosJugados(stats.getPartidosJugados());
        dto.setPartidosGanados(stats.getPartidosGanados());
        dto.setPartidosEmpatados(stats.getPartidosEmpatados());
        dto.setPartidosPerdidos(stats.getPartidosPerdidos());
        dto.setPuntos(stats.getPuntos());
        dto.setGolesFavor(stats.getGolesFavor());
        dto.setGolesContra(stats.getGolesContra());
        dto.setDiferenciaGoles(stats.getDiferenciaGoles());
        dto.setTarjetasAmarillas(stats.getTarjetasAmarillas());
        dto.setTarjetasRojas(stats.getTarjetasRojas());
        dto.setPromedioGolesFavor(stats.getPromedioGolesFavor());
        dto.setPromedioGolesContra(stats.getPromedioGolesContra());
        dto.setEfectividad(stats.getEfectividad());
        
        return dto;
    }
}
