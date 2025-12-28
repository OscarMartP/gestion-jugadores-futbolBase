package com.gestion.jugadores.servicios.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.repositorio.PartidoRepository;
import com.gestion.jugadores.servicios.PartidoService;
import com.gestion.jugadores.servicios.EstadisticasService;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
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
        partido.setPartidoActivo(false);
        Partido partidoFinalizado = partidoRepository.save(partido);
        
        logger.info("Partido {} finalizado correctamente", id);
        
        // Actualizar estadísticas automáticamente
        try {
            String temporadaActual = obtenerTemporadaActual();
            Long equipoId = partido.getEquipo().getId();
            
            logger.info("Actualizando estadísticas del equipo {} para temporada {}", equipoId, temporadaActual);
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
        // No sobrescribir partidoActivo aquí (usar activarPartido/desactivarPartido)
        return partidoRepository.save(partido);
    }
    
    @Override
    public void eliminarPartido(Long id) {
        Partido partido = partidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));
        partidoRepository.delete(partido);
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
}
