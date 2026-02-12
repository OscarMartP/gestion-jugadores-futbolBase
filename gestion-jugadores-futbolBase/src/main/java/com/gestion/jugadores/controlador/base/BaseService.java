package com.gestion.jugadores.controlador.base;

import java.util.List;

/**
 * Interfaz base genérica para servicios que proporciona operaciones CRUD comunes.
 * Todos los servicios específicos deben implementar esta interfaz.
 * 
 * @param <E> Entidad del modelo
 * @param <ID> Tipo del identificador
 */
public interface BaseService<E, ID> {

    /**
     * Buscar entidad por ID
     * @throws ResourceNotFoundException si no existe
     */
    E findById(ID id);

    /**
     * Listar todas las entidades
     */
    List<E> findAll();

    /**
     * Guardar nueva entidad
     */
    E save(E entity);

    /**
     * Actualizar entidad existente
     * @throws ResourceNotFoundException si no existe
     */
    E update(ID id, E entity);

    /**
     * Eliminar entidad por ID
     * @throws ResourceNotFoundException si no existe
     */
    void delete(ID id);
}
