package com.gestion.jugadores.ai.dto;

import java.util.List;

/**
 * DTO para realizar peticiones a la API de OpenAI.
 * Estructura basada en la API de Chat Completions de OpenAI.
 */
public class OpenAiRequest {
    
    private String model;
    private List<Message> messages;
    private Integer max_tokens;
    private Double temperature;
    
    public OpenAiRequest() {
    }
    
    public OpenAiRequest(String model, List<Message> messages, Integer max_tokens) {
        this.model = model;
        this.messages = messages;
        this.max_tokens = max_tokens;
        this.temperature = 0.7; // Valor por defecto balanceado
    }
    
    // Clase interna para mensajes
    public static class Message {
        private String role;
        private String content;
        
        public Message() {
        }
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    // Getters y Setters
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public Integer getMax_tokens() {
        return max_tokens;
    }
    
    public void setMax_tokens(Integer max_tokens) {
        this.max_tokens = max_tokens;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
