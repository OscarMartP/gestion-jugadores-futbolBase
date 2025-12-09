package com.gestion.jugadores.servicios.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.repositorio.PartidoRepository;
import com.gestion.jugadores.servicios.PartidoService;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidoServiceImpl implements PartidoService {

    @Autowired
    private PartidoRepository partidoRepository;

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
    public Partido desactivarPartido(Long id) {
        Partido partido = partidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con id: " + id));
        partido.setPartidoActivo(false);
        return partidoRepository.save(partido);
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
}
