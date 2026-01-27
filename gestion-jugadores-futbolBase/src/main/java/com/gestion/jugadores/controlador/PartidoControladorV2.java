package com.gestion.jugadores.controlador;

import com.gestion.jugadores.controlador.base.BaseController;
import com.gestion.jugadores.controlador.base.BaseService;
import com.gestion.jugadores.dto.PartidoDTO;
import com.gestion.jugadores.mapper.PartidoMapper;
import com.gestion.jugadores.modelo.Partido;
import com.gestion.jugadores.servicios.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de Partidos
 * Extiende BaseController para heredar operaciones CRUD comunes
 * Solo implementa endpoints específicos de Partido
 */
@RestController
@RequestMapping("/api/v1/partidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PartidoControladorV2 extends BaseController<Partido, PartidoDTO, Long> {

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private PartidoMapper partidoMapper;

    @Override
    protected BaseService<Partido, Long> getService() {
        return new BaseService<Partido, Long>() {
            @Override
            public Partido findById(Long id) {
                return partidoService.obtenerPartidoPorId(id);
            }

            @Override
            public List<Partido> findAll() {
                // Si necesitas listar todos los partidos
                return partidoService.obtenerPartidosActivos(); // o implementar findAll en el servicio
            }

            @Override
            public Partido save(Partido entity) {
                // Asegurar que partidoActivo no sea null
                if (entity.getPartidoActivo() == null) {
                    entity.setPartidoActivo(false);
                }
                // ✅ VALIDACIÓN: Evitar jugadores duplicados en titulares y suplentes
                entity.validarAlineacion();
                return partidoService.crearPartido(entity);
            }

            @Override
            public Partido update(Long id, Partido entity) {
                // Preservar partidoActivo si es null
                if (entity.getPartidoActivo() == null) {
                    Partido existente = partidoService.obtenerPartidoPorId(id);
                    entity.setPartidoActivo(existente.getPartidoActivo());
                }
                // ✅ VALIDACIÓN: Evitar jugadores duplicados en titulares y suplentes
                entity.validarAlineacion();
                return partidoService.actualizarPartido(id, entity);
            }

            @Override
            public void delete(Long id) {
                partidoService.eliminarPartido(id);
            }
        };
    }

    @Override
    protected PartidoDTO toDto(Partido entity) {
        return partidoMapper.toDto(entity);
    }

    @Override
    protected Partido toEntity(PartidoDTO dto) {
        return partidoMapper.toEntity(dto);
    }

    // ========== ENDPOINTS ESPECÍFICOS DE PARTIDO ==========

    /**
     * GET /api/v1/partidos/equipo/{equipoId}
     * Obtener partidos por equipo
     */
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<PartidoDTO>> getByEquipo(@PathVariable Long equipoId) {
        List<Partido> partidos = partidoService.obtenerPartidosPorEquipo(equipoId);
        return ResponseEntity.ok(partidos.stream()
                .map(partidoMapper::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * GET /api/v1/partidos/activos/equipo/{equipoId}
     * Obtener partidos activos por equipo
     */
    @GetMapping("/activos/equipo/{equipoId}")
    public ResponseEntity<List<PartidoDTO>> getActivosByEquipo(@PathVariable Long equipoId) {
        List<Partido> partidos = partidoService.obtenerPartidosActivosPorEquipo(equipoId);
        return ResponseEntity.ok(partidos.stream()
                .map(partidoMapper::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * GET /api/v1/partidos/activos
     * Obtener todos los partidos activos
     */
    @GetMapping("/activos")
    public ResponseEntity<List<PartidoDTO>> getActivos() {
        List<Partido> partidos = partidoService.obtenerPartidosActivos();
        return ResponseEntity.ok(partidos.stream()
                .map(partidoMapper::toDto)
                .collect(Collectors.toList()));
    }

    /**
     * PUT /api/v1/partidos/{id}/activar
     * Activar un partido
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<PartidoDTO> activar(@PathVariable Long id) {
        Partido partido = partidoService.activarPartido(id);
        return ResponseEntity.ok(partidoMapper.toDto(partido));
    }

    /**
     * PUT /api/v1/partidos/{id}/desactivar
     * Desactivar un partido
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<PartidoDTO> desactivar(@PathVariable Long id) {
        Partido partido = partidoService.desactivarPartido(id);
        return ResponseEntity.ok(partidoMapper.toDto(partido));
    }

    /**
     * GET /api/v1/partidos/equipo/{equipoId}/tiene-activo
     * Verificar si un equipo tiene partido activo
     */
    @GetMapping("/equipo/{equipoId}/tiene-activo")
    public ResponseEntity<Boolean> tienePartidoActivo(@PathVariable Long equipoId) {
        Boolean tieneActivo = partidoService.tienePartidoActivo(equipoId);
        return ResponseEntity.ok(tieneActivo);
    }

    /**
     * PUT /api/v1/partidos/{id}/alineacion
     * Actualizar alineación (titulares y suplentes) de un partido
     * Valida que no haya jugadores duplicados entre titulares y suplentes
     */
    @PutMapping("/{id}/alineacion")
    public ResponseEntity<PartidoDTO> actualizarAlineacion(
            @PathVariable Long id, 
            @RequestBody AlineacionRequest alineacion) {
        Partido partido = partidoService.obtenerPartidoPorId(id);
        partido.setTitulares(alineacion.getTitulares());
        partido.setSuplentes(alineacion.getSuplentes());
        
        // ✅ VALIDACIÓN: Evitar jugadores duplicados en titulares y suplentes
        partido.validarAlineacion();
        
        Partido actualizado = partidoService.actualizarPartido(id, partido);
        return ResponseEntity.ok(partidoMapper.toDto(actualizado));
    }

    /**
     * DTO interno para recibir alineación
     */
    public static class AlineacionRequest {
        private List<Long> titulares;
        private List<Long> suplentes;

        public List<Long> getTitulares() { return titulares; }
        public void setTitulares(List<Long> titulares) { this.titulares = titulares; }
        
        public List<Long> getSuplentes() { return suplentes; }
        public void setSuplentes(List<Long> suplentes) { this.suplentes = suplentes; }
    }
}
