package com.gestion.jugadores.controlador;

import com.gestion.jugadores.controlador.base.BaseController;
import com.gestion.jugadores.controlador.base.BaseService;
import com.gestion.jugadores.modelo.EventoJugador;
import com.gestion.jugadores.dto.EventoJugadorDTO;
import com.gestion.jugadores.modelo.EventoResumenDTO;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.servicios.EventoJugadorService;
import com.gestion.jugadores.servicios.JugadorService;
import com.gestion.jugadores.servicios.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Eventos de Jugador
 * Extiende BaseController para heredar operaciones CRUD comunes
 * Solo implementa endpoints específicos de EventoJugador
 */
@RestController
@RequestMapping("/api/v1/eventos")
@CrossOrigin(origins = "http://localhost:4200")
public class EventoJugadorControladorV2 extends BaseController<EventoJugador, EventoJugadorDTO, Long> {

    @Autowired
    private EventoJugadorService eventoJugadorService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private PartidoService partidoService;

    @Override
    protected BaseService<EventoJugador, Long> getService() {
        return new BaseService<EventoJugador, Long>() {
            @Override
            public EventoJugador findById(Long id) {
                // Si EventoJugadorService no tiene método findById, implementarlo
                return eventoJugadorService.obtenerEventoPorId(id);
            }

            @Override
            public List<EventoJugador> findAll() {
                // Si EventoJugadorService no tiene método findAll, implementarlo
                return eventoJugadorService.obtenerTodosLosEventos();
            }

            @Override
            public EventoJugador save(EventoJugador entity) {
                return eventoJugadorService.registrarEvento(entity);
            }

            @Override
            public EventoJugador update(Long id, EventoJugador entity) {
                // Si EventoJugadorService no tiene método update, implementarlo
                return eventoJugadorService.actualizarEvento(id, entity);
            }

            @Override
            public void delete(Long id) {
                // Si EventoJugadorService no tiene método delete, implementarlo
                eventoJugadorService.eliminarEvento(id);
            }
        };
    }

    @Override
    protected EventoJugadorDTO toDto(EventoJugador entity) {
        // Conversión manual de EventoJugador a EventoJugadorDTO
        EventoJugadorDTO dto = new EventoJugadorDTO();
        dto.setJugadorId(entity.getJugador() != null ? entity.getJugador().getId() : null);
        dto.setPartidoId(entity.getPartido().getId());
        dto.setTipoEvento(entity.getTipoEvento());
        dto.setMinuto(entity.getMinuto());
        dto.setEsEventoRival(entity.getEsEventoRival()); // ✅ Incluir campo esEventoRival
        dto.setJugadorSaleId(entity.getJugadorSaleId());
        dto.setJugadorEntraId(entity.getJugadorEntraId());
        return dto;
    }

    @Override
    protected EventoJugador toEntity(EventoJugadorDTO dto) {
        // Conversión manual de EventoJugadorDTO a EventoJugador
        Jugador jugador = jugadorService.obtenerJugadorPorId(dto.getJugadorId());
        Partido partido = partidoService.obtenerPartidoPorId(dto.getPartidoId());

        EventoJugador evento = new EventoJugador();
        evento.setJugador(jugador);
        evento.setPartido(partido);
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setMinuto(dto.getMinuto());
        evento.setJugadorSaleId(dto.getJugadorSaleId());
        evento.setJugadorEntraId(dto.getJugadorEntraId());
        return evento;
    }

    // ========== ENDPOINTS ESPECÍFICOS DE EVENTO JUGADOR ==========

    /**
     * POST /api/v1/eventos
     * Registrar nuevo evento de jugador
     * Override para personalizar la creación
     */
    @PostMapping
    @Override
    public ResponseEntity<EventoJugadorDTO> create(@RequestBody EventoJugadorDTO dto) {
        Partido partido = partidoService.obtenerPartidoPorId(dto.getPartidoId());

        EventoJugador evento = new EventoJugador();
        
        // ✅ Determinar si es evento del rival (tipo GOL_RIVAL o esEventoRival=true)
        boolean esEventoRival = "GOL_RIVAL".equalsIgnoreCase(dto.getTipoEvento()) || 
                                Boolean.TRUE.equals(dto.getEsEventoRival());
        
        if (esEventoRival) {
            // Para eventos del rival, usar el primer jugador del equipo como referencia
            // El campo esEventoRival indica claramente que NO es del jugador
            List<Jugador> jugadoresEquipo = jugadorService.obtenerPorEquipo(partido.getEquipo().getId());
            if (jugadoresEquipo.isEmpty()) {
                throw new RuntimeException("No hay jugadores en el equipo para crear evento del rival");
            }
            evento.setJugador(jugadoresEquipo.get(0)); // Jugador de referencia
            evento.setEsEventoRival(true); // ✅ IMPORTANTE: Marca el evento como del rival
        } else {
            // Evento del equipo propio - jugadorId es OBLIGATORIO
            if (dto.getJugadorId() == null) {
                throw new IllegalArgumentException("jugadorId es obligatorio para eventos del equipo");
            }
            Jugador jugador = jugadorService.obtenerJugadorPorId(dto.getJugadorId());
            evento.setJugador(jugador);
            evento.setEsEventoRival(false);
        }
        
        evento.setPartido(partido);
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setMinuto(dto.getMinuto());
        evento.setJugadorSaleId(dto.getJugadorSaleId());
        evento.setJugadorEntraId(dto.getJugadorEntraId());

        EventoJugador registrado = eventoJugadorService.registrarEvento(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrado));
    }

    /**
     * GET /api/v1/eventos/jugador/{jugadorId}
     * Obtener eventos de un jugador específico
     */
    @GetMapping("/jugador/{jugadorId}")
    public ResponseEntity<List<EventoJugador>> eventosPorJugador(@PathVariable Long jugadorId) {
        List<EventoJugador> eventos = eventoJugadorService.obtenerEventosPorJugador(jugadorId);
        return ResponseEntity.ok(eventos);
    }

    /**
     * GET /api/v1/eventos/partido/{partidoId}
     * Obtener eventos de un partido específico
     */
    @GetMapping("/partido/{partidoId}")
    public ResponseEntity<List<EventoJugador>> eventosPorPartido(@PathVariable Long partidoId) {
        List<EventoJugador> eventos = eventoJugadorService.obtenerEventosPorPartido(partidoId);
        return ResponseEntity.ok(eventos);
    }

    /**
     * GET /api/v1/eventos/resumen/jugador/{jugadorId}
     * Obtener resumen de eventos de un jugador
     */
    @GetMapping("/resumen/jugador/{jugadorId}")
    public ResponseEntity<List<EventoResumenDTO>> resumenEventosPorJugador(@PathVariable Long jugadorId) {
        List<EventoResumenDTO> resumen = eventoJugadorService.resumenEventosPorJugador(jugadorId);
        return ResponseEntity.ok(resumen);
    }
}
