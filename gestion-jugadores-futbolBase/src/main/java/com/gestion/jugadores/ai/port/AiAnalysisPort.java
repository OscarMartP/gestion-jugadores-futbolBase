package com.gestion.jugadores.ai.port;

/**
 * Puerto (interfaz) para servicio de análisis con IA.
 * Siguiendo principios de arquitectura hexagonal, esta interfaz define
 * el contrato para servicios externos de IA sin acoplar la lógica de negocio
 * a una implementación específica.
 */
public interface AiAnalysisPort {
    
    /**
     * Genera un análisis de texto utilizando IA basado en un prompt estructurado.
     * 
     * @param prompt El prompt con contexto e instrucciones para la IA
     * @return El análisis generado como texto
     * @throws RuntimeException si hay error en la comunicación con el servicio IA
     */
    String generateAnalysis(String prompt);
    
    /**
     * Genera un análisis con límite de tokens en la respuesta.
     * 
     * @param prompt El prompt con contexto e instrucciones
     * @param maxTokens Número máximo de tokens en la respuesta (controla longitud y coste)
     * @return El análisis generado como texto
     * @throws RuntimeException si hay error en la comunicación con el servicio IA
     */
    String generateAnalysis(String prompt, Integer maxTokens);
}
