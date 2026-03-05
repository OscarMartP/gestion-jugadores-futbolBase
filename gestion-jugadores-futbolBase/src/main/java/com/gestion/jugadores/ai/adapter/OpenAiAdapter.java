package com.gestion.jugadores.ai.adapter;

import com.gestion.jugadores.ai.dto.OpenAiRequest;
import com.gestion.jugadores.ai.dto.OpenAiResponse;
import com.gestion.jugadores.ai.port.AiAnalysisPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;

/**
 * Adaptador que implementa la comunicación con la API de OpenAI.
 * Siguiendo arquitectura hexagonal, este adaptador implementa el puerto
 * AiAnalysisPort y encapsula toda la lógica de comunicación HTTP con OpenAI.
 */
@Component
public class OpenAiAdapter implements AiAnalysisPort {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAiAdapter.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final Integer DEFAULT_MAX_TOKENS = 500;
    
    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    
    /**
     * Constructor del adaptador.
     * 
     * @param apiKey Clave API de OpenAI (inyectada desde application.properties)
     * @param model Modelo de OpenAI a utilizar (por defecto gpt-3.5-turbo)
     */
    public OpenAiAdapter(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.api.model:gpt-3.5-turbo}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        
        // Configuración del WebClient con headers comunes
        this.webClient = WebClient.builder()
                .baseUrl(OPENAI_API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        
        logger.info("OpenAiAdapter inicializado con modelo: {}", model);
    }
    
    @Override
    public String generateAnalysis(String prompt) {
        return generateAnalysis(prompt, DEFAULT_MAX_TOKENS);
    }
    
    @Override
    public String generateAnalysis(String prompt, Integer maxTokens) {
        try {
            logger.debug("Generando análisis con IA. Tokens max: {}", maxTokens);
            
            // Construir el request para OpenAI
            OpenAiRequest request = buildRequest(prompt, maxTokens);
            
            // Realizar llamada HTTP a OpenAI
            OpenAiResponse response = webClient.post()
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAiResponse.class)
                    .block(); // Operación síncrona para simplificar
            
            // Extraer el contenido de la respuesta
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                
                // Log de tokens utilizados (útil para monitorear costes)
                if (response.getUsage() != null) {
                    logger.info("Tokens utilizados - Prompt: {}, Completion: {}, Total: {}",
                            response.getUsage().getPrompt_tokens(),
                            response.getUsage().getCompletion_tokens(),
                            response.getUsage().getTotal_tokens());
                }
                
                return content.trim();
            }
            
            throw new RuntimeException("Respuesta vacía de OpenAI");
            
        } catch (WebClientResponseException e) {
            logger.error("Error en comunicación con OpenAI. Status: {}, Body: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al comunicarse con OpenAI: " + e.getMessage(), e);
            
        } catch (Exception e) {
            logger.error("Error inesperado al generar análisis con IA", e);
            throw new RuntimeException("Error al generar análisis: " + e.getMessage(), e);
        }
    }
    
    /**
     * Construye el objeto de petición para OpenAI.
     * 
     * @param prompt Texto del prompt
     * @param maxTokens Máximo de tokens en la respuesta
     * @return Objeto OpenAiRequest configurado
     */
    private OpenAiRequest buildRequest(String prompt, Integer maxTokens) {
        // Sistema message: configura el contexto general de la IA
        OpenAiRequest.Message systemMessage = new OpenAiRequest.Message(
                "system",
                "Eres un asistente técnico experto en análisis futbolístico. " +
                "Proporciona análisis técnicos profesionales, concisos y objetivos basados en estadísticas."
        );
        
        // User message: el prompt específico con los datos
        OpenAiRequest.Message userMessage = new OpenAiRequest.Message(
                "user",
                prompt
        );
        
        List<OpenAiRequest.Message> messages = Arrays.asList(systemMessage, userMessage);
        
        return new OpenAiRequest(model, messages, maxTokens);
    }
}
