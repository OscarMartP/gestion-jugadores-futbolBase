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
        System.out.println("🔍 OBTENIENDO ESTADÍSTICAS DEL EQUIPO - ID: " + equipoId + ", Temporada: " + temporada);
        EstadisticasEquipo stats = estadisticasEquipoRepository
            .findByEquipo_IdAndTemporada(equipoId, temporada)
            .orElseGet(() -> {
                // Si no existen, calcularlas
                actualizarEstadisticasEquipo(equipoId, temporada);
                return estadisticasEquipoRepository
                    .findByEquipo_IdAndTemporada(equipoId, temporada)
                    .orElse(null);
            });
        
        System.out.println("📋 Stats encontradas: " + (stats != null ? "SÍ" : "NO"));
        
        // Calcular mayor pasador si no está calculado o está desactualizado
        if (stats != null) {
            System.out.println("🎯 Calculando mayor pasador para equipo " + equipoId);
            String mayorPasadorActual = calcularMayorPasador(equipoId, temporada);
            System.out.println("👑 Mayor pasador calculado: " + mayorPasadorActual);
            System.out.println("💾 Mayor pasador en BD: " + stats.getMayorPasador());
            
            if (!mayorPasadorActual.equals(stats.getMayorPasador())) {
                System.out.println("🔄 Actualizando mayor pasador en BD...");
                stats.setMayorPasador(mayorPasadorActual);
                estadisticasEquipoRepository.save(stats);
                System.out.println("✅ Mayor pasador actualizado en BD");
            } else {
                System.out.println("✓ Mayor pasador ya está actualizado");
            }
        }
        
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
        
        // Inicializar campos de pases clave a 0
        stats.setTotalPasesClave(0);
        stats.setPasesClave0_15(0);
        stats.setPasesClave16_30(0);
        stats.setPasesClave31_45(0);
        stats.setPasesClave46_60(0);
        stats.setPasesClave61_75(0);
        stats.setPasesClave76_90(0);
        stats.setPasesClaveGanando(0);
        stats.setPasesClaveEmpatando(0);
        stats.setPasesClavePerdiendo(0);
        stats.setPasesClaveP90(0.0);
        
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
                case "PASE_CLAVE":
                case "PASE CLAVE":
                    System.out.println("🔑 Procesando pase clave - Evento ID: " + evento.getId() + ", Jugador: " + jugador.getNombre());
                    // Incrementar total de pases clave
                    stats.setTotalPasesClave(stats.getTotalPasesClave() + 1);
                    
                    // Determinar el intervalo temporal del pase clave
                    Integer minuto = evento.getMinuto();
                    if (minuto != null) {
                        if (minuto >= 0 && minuto <= 15) {
                            stats.setPasesClave0_15(stats.getPasesClave0_15() + 1);
                        } else if (minuto >= 16 && minuto <= 30) {
                            stats.setPasesClave16_30(stats.getPasesClave16_30() + 1);
                        } else if (minuto >= 31 && minuto <= 45) {
                            stats.setPasesClave31_45(stats.getPasesClave31_45() + 1);
                        } else if (minuto >= 46 && minuto <= 60) {
                            stats.setPasesClave46_60(stats.getPasesClave46_60() + 1);
                        } else if (minuto >= 61 && minuto <= 75) {
                            stats.setPasesClave61_75(stats.getPasesClave61_75() + 1);
                        } else if (minuto >= 76 && minuto <= 90) {
                            stats.setPasesClave76_90(stats.getPasesClave76_90() + 1);
                        }
                        
                        // Determinar el estado del partido en ese momento usando el ID del evento
                        String estadoMarcador = determinarEstadoMarcadorEnMinuto(evento.getPartido(), evento.getId(), jugador.getEquipo());
                        if ("GANANDO".equals(estadoMarcador)) {
                            stats.setPasesClaveGanando(stats.getPasesClaveGanando() + 1);
                        } else if ("EMPATANDO".equals(estadoMarcador)) {
                            stats.setPasesClaveEmpatando(stats.getPasesClaveEmpatando() + 1);
                        } else if ("PERDIENDO".equals(estadoMarcador)) {
                            stats.setPasesClavePerdiendo(stats.getPasesClavePerdiendo() + 1);
                        }
                    }
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
        
        System.out.println("📊 RESUMEN - Jugador: " + jugador.getNombre() + " " + jugador.getApellido());
        System.out.println("   Total pases clave: " + stats.getTotalPasesClave());
        System.out.println("   Ganando: " + stats.getPasesClaveGanando());
        System.out.println("   Empatando: " + stats.getPasesClaveEmpatando());
        System.out.println("   Perdiendo: " + stats.getPasesClavePerdiendo());
        
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
        stats.setTotalPasesClave(0);
        stats.setPasesClave0_15(0);
        stats.setPasesClave16_30(0);
        stats.setPasesClave31_45(0);
        stats.setPasesClave46_60(0);
        stats.setPasesClave61_75(0);
        stats.setPasesClave76_90(0);
        stats.setPasesClaveGanando(0);
        stats.setPasesClaveEmpatando(0);
        stats.setPasesClavePerdiendo(0);
        
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
                } else if (tipo.equals("PASE_CLAVE") || tipo.equals("PASE CLAVE")) {
                    // Contar pase clave para el equipo
                    stats.setTotalPasesClave(stats.getTotalPasesClave() + 1);
                    
                    // Agregar distribución temporal
                    Integer minuto = evento.getMinuto();
                    if (minuto != null) {
                        if (minuto >= 0 && minuto <= 15) {
                            stats.setPasesClave0_15(stats.getPasesClave0_15() + 1);
                        } else if (minuto >= 16 && minuto <= 30) {
                            stats.setPasesClave16_30(stats.getPasesClave16_30() + 1);
                        } else if (minuto >= 31 && minuto <= 45) {
                            stats.setPasesClave31_45(stats.getPasesClave31_45() + 1);
                        } else if (minuto >= 46 && minuto <= 60) {
                            stats.setPasesClave46_60(stats.getPasesClave46_60() + 1);
                        } else if (minuto >= 61 && minuto <= 75) {
                            stats.setPasesClave61_75(stats.getPasesClave61_75() + 1);
                        } else if (minuto >= 76 && minuto <= 90) {
                            stats.setPasesClave76_90(stats.getPasesClave76_90() + 1);
                        }
                        
                        // Determinar estado del marcador en ese minuto
                        String estadoMarcador = determinarEstadoMarcadorEnMinuto(evento.getPartido(), evento.getId(), equipo);
                        if ("GANANDO".equals(estadoMarcador)) {
                            stats.setPasesClaveGanando(stats.getPasesClaveGanando() + 1);
                        } else if ("EMPATANDO".equals(estadoMarcador)) {
                            stats.setPasesClaveEmpatando(stats.getPasesClaveEmpatando() + 1);
                        } else if ("PERDIENDO".equals(estadoMarcador)) {
                            stats.setPasesClavePerdiendo(stats.getPasesClavePerdiendo() + 1);
                        }
                    }
                }
            }
        }
        
        // Calcular métricas
        stats.calcularMetricas();
        
        // Calcular mayor pasador del equipo
        String mayorPasador = calcularMayorPasador(equipoId, temporada);
        stats.setMayorPasador(mayorPasador);
        
        // Guardar estadísticas del equipo
        EstadisticasEquipo savedStats = estadisticasEquipoRepository.save(stats);
        System.out.println("Estadísticas del equipo guardadas: " + savedStats.getId());
        System.out.println("Partidos jugados: " + savedStats.getPartidosJugados());
        System.out.println("Mayor pasador: " + savedStats.getMayorPasador());
        
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
    
    /**
     * Calcula el jugador con más pases clave del equipo
     */
    private String calcularMayorPasador(Long equipoId, String temporada) {
        System.out.println("🔢 Buscando estadísticas de jugadores - Equipo: " + equipoId + ", Temporada: " + temporada);
        List<EstadisticasJugador> estadisticasJugadores = estadisticasJugadorRepository
            .findByJugador_Equipo_IdAndTemporada(equipoId, temporada);
        
        System.out.println("📊 Jugadores encontrados: " + estadisticasJugadores.size());
        
        if (estadisticasJugadores.isEmpty()) {
            System.out.println("⚠️ No hay jugadores con estadísticas");
            return "N/A";
        }
        
        // Mostrar pases clave de cada jugador
        for (EstadisticasJugador stats : estadisticasJugadores) {
            System.out.println("  - " + stats.getJugador().getNombre() + " " + stats.getJugador().getApellido() + ": " + stats.getTotalPasesClave() + " pases clave");
        }
        
        // Encontrar el jugador con más pases clave
        EstadisticasJugador mejorPasador = estadisticasJugadores.stream()
            .filter(stats -> stats.getTotalPasesClave() != null && stats.getTotalPasesClave() > 0)
            .max((s1, s2) -> {
                int pases1 = s1.getTotalPasesClave() != null ? s1.getTotalPasesClave() : 0;
                int pases2 = s2.getTotalPasesClave() != null ? s2.getTotalPasesClave() : 0;
                return Integer.compare(pases1, pases2);
            })
            .orElse(null);
        
        if (mejorPasador == null || mejorPasador.getTotalPasesClave() == 0) {
            return "N/A";
        }
        
        // Retornar nombre completo del jugador
        Jugador jugador = mejorPasador.getJugador();
        return jugador.getNombre() + " " + jugador.getApellido() + " (" + mejorPasador.getTotalPasesClave() + ")";
    }
    
    /**
     * Determina el estado del marcador en un minuto específico del partido
     * reconstruyendo el marcador cronológicamente desde los eventos de gol
     * Usa el ID del evento para determinar el orden exacto dentro del mismo minuto
     */
    private String determinarEstadoMarcadorEnMinuto(Partido partido, Long eventoId, Equipo equipoJugador) {
        if (partido == null || eventoId == null) {
            return "EMPATANDO"; // Por defecto si no hay datos
        }
        
        // Obtener todos los eventos de gol del partido que ocurrieron ANTES de este evento
        // Usando el ID del evento (auto-incremental) para determinar el orden cronológico exacto
        List<EventoJugador> eventosPartido = eventoJugadorRepository.findAll().stream()
            .filter(e -> e.getPartido().getId().equals(partido.getId()))
            .filter(e -> {
                String tipo = e.getTipoEvento().toUpperCase();
                return tipo.equals("GOL") || tipo.equals("GOLES") || tipo.equals("GOL_RIVAL");
            })
            .filter(e -> e.getId() < eventoId) // Solo goles registrados ANTES de este evento
            .sorted((e1, e2) -> e1.getId().compareTo(e2.getId()))
            .collect(java.util.stream.Collectors.toList());
        
        // Reconstruir el marcador hasta el minuto objetivo
        int golesEquipo = 0;
        int golesRival = 0;
        
        System.out.println("DEBUG - Analizando evento ID: " + eventoId + " del equipo: " + equipoJugador.getNombre());
        
        for (EventoJugador gol : eventosPartido) {
            String tipoEvento = gol.getTipoEvento().toUpperCase();
            
            // Si es un gol_rival, siempre es del rival
            if (tipoEvento.equals("GOL_RIVAL")) {
                golesRival++;
                System.out.println("  -> Gol ID " + gol.getId() + " del RIVAL (marcado manualmente)");
            } else if (gol.getJugador() == null) {
                // Si no hay jugador asociado, asumir que es del rival
                golesRival++;
                System.out.println("  -> Gol ID " + gol.getId() + " sin jugador (asumido como RIVAL)");
            } else {
                // Verificar si el gol fue del equipo del jugador o del rival
                boolean esDelEquipo = gol.getJugador().getEquipo().getId().equals(equipoJugador.getId());
                if (esDelEquipo) {
                    golesEquipo++;
                    System.out.println("  -> Gol ID " + gol.getId() + " del equipo " + gol.getJugador().getEquipo().getNombre() + " (nuestro)");
                } else {
                    golesRival++;
                    System.out.println("  -> Gol ID " + gol.getId() + " del rival " + gol.getJugador().getEquipo().getNombre());
                }
            }
        }
        
        System.out.println("  Marcador reconstruido: " + golesEquipo + "-" + golesRival);
        
        // Determinar el estado
        String estado;
        if (golesEquipo > golesRival) {
            estado = "GANANDO";
        } else if (golesEquipo < golesRival) {
            estado = "PERDIENDO";
        } else {
            estado = "EMPATANDO";
        }
        
        System.out.println("  Estado: " + estado);
        return estado;
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
        dto.setTotalPasesClave(stats.getTotalPasesClave());
        dto.setPasesClave0_15(stats.getPasesClave0_15());
        dto.setPasesClave16_30(stats.getPasesClave16_30());
        dto.setPasesClave31_45(stats.getPasesClave31_45());
        dto.setPasesClave46_60(stats.getPasesClave46_60());
        dto.setPasesClave61_75(stats.getPasesClave61_75());
        dto.setPasesClave76_90(stats.getPasesClave76_90());
        dto.setPasesClaveGanando(stats.getPasesClaveGanando());
        dto.setPasesClaveEmpatando(stats.getPasesClaveEmpatando());
        dto.setPasesClavePerdiendo(stats.getPasesClavePerdiendo());
        dto.setPasesClaveP90(stats.getPasesClaveP90());
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
        dto.setTotalPasesClave(stats.getTotalPasesClave());
        dto.setPasesClave0_15(stats.getPasesClave0_15());
        dto.setPasesClave16_30(stats.getPasesClave16_30());
        dto.setPasesClave31_45(stats.getPasesClave31_45());
        dto.setPasesClave46_60(stats.getPasesClave46_60());
        dto.setPasesClave61_75(stats.getPasesClave61_75());
        dto.setPasesClave76_90(stats.getPasesClave76_90());
        dto.setPasesClaveGanando(stats.getPasesClaveGanando());
        dto.setPasesClaveEmpatando(stats.getPasesClaveEmpatando());
        dto.setPasesClavePerdiendo(stats.getPasesClavePerdiendo());
        dto.setPasesClaveP90(stats.getPasesClaveP90());
        dto.setMayorPasador(stats.getMayorPasador());
        dto.setPromedioGolesFavor(stats.getPromedioGolesFavor());
        dto.setPromedioGolesContra(stats.getPromedioGolesContra());
        dto.setEfectividad(stats.getEfectividad());
        
        return dto;
    }
}
