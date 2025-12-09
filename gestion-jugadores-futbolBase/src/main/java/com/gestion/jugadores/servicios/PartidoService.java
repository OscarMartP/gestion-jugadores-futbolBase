package com.gestion.jugadores.servicios;
import com.gestion.jugadores.modelo.Partido;
import java.util.List;

public interface PartidoService {
	Partido crearPartido(Partido partido);
    List<Partido> obtenerPartidosPorEquipo(Long equipoId);
    Partido obtenerPartidoPorId(Long id);

    List<Partido> obtenerPartidosActivosPorEquipo(Long equipoId);
    
    List<Partido> obtenerPartidosActivos();
    
    Partido activarPartido(Long id);
    
    Partido desactivarPartido(Long id);
    
    Boolean tienePartidoActivo(Long equipoId);
    
    Partido actualizarPartido(Long id, Partido partidoActualizado);
    
    void eliminarPartido(Long id);

}
