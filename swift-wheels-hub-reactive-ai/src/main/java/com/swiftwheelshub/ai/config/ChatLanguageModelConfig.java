package com.swiftwheelshub.ai.config;

import com.google.cloud.vertexai.VertexAI;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class ChatLanguageModelConfig {

    @Value("${spring.ai.vertex.ai.gemini.project-id}")
    private String projectId;

    @Value("${spring.ai.vertex.ai.gemini.location}")
    private String location;

    @Value("${spring.ai.vertex.ai.gemini.chat.options.model}")
    private String model;

    @Value("${spring.ai.vertex.ai.gemini.chat.options.temperature}")
    private Float temperature;

    @Bean
    public VertexAI vertexAI() {
        return new VertexAI(projectId, location);
    }

    @Bean
    public ChatModel chatModel() {
        return new VertexAiGeminiChatModel(
                vertexAI(),
                VertexAiGeminiChatOptions.builder()
                        .withModel(model)
                        .withTemperature(temperature)
                        .build()
        );
    }

    @Bean
    public ChatClient chatClient() {
        return ChatClient.create(chatModel());
    }

}
