package com.gestion.jugadores.servicios.impl;

import com.gestion.jugadores.dto.EstadisticasEquipoDTO;
import com.gestion.jugadores.dto.EstadisticasJugadorDTO;
import com.gestion.jugadores.dto.EstadisticasPartidoDTO;
import com.gestion.jugadores.dto.ResumenEstadisticasDTO;
import com.gestion.jugadores.modelo.*;
import com.gestion.jugadores.repositorio.*;
import com.gestion.jugadores.servicios.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class EstadisticasServiceImpl implements EstadisticasService {
    
    private static final Logger logger = LoggerFactory.getLogger(EstadisticasServiceImpl.class);
    
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
        
        // Inicializar campos de tiros a puerta a 0
        stats.setTotalTirosAPuerta(0);
        stats.setTirosAPuerta0_15(0);
        stats.setTirosAPuerta16_30(0);
        stats.setTirosAPuerta31_45(0);
        stats.setTirosAPuerta46_60(0);
        stats.setTirosAPuerta61_75(0);
        stats.setTirosAPuerta76_90(0);
        stats.setTirosAPuertaGanando(0);
        stats.setTirosAPuertaEmpatando(0);
        stats.setTirosAPuertaPerdiendo(0);
        stats.setTirosAPuertaP90(0.0);
        
        // Inicializar campos de robos a 0
        stats.setTotalRobos(0);
        stats.setRobos0_15(0);
        stats.setRobos16_30(0);
        stats.setRobos31_45(0);
        stats.setRobos46_60(0);
        stats.setRobos61_75(0);
        stats.setRobos76_90(0);
        stats.setRobosGanando(0);
        stats.setRobosEmpatando(0);
        stats.setRobosPerdiendo(0);
        stats.setRobosP90(0.0);
        
        // Obtener todos los eventos del jugador en la temporada
        List<EventoJugador> eventos = eventoJugadorRepository.findByJugador_Id(jugadorId);
        
        for (EventoJugador evento : eventos) {
            String tipoEvento = evento.getTipoEvento().toUpperCase();
            
            // Contar por tipo de evento
            switch (tipoEvento) {
                case "GOL":
                case "GOLES":
                    stats.setTotalGoles(stats.getTotalGoles() + 1);
                    // Los goles también son tiros a puerta, procesarlos igual que TIRO_A_PUERTA
                    System.out.println("⚽ Procesando gol como tiro a puerta - Evento ID: " + evento.getId() + ", Jugador: " + jugador.getNombre());
                    stats.setTotalTirosAPuerta(stats.getTotalTirosAPuerta() + 1);
                    
                    Integer minutoGol = evento.getMinuto();
                    if (minutoGol != null) {
                        if (minutoGol >= 0 && minutoGol <= 15) {
                            stats.setTirosAPuerta0_15(stats.getTirosAPuerta0_15() + 1);
                        } else if (minutoGol >= 16 && minutoGol <= 30) {
                            stats.setTirosAPuerta16_30(stats.getTirosAPuerta16_30() + 1);
                        } else if (minutoGol >= 31 && minutoGol <= 45) {
                            stats.setTirosAPuerta31_45(stats.getTirosAPuerta31_45() + 1);
                        } else if (minutoGol >= 46 && minutoGol <= 60) {
                            stats.setTirosAPuerta46_60(stats.getTirosAPuerta46_60() + 1);
                        } else if (minutoGol >= 61 && minutoGol <= 75) {
                            stats.setTirosAPuerta61_75(stats.getTirosAPuerta61_75() + 1);
                        } else if (minutoGol >= 76 && minutoGol <= 90) {
                            stats.setTirosAPuerta76_90(stats.getTirosAPuerta76_90() + 1);
                        }
                        
                        String estadoMarcadorGol = determinarEstadoMarcadorEnMinuto(evento.getPartido(), evento.getId(), jugador.getEquipo());
                        if ("GANANDO".equals(estadoMarcadorGol)) {
                            stats.setTirosAPuertaGanando(stats.getTirosAPuertaGanando() + 1);
                        } else if ("EMPATANDO".equals(estadoMarcadorGol)) {
                            stats.setTirosAPuertaEmpatando(stats.getTirosAPuertaEmpatando() + 1);
                        } else if ("PERDIENDO".equals(estadoMarcadorGol)) {
                            stats.setTirosAPuertaPerdiendo(stats.getTirosAPuertaPerdiendo() + 1);
                        }
                    }
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
                case "TIRO_A_PUERTA":
                case "TIRO A PUERTA":
                    System.out.println("🎯 Procesando tiro a puerta - Evento ID: " + evento.getId() + ", Jugador: " + jugador.getNombre());
                    // Incrementar total de tiros a puerta
                    stats.setTotalTirosAPuerta(stats.getTotalTirosAPuerta() + 1);
                    
                    // Determinar el intervalo temporal del tiro a puerta
                    Integer minutoTiro = evento.getMinuto();
                    if (minutoTiro != null) {
                        if (minutoTiro >= 0 && minutoTiro <= 15) {
                            stats.setTirosAPuerta0_15(stats.getTirosAPuerta0_15() + 1);
                        } else if (minutoTiro >= 16 && minutoTiro <= 30) {
                            stats.setTirosAPuerta16_30(stats.getTirosAPuerta16_30() + 1);
                        } else if (minutoTiro >= 31 && minutoTiro <= 45) {
                            stats.setTirosAPuerta31_45(stats.getTirosAPuerta31_45() + 1);
                        } else if (minutoTiro >= 46 && minutoTiro <= 60) {
                            stats.setTirosAPuerta46_60(stats.getTirosAPuerta46_60() + 1);
                        } else if (minutoTiro >= 61 && minutoTiro <= 75) {
                            stats.setTirosAPuerta61_75(stats.getTirosAPuerta61_75() + 1);
                        } else if (minutoTiro >= 76 && minutoTiro <= 90) {
                            stats.setTirosAPuerta76_90(stats.getTirosAPuerta76_90() + 1);
                        }
                        
                        // Determinar el estado del partido en ese momento
                        String estadoMarcadorTiro = determinarEstadoMarcadorEnMinuto(evento.getPartido(), evento.getId(), jugador.getEquipo());
                        if ("GANANDO".equals(estadoMarcadorTiro)) {
                            stats.setTirosAPuertaGanando(stats.getTirosAPuertaGanando() + 1);
                        } else if ("EMPATANDO".equals(estadoMarcadorTiro)) {
                            stats.setTirosAPuertaEmpatando(stats.getTirosAPuertaEmpatando() + 1);
                        } else if ("PERDIENDO".equals(estadoMarcadorTiro)) {
                            stats.setTirosAPuertaPerdiendo(stats.getTirosAPuertaPerdiendo() + 1);
                        }
                    }
                    break;
                    
                case "ROBO":
                case "ROBOS":
                    System.out.println("🛡️ Procesando robo - Evento ID: " + evento.getId() + ", Jugador: " + jugador.getNombre());
                    // Incrementar total de robos (null-safe)
                    stats.setTotalRobos((stats.getTotalRobos() != null ? stats.getTotalRobos() : 0) + 1);
                    
                    // Determinar el intervalo temporal del robo
                    Integer minutoRobo = evento.getMinuto();
                    if (minutoRobo != null) {
                        if (minutoRobo >= 0 && minutoRobo <= 15) {
                            stats.setRobos0_15((stats.getRobos0_15() != null ? stats.getRobos0_15() : 0) + 1);
                        } else if (minutoRobo >= 16 && minutoRobo <= 30) {
                            stats.setRobos16_30((stats.getRobos16_30() != null ? stats.getRobos16_30() : 0) + 1);
                        } else if (minutoRobo >= 31 && minutoRobo <= 45) {
                            stats.setRobos31_45((stats.getRobos31_45() != null ? stats.getRobos31_45() : 0) + 1);
                        } else if (minutoRobo >= 46 && minutoRobo <= 60) {
                            stats.setRobos46_60((stats.getRobos46_60() != null ? stats.getRobos46_60() : 0) + 1);
                        } else if (minutoRobo >= 61 && minutoRobo <= 75) {
                            stats.setRobos61_75((stats.getRobos61_75() != null ? stats.getRobos61_75() : 0) + 1);
                        } else if (minutoRobo >= 76 && minutoRobo <= 90) {
                            stats.setRobos76_90((stats.getRobos76_90() != null ? stats.getRobos76_90() : 0) + 1);
                        }
                        
                        // Determinar el estado del partido en ese momento
                        String estadoMarcadorRobo = determinarEstadoMarcadorEnMinuto(evento.getPartido(), evento.getId(), jugador.getEquipo());
                        if ("GANANDO".equals(estadoMarcadorRobo)) {
                            stats.setRobosGanando((stats.getRobosGanando() != null ? stats.getRobosGanando() : 0) + 1);
                        } else if ("EMPATANDO".equals(estadoMarcadorRobo)) {
                            stats.setRobosEmpatando((stats.getRobosEmpatando() != null ? stats.getRobosEmpatando() : 0) + 1);
                        } else if ("PERDIENDO".equals(estadoMarcadorRobo)) {
                            stats.setRobosPerdiendo((stats.getRobosPerdiendo() != null ? stats.getRobosPerdiendo() : 0) + 1);
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
        
        // Inicializar campos de tiros a puerta a 0
        stats.setTotalTirosAPuerta(0);
        stats.setTirosAPuerta0_15(0);
        stats.setTirosAPuerta16_30(0);
        stats.setTirosAPuerta31_45(0);
        stats.setTirosAPuerta46_60(0);
        stats.setTirosAPuerta61_75(0);
        stats.setTirosAPuerta76_90(0);
        stats.setTirosAPuertaGanando(0);
        stats.setTirosAPuertaEmpatando(0);
        stats.setTirosAPuertaPerdiendo(0);
        
        // Inicializar campos de robos a 0
        stats.setTotalRobos(0);
        stats.setRobos0_15(0);
        stats.setRobos16_30(0);
        stats.setRobos31_45(0);
        stats.setRobos46_60(0);
        stats.setRobos61_75(0);
        stats.setRobos76_90(0);
        stats.setRobosGanando(0);
        stats.setRobosEmpatando(0);
        stats.setRobosPerdiendo(0);
        
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
                } else if (tipo.equals("TIRO_A_PUERTA") || tipo.equals("TIRO A PUERTA") || tipo.equals("GOL")) {
                    // Contar tiro a puerta para el equipo (incluye goles ya que un gol es un tiro a puerta que entró)
                    stats.setTotalTirosAPuerta(stats.getTotalTirosAPuerta() + 1);
                    
                    // Agregar distribución temporal
                    Integer minutoTiro = evento.getMinuto();
                    if (minutoTiro != null) {
                        if (minutoTiro >= 0 && minutoTiro <= 15) {
                            stats.setTirosAPuerta0_15(stats.getTirosAPuerta0_15() + 1);
                        } else if (minutoTiro >= 16 && minutoTiro <= 30) {
                            stats.setTirosAPuerta16_30(stats.getTirosAPuerta16_30() + 1);
                        } else if (minutoTiro >= 31 && minutoTiro <= 45) {
                            stats.setTirosAPuerta31_45(stats.getTirosAPuerta31_45() + 1);
                        } else if (minutoTiro >= 46 && minutoTiro <= 60) {
                            stats.setTirosAPuerta46_60(stats.getTirosAPuerta46_60() + 1);
                        } else if (minutoTiro >= 61 && minutoTiro <= 75) {
                            stats.setTirosAPuerta61_75(stats.getTirosAPuerta61_75() + 1);
                        } else if (minutoTiro >= 76 && minutoTiro <= 90) {
                            stats.setTirosAPuerta76_90(stats.getTirosAPuerta76_90() + 1);
                        }
                        
                        // Determinar estado del marcador en ese minuto
                        String estadoMarcadorTiro = determinarEstadoMarcadorEnMinuto(evento.getPartido(), evento.getId(), equipo);
                        if ("GANANDO".equals(estadoMarcadorTiro)) {
                            stats.setTirosAPuertaGanando(stats.getTirosAPuertaGanando() + 1);
                        } else if ("EMPATANDO".equals(estadoMarcadorTiro)) {
                            stats.setTirosAPuertaEmpatando(stats.getTirosAPuertaEmpatando() + 1);
                        } else if ("PERDIENDO".equals(estadoMarcadorTiro)) {
                            stats.setTirosAPuertaPerdiendo(stats.getTirosAPuertaPerdiendo() + 1);
                        }
                    }
                } else if (tipo.equals("ROBO") || tipo.equals("ROBOS")) {
                    // Contar robo para el equipo (null-safe)
                    stats.setTotalRobos((stats.getTotalRobos() != null ? stats.getTotalRobos() : 0) + 1);
                    
                    // Agregar distribución temporal
                    Integer minutoRobo = evento.getMinuto();
                    if (minutoRobo != null) {
                        if (minutoRobo >= 0 && minutoRobo <= 15) {
                            stats.setRobos0_15((stats.getRobos0_15() != null ? stats.getRobos0_15() : 0) + 1);
                        } else if (minutoRobo >= 16 && minutoRobo <= 30) {
                            stats.setRobos16_30((stats.getRobos16_30() != null ? stats.getRobos16_30() : 0) + 1);
                        } else if (minutoRobo >= 31 && minutoRobo <= 45) {
                            stats.setRobos31_45((stats.getRobos31_45() != null ? stats.getRobos31_45() : 0) + 1);
                        } else if (minutoRobo >= 46 && minutoRobo <= 60) {
                            stats.setRobos46_60((stats.getRobos46_60() != null ? stats.getRobos46_60() : 0) + 1);
                        } else if (minutoRobo >= 61 && minutoRobo <= 75) {
                            stats.setRobos61_75((stats.getRobos61_75() != null ? stats.getRobos61_75() : 0) + 1);
                        } else if (minutoRobo >= 76 && minutoRobo <= 90) {
                            stats.setRobos76_90((stats.getRobos76_90() != null ? stats.getRobos76_90() : 0) + 1);
                        }
                        
                        // Determinar estado del marcador en ese minuto
                        String estadoMarcadorRobo = determinarEstadoMarcadorEnMinuto(evento.getPartido(), evento.getId(), equipo);
                        if ("GANANDO".equals(estadoMarcadorRobo)) {
                            stats.setRobosGanando((stats.getRobosGanando() != null ? stats.getRobosGanando() : 0) + 1);
                        } else if ("EMPATANDO".equals(estadoMarcadorRobo)) {
                            stats.setRobosEmpatando((stats.getRobosEmpatando() != null ? stats.getRobosEmpatando() : 0) + 1);
                        } else if ("PERDIENDO".equals(estadoMarcadorRobo)) {
                            stats.setRobosPerdiendo((stats.getRobosPerdiendo() != null ? stats.getRobosPerdiendo() : 0) + 1);
                        }
                    }
                }
            }
        }
        
        // Calcular tiros recibidos del rival (PARADAS + GOLES_RIVAL)
        // Inicializar contadores de tiros recibidos
        stats.setTirosRecibidos0_15(0);
        stats.setTirosRecibidos16_30(0);
        stats.setTirosRecibidos31_45(0);
        stats.setTirosRecibidos46_60(0);
        stats.setTirosRecibidos61_75(0);
        stats.setTirosRecibidos76_90(0);
        
        // Para cada partido del equipo, procesar paradas y goles del rival
        for (Partido partido : partidos) {
            // Obtener todos los eventos del partido
            List<EventoJugador> eventosPartido = eventoJugadorRepository.findByPartido_Id(partido.getId());
            
            // Contar paradas del portero de nuestro equipo (tiros que fueron parados)
            for (EventoJugador evento : eventosPartido) {
                String tipo = evento.getTipoEvento().toUpperCase();
                Integer minuto = evento.getMinuto();
                
                // Solo contar paradas de jugadores de nuestro equipo
                if (tipo.equals("PARADA") && minuto != null && 
                    evento.getJugador().getEquipo().getId().equals(equipoId)) {
                    
                    if (minuto >= 0 && minuto <= 15) {
                        stats.setTirosRecibidos0_15(stats.getTirosRecibidos0_15() + 1);
                    } else if (minuto >= 16 && minuto <= 30) {
                        stats.setTirosRecibidos16_30(stats.getTirosRecibidos16_30() + 1);
                    } else if (minuto >= 31 && minuto <= 45) {
                        stats.setTirosRecibidos31_45(stats.getTirosRecibidos31_45() + 1);
                    } else if (minuto >= 46 && minuto <= 60) {
                        stats.setTirosRecibidos46_60(stats.getTirosRecibidos46_60() + 1);
                    } else if (minuto >= 61 && minuto <= 75) {
                        stats.setTirosRecibidos61_75(stats.getTirosRecibidos61_75() + 1);
                    } else if (minuto >= 76 && minuto <= 90) {
                        stats.setTirosRecibidos76_90(stats.getTirosRecibidos76_90() + 1);
                    }
                }
            }
            
            // Contar goles del rival (son tiros recibidos que no fueron parados)
            logger.info("🔍 PARTIDO ID: {} - GolesRival: {} - Eventos totales: {}", partido.getId(), partido.getGolesRival(), eventosPartido.size());
            if (partido.getGolesRival() != null && partido.getGolesRival() > 0) {
                logger.info("🔍 Procesando goles del rival para partido ID: {} - Total goles rival: {}", partido.getId(), partido.getGolesRival());
                // Buscar eventos de gol del rival para saber en qué minutos ocurrieron
                List<EventoJugador> golesEventos = new ArrayList<>();
                for (EventoJugador evento : eventosPartido) {
                    String tipo = evento.getTipoEvento().toUpperCase();
                    logger.debug("  📝 Evento: tipo={}, minuto={}, jugador={}", tipo, evento.getMinuto(), (evento.getJugador() != null ? evento.getJugador().getId() : "null"));
                    // GOL_RIVAL tiene jugador null, o GOL de jugadores que NO son de nuestro equipo
                    if (tipo.equals("GOL_RIVAL")) {
                        logger.info("    ✅ GOL_RIVAL detectado en minuto: {}", evento.getMinuto());
                        golesEventos.add(evento);
                    } else if (tipo.equals("GOL") && evento.getJugador() != null && 
                               !evento.getJugador().getEquipo().getId().equals(equipoId)) {
                        logger.info("    ✅ GOL del rival detectado en minuto: {}", evento.getMinuto());
                        golesEventos.add(evento);
                    }
                }
                
                logger.info("  🎯 Total eventos de gol del rival encontrados: {}", golesEventos.size());
                // Si encontramos eventos de gol con minuto, usarlos
                if (!golesEventos.isEmpty()) {
                    for (EventoJugador golEvento : golesEventos) {
                        Integer minuto = golEvento.getMinuto();
                        logger.info("    ⏱️ Procesando gol en minuto: {}", minuto);
                        if (minuto != null) {
                            if (minuto >= 0 && minuto <= 15) {
                                logger.info("      → Asignado a intervalo 0-15");
                                stats.setTirosRecibidos0_15(stats.getTirosRecibidos0_15() + 1);
                            } else if (minuto >= 16 && minuto <= 30) {
                                logger.info("      → Asignado a intervalo 16-30");
                                stats.setTirosRecibidos16_30(stats.getTirosRecibidos16_30() + 1);
                            } else if (minuto >= 31 && minuto <= 45) {
                                logger.info("      → Asignado a intervalo 31-45");
                                stats.setTirosRecibidos31_45(stats.getTirosRecibidos31_45() + 1);
                            } else if (minuto >= 46 && minuto <= 60) {
                                logger.info("      → Asignado a intervalo 46-60");
                                stats.setTirosRecibidos46_60(stats.getTirosRecibidos46_60() + 1);
                            } else if (minuto >= 61 && minuto <= 75) {
                                logger.info("      → Asignado a intervalo 61-75");
                                stats.setTirosRecibidos61_75(stats.getTirosRecibidos61_75() + 1);
                            } else if (minuto >= 76 && minuto <= 90) {
                                logger.info("      → Asignado a intervalo 76-90");
                                stats.setTirosRecibidos76_90(stats.getTirosRecibidos76_90() + 1);
                            }
                        }
                    }
                } else {
                    logger.warn("  ⚠️ No se encontraron eventos, distribuyendo uniformemente");
                    // Si no hay eventos de gol con minuto, distribuir proporcionalmente
                    // basándonos en que el partido duró 90 minutos
                    int golesRival = partido.getGolesRival();
                    // Distribuir uniformemente (cada intervalo tiene la misma probabilidad)
                    int golesPorIntervalo = golesRival / 6;
                    int golesRestantes = golesRival % 6;
                    
                    stats.setTirosRecibidos0_15(stats.getTirosRecibidos0_15() + golesPorIntervalo);
                    stats.setTirosRecibidos16_30(stats.getTirosRecibidos16_30() + golesPorIntervalo);
                    stats.setTirosRecibidos31_45(stats.getTirosRecibidos31_45() + golesPorIntervalo);
                    stats.setTirosRecibidos46_60(stats.getTirosRecibidos46_60() + golesPorIntervalo);
                    stats.setTirosRecibidos61_75(stats.getTirosRecibidos61_75() + golesPorIntervalo);
                    stats.setTirosRecibidos76_90(stats.getTirosRecibidos76_90() + golesPorIntervalo + golesRestantes);
                }
            }
        }
        
        // Calcular total de tiros recibidos (suma de todos los intervalos)
        stats.setTotalTirosRecibidos(
            stats.getTirosRecibidos0_15() +
            stats.getTirosRecibidos16_30() +
            stats.getTirosRecibidos31_45() +
            stats.getTirosRecibidos46_60() +
            stats.getTirosRecibidos61_75() +
            stats.getTirosRecibidos76_90()
        );
        
        // Calcular métricas
        stats.calcularMetricas();
        
        // Calcular mayor pasador del equipo
        String mayorPasador = calcularMayorPasador(equipoId, temporada);
        stats.setMayorPasador(mayorPasador);
        
        // Calcular mayor tirador del equipo
        String mayorTirador = calcularMayorTirador(equipoId, temporada);
        stats.setMayorTirador(mayorTirador);
        
        // Calcular mayor recuperador del equipo
        String mayorRecuperador = calcularMayorRecuperador(equipoId, temporada);
        stats.setMayorRecuperador(mayorRecuperador);
        
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
     * Calcula el jugador con más tiros a puerta del equipo
     */
    private String calcularMayorTirador(Long equipoId, String temporada) {
        System.out.println("🎯 Buscando estadísticas de tiradores - Equipo: " + equipoId + ", Temporada: " + temporada);
        List<EstadisticasJugador> estadisticasJugadores = estadisticasJugadorRepository
            .findByJugador_Equipo_IdAndTemporada(equipoId, temporada);
        
        System.out.println("📊 Jugadores encontrados: " + estadisticasJugadores.size());
        
        if (estadisticasJugadores.isEmpty()) {
            System.out.println("⚠️ No hay jugadores con estadísticas");
            return "N/A";
        }
        
        // Mostrar tiros a puerta de cada jugador
        for (EstadisticasJugador stats : estadisticasJugadores) {
            System.out.println("  - " + stats.getJugador().getNombre() + " " + stats.getJugador().getApellido() + ": " + stats.getTotalTirosAPuerta() + " tiros a puerta");
        }
        
        // Encontrar el jugador con más tiros a puerta
        EstadisticasJugador mejorTirador = estadisticasJugadores.stream()
            .filter(stats -> stats.getTotalTirosAPuerta() != null && stats.getTotalTirosAPuerta() > 0)
            .max((s1, s2) -> {
                int tiros1 = s1.getTotalTirosAPuerta() != null ? s1.getTotalTirosAPuerta() : 0;
                int tiros2 = s2.getTotalTirosAPuerta() != null ? s2.getTotalTirosAPuerta() : 0;
                return Integer.compare(tiros1, tiros2);
            })
            .orElse(null);
        
        if (mejorTirador == null || mejorTirador.getTotalTirosAPuerta() == 0) {
            return "N/A";
        }
        
        // Retornar nombre completo del jugador
        Jugador jugador = mejorTirador.getJugador();
        return jugador.getNombre() + " " + jugador.getApellido() + " (" + mejorTirador.getTotalTirosAPuerta() + ")";
    }
    
    /**
     * Calcula el jugador con más robos del equipo
     */
    private String calcularMayorRecuperador(Long equipoId, String temporada) {
        System.out.println("🛡️ Buscando estadísticas de jugadores para robos - Equipo: " + equipoId + ", Temporada: " + temporada);
        List<EstadisticasJugador> estadisticasJugadores = estadisticasJugadorRepository
            .findByJugador_Equipo_IdAndTemporada(equipoId, temporada);
        
        System.out.println("📊 Jugadores encontrados: " + estadisticasJugadores.size());
        
        if (estadisticasJugadores.isEmpty()) {
            System.out.println("⚠️ No hay jugadores con estadísticas");
            return "N/A";
        }
        
        // Buscar el jugador con más robos
        EstadisticasJugador mejorRecuperador = estadisticasJugadores.stream()
            .filter(e -> e.getTotalRobos() != null && e.getTotalRobos() > 0)
            .max((e1, e2) -> {
                int robos1 = e1.getTotalRobos() != null ? e1.getTotalRobos() : 0;
                int robos2 = e2.getTotalRobos() != null ? e2.getTotalRobos() : 0;
                return Integer.compare(robos1, robos2);
            })
            .orElse(null);
        
        if (mejorRecuperador == null || mejorRecuperador.getTotalRobos() == null || mejorRecuperador.getTotalRobos() == 0) {
            System.out.println("⚠️ No hay jugadores con robos registrados");
            return "N/A";
        }
        
        // Retornar nombre completo del jugador con cantidad de robos
        Jugador jugador = mejorRecuperador.getJugador();
        String resultado = jugador.getNombre() + " " + jugador.getApellido() + " (" + mejorRecuperador.getTotalRobos() + " robos)";
        System.out.println("🏆 Mayor recuperador: " + resultado);
        return resultado;
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
        dto.setTotalTirosAPuerta(stats.getTotalTirosAPuerta());
        dto.setTirosAPuerta0_15(stats.getTirosAPuerta0_15());
        dto.setTirosAPuerta16_30(stats.getTirosAPuerta16_30());
        dto.setTirosAPuerta31_45(stats.getTirosAPuerta31_45());
        dto.setTirosAPuerta46_60(stats.getTirosAPuerta46_60());
        dto.setTirosAPuerta61_75(stats.getTirosAPuerta61_75());
        dto.setTirosAPuerta76_90(stats.getTirosAPuerta76_90());
        dto.setTirosAPuertaGanando(stats.getTirosAPuertaGanando());
        dto.setTirosAPuertaEmpatando(stats.getTirosAPuertaEmpatando());
        dto.setTirosAPuertaPerdiendo(stats.getTirosAPuertaPerdiendo());
        dto.setTirosAPuertaP90(stats.getTirosAPuertaP90());
        dto.setTotalRobos(stats.getTotalRobos());
        dto.setRobos0_15(stats.getRobos0_15());
        dto.setRobos16_30(stats.getRobos16_30());
        dto.setRobos31_45(stats.getRobos31_45());
        dto.setRobos46_60(stats.getRobos46_60());
        dto.setRobos61_75(stats.getRobos61_75());
        dto.setRobos76_90(stats.getRobos76_90());
        dto.setRobosGanando(stats.getRobosGanando());
        dto.setRobosEmpatando(stats.getRobosEmpatando());
        dto.setRobosPerdiendo(stats.getRobosPerdiendo());
        dto.setRobosP90(stats.getRobosP90());
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
        dto.setTotalTirosAPuerta(stats.getTotalTirosAPuerta());
        dto.setTirosAPuerta0_15(stats.getTirosAPuerta0_15());
        dto.setTirosAPuerta16_30(stats.getTirosAPuerta16_30());
        dto.setTirosAPuerta31_45(stats.getTirosAPuerta31_45());
        dto.setTirosAPuerta46_60(stats.getTirosAPuerta46_60());
        dto.setTirosAPuerta61_75(stats.getTirosAPuerta61_75());
        dto.setTirosAPuerta76_90(stats.getTirosAPuerta76_90());
        dto.setTirosAPuertaGanando(stats.getTirosAPuertaGanando());
        dto.setTirosAPuertaEmpatando(stats.getTirosAPuertaEmpatando());
        dto.setTirosAPuertaPerdiendo(stats.getTirosAPuertaPerdiendo());
        dto.setTirosAPuertaP90(stats.getTirosAPuertaP90());
        dto.setMayorTirador(stats.getMayorTirador());
        dto.setTotalTirosRecibidos(stats.getTotalTirosRecibidos());
        dto.setTirosRecibidos0_15(stats.getTirosRecibidos0_15());
        dto.setTirosRecibidos16_30(stats.getTirosRecibidos16_30());
        dto.setTirosRecibidos31_45(stats.getTirosRecibidos31_45());
        dto.setTirosRecibidos46_60(stats.getTirosRecibidos46_60());
        dto.setTirosRecibidos61_75(stats.getTirosRecibidos61_75());
        dto.setTirosRecibidos76_90(stats.getTirosRecibidos76_90());
        dto.setTotalRobos(stats.getTotalRobos());
        dto.setRobos0_15(stats.getRobos0_15());
        dto.setRobos16_30(stats.getRobos16_30());
        dto.setRobos31_45(stats.getRobos31_45());
        dto.setRobos46_60(stats.getRobos46_60());
        dto.setRobos61_75(stats.getRobos61_75());
        dto.setRobos76_90(stats.getRobos76_90());
        dto.setRobosGanando(stats.getRobosGanando());
        dto.setRobosEmpatando(stats.getRobosEmpatando());
        dto.setRobosPerdiendo(stats.getRobosPerdiendo());
        dto.setRobosP90(stats.getRobosP90());
        dto.setMayorRecuperador(stats.getMayorRecuperador());
        dto.setPromedioGolesFavor(stats.getPromedioGolesFavor());
        dto.setPromedioGolesContra(stats.getPromedioGolesContra());
        dto.setEfectividad(stats.getEfectividad());
        
        return dto;
    }
    
    // ========== ESTADÍSTICAS DE PARTIDO INDIVIDUAL ==========
    
    @Override
    public EstadisticasPartidoDTO obtenerEstadisticasPartido(Long partidoId) {
        // Obtener el partido
        Partido partido = partidoRepository.findById(partidoId)
            .orElseThrow(() -> new RuntimeException("Partido no encontrado con id: " + partidoId));
        
        // Obtener todos los eventos del partido
        List<EventoJugador> eventos = eventoJugadorRepository.findByPartido_Id(partidoId);
        
        // Crear el DTO
        EstadisticasPartidoDTO dto = new EstadisticasPartidoDTO();
        dto.setId(partido.getId());
        dto.setEquipoId(partido.getEquipo().getId());
        dto.setEquipoNombre(partido.getEquipo().getNombre());
        dto.setFecha(partido.getFecha());
        dto.setTitulo(partido.getTitulo());
        dto.setDuracion(partido.getDuracion());
        dto.setResultado(partido.getResultado());
        dto.setGolesEquipo(partido.getGolesEquipo() != null ? partido.getGolesEquipo() : 0);
        dto.setGolesRival(partido.getGolesRival() != null ? partido.getGolesRival() : 0);
        
        // Agrupar eventos por jugador
        Map<Long, EstadisticasPartidoDTO.EventoJugadorResumen> eventosPorJugador = new HashMap<>();
        
        // Contadores totales
        int totalGoles = 0, totalAsistencias = 0, totalPasesClave = 0;
        int totalTarjetasAmarillas = 0, totalTarjetasRojas = 0;
        
        // Distribuciones temporales
        EstadisticasPartidoDTO.DistribucionTemporal distGoles = new EstadisticasPartidoDTO.DistribucionTemporal();
        EstadisticasPartidoDTO.DistribucionTemporal distAsistencias = new EstadisticasPartidoDTO.DistribucionTemporal();
        EstadisticasPartidoDTO.DistribucionTemporal distTarjetas = new EstadisticasPartidoDTO.DistribucionTemporal();
        EstadisticasPartidoDTO.DistribucionTemporal distTirosRecibidos = new EstadisticasPartidoDTO.DistribucionTemporal();
        
        // Inicializar distribuciones
        distGoles.setIntervalo0_15(0);
        distGoles.setIntervalo16_30(0);
        distGoles.setIntervalo31_45(0);
        distGoles.setIntervalo46_60(0);
        distGoles.setIntervalo61_75(0);
        distGoles.setIntervalo76_90(0);
        
        distAsistencias.setIntervalo0_15(0);
        distAsistencias.setIntervalo16_30(0);
        distAsistencias.setIntervalo31_45(0);
        distAsistencias.setIntervalo46_60(0);
        distAsistencias.setIntervalo61_75(0);
        distAsistencias.setIntervalo76_90(0);
        
        distTarjetas.setIntervalo0_15(0);
        distTarjetas.setIntervalo16_30(0);
        distTarjetas.setIntervalo31_45(0);
        distTarjetas.setIntervalo46_60(0);
        distTarjetas.setIntervalo61_75(0);
        distTarjetas.setIntervalo76_90(0);
        
        distTirosRecibidos.setIntervalo0_15(0);
        distTirosRecibidos.setIntervalo16_30(0);
        distTirosRecibidos.setIntervalo31_45(0);
        distTirosRecibidos.setIntervalo46_60(0);
        distTirosRecibidos.setIntervalo61_75(0);
        distTirosRecibidos.setIntervalo76_90(0);
        
        // Procesar cada evento
        for (EventoJugador evento : eventos) {
            String tipoEvento = evento.getTipoEvento().toUpperCase();
            Integer minuto = evento.getMinuto() != null ? evento.getMinuto() : 0;
            
            // Solo procesar eventos con jugador asignado (no GOL_RIVAL)
            if (evento.getJugador() != null) {
                Long jugadorId = evento.getJugador().getId();
                
                // Obtener o crear resumen del jugador
                EstadisticasPartidoDTO.EventoJugadorResumen resumen = eventosPorJugador.get(jugadorId);
                if (resumen == null) {
                    resumen = new EstadisticasPartidoDTO.EventoJugadorResumen();
                    resumen.setJugadorId(jugadorId);
                    resumen.setJugadorNombre(evento.getJugador().getNombre());
                    resumen.setGoles(0);
                    resumen.setAsistencias(0);
                    resumen.setPasesClave(0);
                    resumen.setTarjetasAmarillas(0);
                    resumen.setTarjetasRojas(0);
                    resumen.setRobos(0);
                    resumen.setTirosAPuerta(0);
                    eventosPorJugador.put(jugadorId, resumen);
                }
                
                // Contar evento según tipo
                switch (tipoEvento) {
                    case "GOL":
                        resumen.setGoles(resumen.getGoles() + 1);
                        totalGoles++;
                        incrementarIntervalo(distGoles, minuto);
                        break;
                    case "ASISTENCIA":
                        resumen.setAsistencias(resumen.getAsistencias() + 1);
                        totalAsistencias++;
                        incrementarIntervalo(distAsistencias, minuto);
                        break;
                    case "PASE_CLAVE":
                        resumen.setPasesClave(resumen.getPasesClave() + 1);
                        totalPasesClave++;
                        break;
                    case "TARJETA_AMARILLA":
                        resumen.setTarjetasAmarillas(resumen.getTarjetasAmarillas() + 1);
                        totalTarjetasAmarillas++;
                        incrementarIntervalo(distTarjetas, minuto);
                        break;
                    case "TARJETA_ROJA":
                        resumen.setTarjetasRojas(resumen.getTarjetasRojas() + 1);
                        totalTarjetasRojas++;
                        incrementarIntervalo(distTarjetas, minuto);
                        break;
                    case "ROBO":
                        resumen.setRobos(resumen.getRobos() + 1);
                        break;
                    case "TIRO_A_PUERTA":
                        resumen.setTirosAPuerta(resumen.getTirosAPuerta() + 1);
                        break;
                }
            } else if (tipoEvento.equals("GOL_RIVAL")) {
                // Procesar goles del rival
                incrementarIntervalo(distTirosRecibidos, minuto);
            }
        }
        
        // Asignar totales
        dto.setTotalGoles(totalGoles);
        dto.setTotalAsistencias(totalAsistencias);
        dto.setTotalPasesClave(totalPasesClave);
        dto.setTotalTarjetasAmarillas(totalTarjetasAmarillas);
        dto.setTotalTarjetasRojas(totalTarjetasRojas);
        dto.setTirosRecibidos(dto.getGolesRival());
        
        // Asignar distribuciones
        dto.setDistribucionGoles(distGoles);
        dto.setDistribucionAsistencias(distAsistencias);
        dto.setDistribucionTarjetas(distTarjetas);
        dto.setDistribucionTirosRecibidos(distTirosRecibidos);
        
        // Convertir map a lista
        dto.setEventosPorJugador(new ArrayList<>(eventosPorJugador.values()));
        
        return dto;
    }
    
    /**
     * Helper method to increment the correct time interval
     */
    private void incrementarIntervalo(EstadisticasPartidoDTO.DistribucionTemporal dist, int minuto) {
        if (minuto <= 15) {
            dist.setIntervalo0_15(dist.getIntervalo0_15() + 1);
        } else if (minuto <= 30) {
            dist.setIntervalo16_30(dist.getIntervalo16_30() + 1);
        } else if (minuto <= 45) {
            dist.setIntervalo31_45(dist.getIntervalo31_45() + 1);
        } else if (minuto <= 60) {
            dist.setIntervalo46_60(dist.getIntervalo46_60() + 1);
        } else if (minuto <= 75) {
            dist.setIntervalo61_75(dist.getIntervalo61_75() + 1);
        } else {
            dist.setIntervalo76_90(dist.getIntervalo76_90() + 1);
        }
    }
}
