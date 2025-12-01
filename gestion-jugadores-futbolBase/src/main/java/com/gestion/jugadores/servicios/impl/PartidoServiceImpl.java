package com.gestion.jugadores.servicios.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.repositorio.PartidoRepository;
import com.gestion.jugadores.servicios.PartidoService;

import java.util.List;

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
}
