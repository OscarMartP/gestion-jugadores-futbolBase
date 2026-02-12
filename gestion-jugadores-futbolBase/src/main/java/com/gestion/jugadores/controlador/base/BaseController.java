package com.gestion.jugadores.controlador.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador base genérico que proporciona operaciones CRUD comunes
 * para reducir código repetitivo en los controladores específicos.
 * 
 * @param <E> Entidad del modelo (ej: Jugador, Partido, Equipo)
 * @param <D> DTO correspondiente (ej: JugadorDTO, PartidoDTO, EquipoDTO)
 * @param <ID> Tipo del identificador (generalmente Long)
 */
public abstract class BaseController<E, D, ID> {

    /**
     * Obtiene el servicio específico para la entidad
     */
    protected abstract BaseService<E, ID> getService();

    /**
     * Convierte una entidad a su DTO correspondiente
     */
    protected abstract D toDto(E entity);

    /**
     * Convierte un DTO a su entidad correspondiente
     */
    protected abstract E toEntity(D dto);

    /**
     * GET /{id} - Obtener por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<D> getById(@PathVariable ID id) {
        E entity = getService().findById(id);
        return ResponseEntity.ok(toDto(entity));
    }

    /**
     * GET / - Listar todos
     */
    @GetMapping
    public ResponseEntity<List<D>> getAll() {
        List<E> entities = getService().findAll();
        List<D> dtos = entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST / - Crear nuevo
     */
    @PostMapping
    public ResponseEntity<D> create(@RequestBody D dto) {
        E entity = toEntity(dto);
        E created = getService().save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    /**
     * PUT /{id} - Actualizar existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<D> update(@PathVariable ID id, @RequestBody D dto) {
        E entity = toEntity(dto);
        E updated = getService().update(id, entity);
        return ResponseEntity.ok(toDto(updated));
    }

    /**
     * DELETE /{id} - Eliminar
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable ID id) {
        getService().delete(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
