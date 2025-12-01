package com.gestion.jugadores.servicios;
import com.gestion.jugadores.modelo.Partido;
import java.util.List;

public interface PartidoService {
	Partido crearPartido(Partido partido);
    List<Partido> obtenerPartidosPorEquipo(Long equipoId);
    Partido obtenerPartidoPorId(Long id);
}
