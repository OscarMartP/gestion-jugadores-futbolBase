package com.gestion.jugadores.servicios.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.modelo.EventoJugador;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.repositorio.PartidoRepository;
import com.gestion.jugadores.repositorio.EventoJugadorRepository;
import com.gestion.jugadores.repositorio.JugadorRepositorio;
import com.gestion.jugadores.servicios.PartidoService;
import com.gestion.jugadores.servicios.EstadisticasService;
import com.gestion.jugadores.servicios.JugadorService;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PartidoServiceImpl implements PartidoService {

    private static final Logger logger = LoggerFactory.getLogger(PartidoServiceImpl.class);

    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private EstadisticasService estadisticasService;
    
    @Autowired
    private JugadorService jugadorService;
    
    @Autowired
    private EventoJugadorRepository eventoJugadorRepository;
    
    @Autowired
    private JugadorRepositorio jugadorRepositorio;

    @Override
    public Partido crearPartido(Partido partido) {
        return partidoRepository.save(partido);
    }

    @Override
    public List<Partido> obtenerPartidosPorEquipo(Long equipoId) {
        return partidoRepository.findByEquipo_Id(equipoId); // CAMBIO
    }
    
    public Partido obtenerPartidoPorId(Long id) {
        return partidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con ID: " + id));
    }

    @Override
    public List<Partido> obtenerPartidosActivosPorEquipo(Long equipoId) {
        return partidoRepository.findByEquipoIdAndPartidoActivo(equipoId, true);
    }
    
    @Override
    public List<Partido> obtenerPartidosActivos() {
        return partidoRepository.findByPartidoActivo(true);
    }
    
    @Override
    @Transactional
    public Partido activarPartido(Long id) {
        Partido partido = partidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));

        // Si el partido ya está activo, no hacer nada
        if (partido.getPartidoActivo()) {
            return partido;
        }

        // Desactivar en bloque otros partidos activos del mismo equipo (bulk update)
        Long equipoId = partido.getEquipo().getId();
        partidoRepository.deactivateOtherActiveByEquipoId(equipoId, id);

        // Activar el partido solicitado
        partido.setPartidoActivo(true);
        return partidoRepository.save(partido);
    }
    
    @Override
    @Transactional
    public Partido desactivarPartido(Long id) {
        logger.info("Desactivando partido con id: {}", id);
        
        Partido partido = partidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));
        
        // Calcular minutos jugados para todos los jugadores ANTES de finalizar
        try {
            calcularMinutosJugados(partido);
            logger.info("Minutos jugados calculados correctamente para partido {}", id);
        } catch (Exception e) {
            logger.error("Error al calcular minutos jugados: {}", e.getMessage());
        }
        
        // Contar goles desde los eventos registrados
        try {
            List<EventoJugador> eventos = eventoJugadorRepository.findByPartido_Id(id);
            
            long golesEquipo = eventos.stream()
                .filter(e -> e.getTipoEvento() != null && e.getTipoEvento().toUpperCase().equals("GOL"))
                .count();
            
            long golesRival = eventos.stream()
                .filter(e -> e.getTipoEvento() != null && e.getTipoEvento().toUpperCase().equals("GOL_RIVAL"))
                .count();
            
            partido.setGolesEquipo((int) golesEquipo);
            partido.setGolesRival((int) golesRival);
            
            logger.info("Goles contados desde eventos - Equipo: {}, Rival: {} para partido {}", golesEquipo, golesRival, id);
        } catch (Exception e) {
            logger.error("Error al contar goles desde eventos: {}", e.getMessage());
        }
        
        // Calcular resultado del partido basándose en goles
        if (partido.getGolesEquipo() != null && partido.getGolesRival() != null) {
            if (partido.getGolesEquipo() > partido.getGolesRival()) {
                partido.setResultado("VICTORIA");
            } else if (partido.getGolesEquipo() < partido.getGolesRival()) {
                partido.setResultado("DERROTA");
            } else {
                partido.setResultado("EMPATE");
            }
            logger.info("Resultado del partido {}: {}", id, partido.getResultado());
        } else {
            logger.warn("No se pudo calcular resultado para partido {} - goles no definidos", id);
        }
        
        partido.setPartidoActivo(false);
        Partido partidoFinalizado = partidoRepository.save(partido);
        
        logger.info("Partido {} finalizado correctamente", id);
        
        // Actualizar estadísticas automáticamente
        try {
            String temporadaActual = obtenerTemporadaActual();
            Long equipoId = partido.getEquipo().getId();
            
            logger.info("Actualizando estadísticas del equipo {} para temporada {}", equipoId, temporadaActual);
            
            // Actualizar estadísticas de cada jugador que participó en el partido
            List<com.gestion.jugadores.modelo.Jugador> jugadores = jugadorService.obtenerPorEquipo(equipoId);
            for (com.gestion.jugadores.modelo.Jugador jugador : jugadores) {
                try {
                    logger.info("Actualizando estadísticas del jugador {} {} para temporada {}", 
                        jugador.getNombre(), jugador.getApellido(), temporadaActual);
                    estadisticasService.actualizarEstadisticasJugador(jugador.getId(), temporadaActual);
                } catch (Exception e) {
                    logger.error("Error al actualizar estadísticas del jugador {}: {}", jugador.getId(), e.getMessage());
                }
            }
            
            // Luego actualizar estadísticas del equipo (agregadas)
            estadisticasService.actualizarEstadisticasEquipo(equipoId, temporadaActual);
            
            logger.info("Estadísticas actualizadas correctamente para equipo {}", equipoId);
        } catch (Exception e) {
            logger.error("Error al actualizar estadísticas después de finalizar partido {}: {}", id, e.getMessage());
            // No lanzamos excepción para no revertir la transacción del partido
            // Las estadísticas se pueden actualizar manualmente después
        }
        
        return partidoFinalizado;
    }

    @Override
    public Boolean tienePartidoActivo(Long equipoId) {
        List<Partido> partidosActivos = partidoRepository.findByEquipoIdAndPartidoActivo(equipoId, true);
        return !partidosActivos.isEmpty();
    }
    
    @Override
    public Partido actualizarPartido(Long id, Partido partidoActualizado) {
        Partido partido = partidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));
        partido.setFecha(partidoActualizado.getFecha());
        partido.setDuracion(partidoActualizado.getDuracion());
        partido.setEquipo(partidoActualizado.getEquipo());
        
        // Actualizar goles si están presentes
        if (partidoActualizado.getGolesEquipo() != null) {
            partido.setGolesEquipo(partidoActualizado.getGolesEquipo());
        }
        if (partidoActualizado.getGolesRival() != null) {
            partido.setGolesRival(partidoActualizado.getGolesRival());
        }
        
        // Calcular resultado si ambos goles están definidos y el partido está finalizado
        if (partido.getGolesEquipo() != null && partido.getGolesRival() != null && !partido.getPartidoActivo()) {
            if (partido.getGolesEquipo() > partido.getGolesRival()) {
                partido.setResultado("VICTORIA");
            } else if (partido.getGolesEquipo() < partido.getGolesRival()) {
                partido.setResultado("DERROTA");
            } else {
                partido.setResultado("EMPATE");
            }
            logger.info("Resultado actualizado para partido {}: {}", id, partido.getResultado());
            
            // Actualizar estadísticas
            try {
                String temporadaActual = obtenerTemporadaActual();
                Long equipoId = partido.getEquipo().getId();
                estadisticasService.actualizarEstadisticasEquipo(equipoId, temporadaActual);
                logger.info("Estadísticas actualizadas para equipo {}", equipoId);
            } catch (Exception e) {
                logger.error("Error al actualizar estadísticas: {}", e.getMessage());
            }
        }
        
        // No sobrescribir partidoActivo aquí (usar activarPartido/desactivarPartido)
        return partidoRepository.save(partido);
    }
    
    @Override
    @Transactional
    public void eliminarPartido(Long id) {
        Partido partido = partidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));
        
        // Primero eliminar todos los eventos asociados al partido
        logger.info("Eliminando eventos del partido con id: {}", id);
        List<EventoJugador> eventos = eventoJugadorRepository.findByPartido_Id(id);
        if (!eventos.isEmpty()) {
            logger.info("Se encontraron {} eventos para eliminar", eventos.size());
            eventoJugadorRepository.deleteAll(eventos);
            logger.info("Eventos eliminados exitosamente");
        }
        
        // Ahora eliminar el partido
        logger.info("Eliminando partido con id: {}", id);
        partidoRepository.delete(partido);
        logger.info("Partido eliminado exitosamente");
    }
    
    /**
     * Obtiene la temporada actual en formato YYYY-YYYY+1
     * Por ejemplo: 2024-2025
     * Si estamos antes de julio, la temporada empezó el año anterior
     */
    private String obtenerTemporadaActual() {
        int currentYear = Year.now().getValue();
        int currentMonth = LocalDate.now().getMonthValue();
        
        // Si estamos antes de julio, la temporada empezó el año anterior
        if (currentMonth < 7) {
            return (currentYear - 1) + "-" + currentYear;
        } else {
            return currentYear + "-" + (currentYear + 1);
        }
    }
    
    /**
     * Calcula los minutos jugados para cada jugador en un partido
     * Tiene en cuenta: titulares, suplentes y sustituciones
     */
    private void calcularMinutosJugados(Partido partido) {
        logger.info("Calculando minutos jugados para partido {}", partido.getId());
        
        Integer duracionPartido = partido.getDuracion();
        if (duracionPartido == null || duracionPartido <= 0) {
            duracionPartido = 90; // Default
        }
        
        List<Long> titularesIds = partido.getTitulares();
        List<Long> suplentesIds = partido.getSuplentes();
        
        // Si no hay titulares definidos, no podemos calcular (partidos antiguos)
        if (titularesIds == null || titularesIds.isEmpty()) {
            logger.warn("No hay titulares definidos para partido {}, saltando cálculo de minutos", partido.getId());
            return;
        }
        
        // Obtener todas las sustituciones del partido
        List<EventoJugador> sustituciones = eventoJugadorRepository.findByPartido_IdAndTipoEvento(
            partido.getId(), "sustitucion"
        );
        
        // Mapa para almacenar minutos jugados por jugador
        Map<Long, Integer> minutosMap = new HashMap<>();
        Map<Long, Boolean> fueTitularMap = new HashMap<>();
        
        // Inicializar titulares: jugaron desde el minuto 0
        for (Long jugadorId : titularesIds) {
            minutosMap.put(jugadorId, duracionPartido);
            fueTitularMap.put(jugadorId, true);
        }
        
        // Procesar sustituciones
        for (EventoJugador sustitucion : sustituciones) {
            Long jugadorSale = sustitucion.getJugadorSaleId();
            Long jugadorEntra = sustitucion.getJugadorEntraId();
            Integer minuto = sustitucion.getMinuto();
            
            if (jugadorSale != null && minutosMap.containsKey(jugadorSale)) {
                // El jugador que sale solo jugó hasta este minuto
                minutosMap.put(jugadorSale, minuto);
            }
            
            if (jugadorEntra != null) {
                // El jugador que entra jugó desde este minuto hasta el final
                int minutosJugados = duracionPartido - minuto;
                minutosMap.put(jugadorEntra, minutosJugados);
                fueTitularMap.put(jugadorEntra, false); // Entró como suplente
            }
        }
        
        // Crear o actualizar eventos para cada jugador con sus minutos jugados
        for (Map.Entry<Long, Integer> entry : minutosMap.entrySet()) {
            Long jugadorId = entry.getKey();
            Integer minutos = entry.getValue();
            Boolean fueTitular = fueTitularMap.getOrDefault(jugadorId, false);
            
            try {
                Jugador jugador = jugadorRepositorio.findById(jugadorId).orElse(null);
                if (jugador != null) {
                    // Buscar si ya existe un evento de participación para este jugador
                    List<EventoJugador> eventosJugador = eventoJugadorRepository.findByJugador_IdAndPartido_Id(
                        jugadorId, partido.getId()
                    );
                    
                    // Si no tiene eventos, crear uno de "participacion"
                    if (eventosJugador.isEmpty()) {
                        EventoJugador evento = new EventoJugador();
                        evento.setJugador(jugador);
                        evento.setPartido(partido);
                        evento.setTipoEvento("participacion");
                        evento.setMinuto(0);
                        evento.setFueTitular(fueTitular);
                        evento.setMinutosJugados(minutos);
                        eventoJugadorRepository.save(evento);
                        logger.info("Evento de participación creado para jugador {} con {} minutos", jugadorId, minutos);
                    } else {
                        // Actualizar el primer evento encontrado con los minutos y fue titular
                        EventoJugador primerEvento = eventosJugador.get(0);
                        primerEvento.setFueTitular(fueTitular);
                        primerEvento.setMinutosJugados(minutos);
                        eventoJugadorRepository.save(primerEvento);
                        logger.info("Evento actualizado para jugador {} con {} minutos", jugadorId, minutos);
                    }
                }
            } catch (Exception e) {
                logger.error("Error al guardar minutos para jugador {}: {}", jugadorId, e.getMessage());
            }
        }
        
        logger.info("Cálculo de minutos jugados completado para partido {}", partido.getId());
    }
}
