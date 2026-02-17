package com.gestion.jugadores.servicios.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gestion.jugadores.excepciones.ValidacionException;
import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.modelo.Usuario;
import com.gestion.jugadores.repositorio.AnalisisJugadorRepository;
import com.gestion.jugadores.repositorio.EquipoRepository;
import com.gestion.jugadores.repositorio.EstadisticasEquipoRepository;
import com.gestion.jugadores.repositorio.EventoJugadorRepository;
import com.gestion.jugadores.repositorio.PartidoRepository;
import com.gestion.jugadores.repositorio.UsuarioRepository;
import com.gestion.jugadores.servicios.EquipoService;

@Service
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AnalisisJugadorRepository analisisJugadorRepository;
    private final EstadisticasEquipoRepository estadisticasEquipoRepository;
    private final PartidoRepository partidoRepository;
    private final EventoJugadorRepository eventoJugadorRepository;

    @Autowired
    public EquipoServiceImpl(EquipoRepository equipoRepository, 
                              UsuarioRepository usuarioRepository,
                              AnalisisJugadorRepository analisisJugadorRepository,
                              EstadisticasEquipoRepository estadisticasEquipoRepository,
                              PartidoRepository partidoRepository,
                              EventoJugadorRepository eventoJugadorRepository) {
        this.equipoRepository = equipoRepository;
        this.usuarioRepository = usuarioRepository;
        this.analisisJugadorRepository = analisisJugadorRepository;
        this.estadisticasEquipoRepository = estadisticasEquipoRepository;
        this.partidoRepository = partidoRepository;
        this.eventoJugadorRepository = eventoJugadorRepository;
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

    @Override
    public Equipo actualizarEquipo(Long equipoId, Equipo equipoActualizado) {
        Equipo equipo = equipoRepository.findById(equipoId)
            .orElseThrow(() -> new ValidacionException("Equipo no encontrado"));
        
        if (StringUtils.hasText(equipoActualizado.getNombre())) {
            equipo.setNombre(equipoActualizado.getNombre());
        }
        
        if (equipoActualizado.getTipoFutbol() != null) {
            equipo.setTipoFutbol(equipoActualizado.getTipoFutbol());
        }
        
        if (equipoActualizado.getDuracionPartido() != null) {
            if (equipoActualizado.getDuracionPartido() <= 0 || equipoActualizado.getDuracionPartido() > 120) {
                throw new ValidacionException("Duración inválida (1-120 minutos)");
            }
            equipo.setDuracionPartido(equipoActualizado.getDuracionPartido());
        }
        
        return equipoRepository.save(equipo);
    }

    @Override
    public void eliminarEquipo(Long equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
            .orElseThrow(() -> new ValidacionException("Equipo no encontrado"));
        
        // 1. Eliminar eventos de los partidos del equipo
        List<Partido> partidos = partidoRepository.findByEquipo_Id(equipoId);
        for (Partido partido : partidos) {
            eventoJugadorRepository.deleteAll(
                eventoJugadorRepository.findByPartido_Id(partido.getId())
            );
        }
        
        // 2. Eliminar todos los partidos del equipo
        partidoRepository.deleteAll(partidos);
        
        // 3. Eliminar estadísticas del equipo (todas las temporadas)
        estadisticasEquipoRepository.deleteAll(
            estadisticasEquipoRepository.findByEquipo_Id(equipoId)
        );
        
        // 4. Eliminar análisis de IA de los jugadores del equipo
        if (equipo.getJugadores() != null && !equipo.getJugadores().isEmpty()) {
            for (Jugador jugador : equipo.getJugadores()) {
                analisisJugadorRepository.deleteAll(
                    analisisJugadorRepository.findByJugadorIdOrderByFechaGeneracionDesc(jugador.getId())
                );
            }
        }
        
        // 5. Al eliminar el equipo, los jugadores asociados se eliminan en cascada
        // gracias a la configuración CascadeType.ALL en la entidad Equipo
        equipoRepository.delete(equipo);
    }
} 
