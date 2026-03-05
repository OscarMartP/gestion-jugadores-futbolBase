package com.gestion.jugadores.servicios.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestion.jugadores.modelo.Usuario;
import com.gestion.jugadores.modelo.UsuarioRol;
import com.gestion.jugadores.repositorio.RolRepository;
import com.gestion.jugadores.repositorio.UsuarioRepository;
import com.gestion.jugadores.servicios.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Inyecta el PasswordEncoder


    @Override
    public Usuario guardarUsuario(Usuario usuario, Set<UsuarioRol> usuarioRoles) throws Exception {
        Usuario usuarioLocal = usuarioRepository.findByUsername(usuario.getUsername());
        if (usuarioLocal != null) {
            throw new Exception("El usuario ya existe");
        }

        // Hashear la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setPerfil("default.png");

        for (UsuarioRol usuarioRol : usuarioRoles) {
            rolRepository.save(usuarioRol.getRol());
        }
        usuario.getUsuarioRoles().addAll(usuarioRoles);
        return usuarioRepository.save(usuario);
    }
    
    @Override
    public Usuario obtenerUsuario(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public void eliminarUsuario(Long usuarioId) {
        usuarioRepository.deleteById(usuarioId);
    }

}
