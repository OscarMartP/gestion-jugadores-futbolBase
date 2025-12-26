package com.gestion.jugadores.controlador;

import com.gestion.jugadores.controlador.base.BaseController;
import com.gestion.jugadores.controlador.base.BaseService;
import com.gestion.jugadores.dto.JugadorDTO;
import com.gestion.jugadores.excepciones.ResourceNotFoundException;
import com.gestion.jugadores.mapper.JugadorMapper;
import com.gestion.jugadores.modelo.Equipo;
import com.gestion.jugadores.modelo.Jugador;
import com.gestion.jugadores.repositorio.EquipoRepository;
import com.gestion.jugadores.repositorio.JugadorRepositorio;
import com.gestion.jugadores.repositorio.UsuarioRepository;
import com.gestion.jugadores.servicios.EquipoService;
import com.gestion.jugadores.servicios.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de Jugadores
 * Extiende BaseController para heredar operaciones CRUD comunes
 * Solo implementa endpoints específicos de Jugador
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:4200")
public class JugadorControladorV2 extends BaseController<Jugador, JugadorDTO, Long> {

    @Autowired
    private JugadorRepositorio jugadorRepositorio;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private JugadorMapper jugadorMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Override
    protected BaseService<Jugador, Long> getService() {
        return new BaseService<Jugador, Long>() {
            @Override
            public Jugador findById(Long id) {
                return jugadorRepositorio.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("No existe el jugador con el ID: " + id));
            }

            @Override
            public List<Jugador> findAll() {
                // Este método no se usa directamente, se maneja en getAll() override
                return jugadorRepositorio.findAll();
            }

            @Override
            public Jugador save(Jugador entity) {
                return jugadorRepositorio.save(entity);
            }

            @Override
            public Jugador update(Long id, Jugador entity) {
                Jugador jugador = findById(id);
                jugador.setNombre(entity.getNombre());
                jugador.setApellido(entity.getApellido());
                jugador.setPosicion(entity.getPosicion());
                
                // Actualizar equipo si se proporciona un equipoId válido
                if (entity.getEquipo() != null && entity.getEquipo().getId() != null) {
                    Equipo equipo = equipoRepository.findById(entity.getEquipo().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + entity.getEquipo().getId()));
                    jugador.setEquipo(equipo);
                }
                
                Jugador jugadorActualizado = jugadorRepositorio.save(jugador);
                // Refrescar desde la base de datos para asegurar que tenemos los datos más recientes
                return jugadorRepositorio.findById(jugadorActualizado.getId())
                    .orElse(jugadorActualizado);
            }

            @Override
            public void delete(Long id) {
                Jugador jugador = findById(id);
                jugadorRepositorio.delete(jugador);
            }
        };
    }

    @Override
    protected JugadorDTO toDto(Jugador entity) {
        return jugadorMapper.toDto(entity);
    }

    @Override
    protected Jugador toEntity(JugadorDTO dto) {
        return jugadorMapper.toEntity(dto);
    }

    /**
     * GET /{id} - Obtener por ID
     */
    @GetMapping("/jugadores/{id}")
    @Override
    public ResponseEntity<JugadorDTO> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    /**
     * PUT /{id} - Actualizar existente
     */
    @PutMapping("/jugadores/{id}")
    @Override
    public ResponseEntity<JugadorDTO> update(@PathVariable Long id, @RequestBody JugadorDTO dto) {
        return super.update(id, dto);
    }

    /**
     * DELETE /{id} - Eliminar
     */
    @DeleteMapping("/jugadores/{id}")
    @Override
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id) {
        return super.delete(id);
    }

    // ========== ENDPOINTS ESPECÍFICOS DE JUGADOR ==========

    /**
     * GET /api/v1/jugadores?equipoId=X
     * Listar jugadores con filtro opcional por equipo
     * Si no hay equipoId, devuelve jugadores del usuario autenticado
     */
    @GetMapping("/jugadores")
    @Override
    public ResponseEntity<List<JugadorDTO>> getAll() {
        // Este método se sobrescribe en el siguiente con parámetros
        return ResponseEntity.ok(List.of());
    }

    /**
     * GET /api/v1/jugadores?equipoId=X
     * Listar jugadores con filtro opcional por equipo
     */
    @GetMapping(value = "/jugadores", params = "!equipoId")
    public ResponseEntity<List<JugadorDTO>> listarJugadoresPorUsuario(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        com.gestion.jugadores.modelo.Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Jugador> jugadores = jugadorService.obtenerPorUsuario(usuario.getId());
        return ResponseEntity.ok(jugadores.stream()
                .map(jugadorMapper::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * GET /api/v1/jugadores?equipoId=X
     * Listar jugadores por equipo específico
     */
    @GetMapping(value = "/jugadores", params = "equipoId")
    public ResponseEntity<List<JugadorDTO>> listarJugadoresPorEquipo(@RequestParam Long equipoId) {
        List<Jugador> jugadores = jugadorService.obtenerPorEquipo(equipoId);
        return ResponseEntity.ok(jugadores.stream()
                .map(jugadorMapper::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * POST /api/v1/jugadores
     * Crear nuevo jugador
     * Override para añadir validación de equipo
     */
    @PostMapping("/jugadores")
    @Override
    public ResponseEntity<JugadorDTO> create(@RequestBody JugadorDTO jugadorDTO) {
        // Validar que se proporcione equipoId
        if (jugadorDTO.getEquipoId() == null) {
            throw new RuntimeException("Debe proporcionar un equipoId válido para el jugador");
        }

        // Recuperar el Equipo real de la base de datos
        Equipo equipo = equipoService.obtenerEquipoPorId(jugadorDTO.getEquipoId());
        if (equipo == null) {
            throw new ResourceNotFoundException("No existe el equipo con el ID: " + jugadorDTO.getEquipoId());
        }

        // Crear la entidad Jugador con los datos del DTO
        Jugador jugador = new Jugador();
        jugador.setNombre(jugadorDTO.getNombre());
        jugador.setApellido(jugadorDTO.getApellido());
        jugador.setPosicion(jugadorDTO.getPosicion());
        jugador.setEquipo(equipo);

        // Guardar el jugador
        Jugador jugadorGuardado = jugadorRepositorio.save(jugador);
        return ResponseEntity.status(HttpStatus.CREATED).body(jugadorMapper.toDto(jugadorGuardado));
    }

    /**
     * GET /api/v1/jugadores/equipo/{equipoId}
     * Obtener jugadores de un equipo específico (alternativa a ?equipoId=X)
     */
    @GetMapping("/jugadores/equipo/{equipoId}")
    public ResponseEntity<List<JugadorDTO>> obtenerJugadoresPorEquipo(@PathVariable Long equipoId) {
        List<Jugador> jugadores = jugadorService.obtenerPorEquipo(equipoId);
        return ResponseEntity.ok(jugadores.stream()
                .map(jugadorMapper::toDto)
                .collect(Collectors.toList()));
    }
}
