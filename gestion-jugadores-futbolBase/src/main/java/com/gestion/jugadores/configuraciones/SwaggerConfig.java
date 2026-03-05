package com.gestion.jugadores.configuraciones;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para la documentación de la API REST
 * 
 * Acceso a Swagger UI: http://localhost:8080/swagger-ui.html
 * Acceso a OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestión de Jugadores de Fútbol Base")
                        .version("1.0")
                        .description("API REST para la gestión de jugadores, equipos, partidos y estadísticas de fútbol base")
                        .contact(new Contact()
                                .name("Oscar Martínez")
                                .email("oscar@example.com")
                                .url("https://github.com/OscarMartP/gestion-jugadores-futbolBase"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo Local"),
                        new Server()
                                .url("https://api.gestion-jugadores.com")
                                .description("Servidor de Producción (opcional)")))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Introduce el token JWT obtenido del endpoint /login")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
