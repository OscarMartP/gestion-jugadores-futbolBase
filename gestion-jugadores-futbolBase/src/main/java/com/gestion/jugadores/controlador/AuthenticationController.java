package com.gestion.jugadores.controlador;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.jugadores.configuraciones.JwtUtils;
import com.gestion.jugadores.modelo.JwtRequest;
import com.gestion.jugadores.modelo.JwtResponse;
import com.gestion.jugadores.modelo.Rol;
import com.gestion.jugadores.modelo.Usuario;
import com.gestion.jugadores.modelo.UsuarioRol;
import com.gestion.jugadores.servicios.UsuarioService;
import com.gestion.jugadores.servicios.impl.UserDetailsServiceImpl;
//Logica Inicio Sesión
@RestController
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UsuarioService usuarioService;

	// Manejar explícitamente OPTIONS para CORS preflight
	@RequestMapping(value = "/generate-token", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> handleOptionsGenerateToken() {
		return ResponseEntity.ok().build();
	}

	@PostMapping("/generate-token")
	public ResponseEntity<?> generarToken(@RequestBody JwtRequest jwtRequest) throws Exception {
		try {
			autenticar(jwtRequest.getUsername(), jwtRequest.getPassword());
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Usuario no encontrado");
		}

		UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
		String token = this.jwtUtils.generateToken(userDetails);
		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void autenticar(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException exception) {
			throw new Exception("USUARIO DESHABILITADO " + exception.getMessage());
		} catch (BadCredentialsException e) {
			throw new Exception("Credenciales invalidas " + e.getMessage());
		}
	}

	@GetMapping("/actual-usuario")
	public Usuario obtenerUsuarioActual(Principal principal) {
		return (Usuario) this.userDetailsService.loadUserByUsername(principal.getName());
	}
	
	// Manejar explícitamente OPTIONS para CORS preflight en registro
	@RequestMapping(value = "/api/v1/register", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> handleOptionsRegister() {
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/api/v1/register")
	public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
		try {
			// Configurar valores por defecto
			usuario.setPerfil("default.png");
			
			// Crear rol por defecto (NORMAL)
			Set<UsuarioRol> usuarioRoles = new HashSet<>();
			Rol rol = new Rol();
			rol.setRolId(2L);
			rol.setRolNombre("NORMAL");
			
			UsuarioRol usuarioRol = new UsuarioRol();
			usuarioRol.setUsuario(usuario);
			usuarioRol.setRol(rol);
			usuarioRoles.add(usuarioRol);
			
			// Guardar usuario
			Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario, usuarioRoles);
			
			// Generar token automáticamente
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(usuarioGuardado.getUsername());
			String token = this.jwtUtils.generateToken(userDetails);
			
			return ResponseEntity.ok(new JwtResponse(token));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error al registrar usuario: " + e.getMessage());
		}
	}
}
