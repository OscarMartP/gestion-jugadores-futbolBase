package com.gestion.jugadores.servicios.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gestion.jugadores.excepciones.ValidacionException;
import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Usuario;
import com.gestion.jugadores.repositorio.EquipoRepository;
import com.gestion.jugadores.repositorio.UsuarioRepository;
import com.gestion.jugadores.servicios.EquipoService;

@Service
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public EquipoServiceImpl(EquipoRepository equipoRepository, 
                              UsuarioRepository usuarioRepository) {
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Equipo registrarEquipo(Equipo equipo, Long userId) {
        validarDatosEquipo(equipo, userId);

        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new ValidacionException("Usuario no encontrado"));

        equipo.setUsuario(usuario);
        return equipoRepository.save(equipo);
    }

    @Override
    public Equipo registrarEquipoParaUsername(Equipo equipo, String username) {
        if (username == null || username.isEmpty()) {
            throw new ValidacionException("Username inválido");
        }
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new ValidacionException("Usuario no encontrado");
        }
        // Reutilizar validaciones usando el id del usuario
        validarDatosEquipo(equipo, usuario.getId());
        equipo.setUsuario(usuario);
        return equipoRepository.save(equipo);
    }

    @Override
    public List<Equipo> obtenerEquiposPorUsuario(Long usuarioId) {
        validarIdUsuario(usuarioId);
        return equipoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Equipo> obtenerEquiposPorUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new ValidacionException("Username inválido");
        }
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            throw new ValidacionException("Usuario no encontrado");
        }
        return equipoRepository.findByUsuarioId(usuario.getId());
    }

    @Override
    public Equipo obtenerEquipoDelUsuario(Long equipoId, Long usuarioId) {
        validarIdUsuario(usuarioId);
        return equipoRepository.findByIdAndUsuarioId(equipoId, usuarioId)
            .orElseThrow(() -> new ValidacionException("Equipo no encontrado o no pertenece al usuario"));
    }

    @Override
    public Equipo actualizarDuracionPartido(Long equipoId, Long usuarioId, Integer nuevaDuracion) {
        validarIdUsuario(usuarioId);

        if (nuevaDuracion == null || nuevaDuracion <= 0 || nuevaDuracion > 120) {
            throw new ValidacionException("La duración debe ser entre 1 y 120 minutos");
        }

        Equipo equipo = equipoRepository.findByIdAndUsuarioId(equipoId, usuarioId)
            .orElseThrow(() -> new ValidacionException("Equipo no encontrado o no pertenece al usuario"));

        equipo.setDuracionPartido(nuevaDuracion);
        return equipoRepository.save(equipo);
    }

    @Override
    public Equipo obtenerEquipoPorId(Long equipoId) {
        return equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
    }

    // Métodos de validación privados
    private void validarDatosEquipo(Equipo equipo, Long userId) {
        validarIdUsuario(userId);

        if (!StringUtils.hasText(equipo.getNombre())) {
            throw new ValidacionException("El nombre del equipo es obligatorio");
        }

        if (equipo.getDuracionPartido() != null && 
            (equipo.getDuracionPartido() <= 0 || equipo.getDuracionPartido() > 120)) {
            throw new ValidacionException("Duración inválida (1-120 minutos)");
        }

        if (equipoRepository.existsByNombreAndUsuarioId(equipo.getNombre(), userId)) {
            throw new ValidacionException("Ya tienes un equipo con ese nombre");
        }
    }

    private void validarIdUsuario(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidacionException("ID de usuario inválido");
        }
    }
} 
