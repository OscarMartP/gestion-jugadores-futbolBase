package com.gestion.jugadores.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración para procesamiento asíncrono
 * Permite ejecutar tareas en segundo plano sin bloquear el hilo principal
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * Configuración del pool de threads para tareas asíncronas
     * Optimizado para actualización de estadísticas en partidos
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Hilos iniciales en el pool
        executor.setCorePoolSize(2);
        
        // Máximo de hilos permitidos
        executor.setMaxPoolSize(5);
        
        // Capacidad de la cola de espera
        executor.setQueueCapacity(100);
        
        // Prefijo para identificar threads en logs
        executor.setThreadNamePrefix("Async-Estadisticas-");
        
        // Esperar a que terminen las tareas antes de shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Timeout para esperar terminación (30 segundos)
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        return executor;
    }
}
