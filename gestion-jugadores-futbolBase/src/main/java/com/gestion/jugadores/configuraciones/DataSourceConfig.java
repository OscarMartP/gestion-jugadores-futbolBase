package com.gestion.jugadores.configuraciones;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Configuración del DataSource para producción con Neon PostgreSQL
 * Maneja automáticamente la conversión de URLs de Neon (postgresql://) a formato JDBC (jdbc:postgresql://)
 */
@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalStateException("DATABASE_URL environment variable is not set");
        }
        
        // Convertir postgresql:// a jdbc:postgresql:// si es necesario
        if (databaseUrl.startsWith("postgresql://") && !databaseUrl.startsWith("jdbc:")) {
            databaseUrl = "jdbc:" + databaseUrl;
        }
        
        // Asegurar que SSL esté habilitado para Neon
        if (!databaseUrl.contains("ssl=") && !databaseUrl.contains("sslmode=")) {
            databaseUrl += (databaseUrl.contains("?") ? "&" : "?") + "sslmode=require";
        }
        
        return DataSourceBuilder
                .create()
                .url(databaseUrl)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
