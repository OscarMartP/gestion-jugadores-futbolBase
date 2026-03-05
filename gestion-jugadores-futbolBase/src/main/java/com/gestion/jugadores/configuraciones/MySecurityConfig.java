package com.gestion.jugadores.configuraciones;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gestion.jugadores.servicios.impl.UserDetailsServiceImpl;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Value("${cors.allowed.origins}")
	private String allowedOrigins;

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/*@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}*/
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// Usar origins desde application.properties (valores separados por coma)
		configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsServiceImpl).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http.csrf().disable()
	        .cors().configurationSource(corsConfigurationSource()).and()
	        .authorizeRequests()
			.antMatchers("/generate-token", "/usuarios/", "/api/v1/register", "/register").permitAll()
			// Permitir acceso público a Swagger UI y OpenAPI docs
			.antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
			// Permitir acceso público a endpoints de IA
			.antMatchers("/api/v1/ai/**").permitAll()
			// allow unauthenticated GET requests to list jugadores by query param (frontend uses /api/v1/jugadores?equipoId=...)
			.antMatchers(HttpMethod.GET, "/api/v1/jugadores").permitAll()
			.antMatchers("/api/v1/jugadores/equipo/**").permitAll()
			// Permitir acceso de solo lectura a partidos y eventos (para backups)
			.antMatchers(HttpMethod.GET, "/api/v1/partidos/**").permitAll()
			.antMatchers(HttpMethod.GET, "/api/v1/eventos/**").permitAll()
			.antMatchers(HttpMethod.GET, "/api/v1/estadisticas/**").permitAll()
			// Permitir acceso a equipos y partidos
			.antMatchers("/equipos/**", "/partidos/**", "/api/v2/**").permitAll()
	        .antMatchers(HttpMethod.OPTIONS).permitAll()
	        .anyRequest().authenticated()
	        .and()
	        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
	        .and()
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
