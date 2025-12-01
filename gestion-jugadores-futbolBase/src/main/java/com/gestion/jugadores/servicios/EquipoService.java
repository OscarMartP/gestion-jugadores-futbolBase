package com.gestion.jugadores.servicios;

import java.util.List;

import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Jugador;

public interface EquipoService {
    Equipo registrarEquipo(Equipo equipo, Long userId);
    List<Equipo> obtenerEquiposPorUsuario(Long usuarioId);
    Equipo obtenerEquipoDelUsuario(Long equipoId, Long usuarioId);
    Equipo actualizarDuracionPartido(Long equipoId, Long usuarioId, Integer nuevaDuracion);
    Equipo obtenerEquipoPorId(Long equipoId);
}
