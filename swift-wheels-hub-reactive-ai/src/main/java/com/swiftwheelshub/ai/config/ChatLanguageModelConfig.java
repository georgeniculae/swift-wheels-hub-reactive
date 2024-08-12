package com.swiftwheelshub.ai.config;

import com.google.cloud.vertexai.VertexAI;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatLanguageModelConfig {

    @Bean
    public VertexAI vertexAI(VertexProperties vertexProperties) {
        return new VertexAI(vertexProperties.getProjectId(), vertexProperties.getLocation());
    }

    @Bean
    public ChatModel chatModel(ChatProperties chatProperties, VertexAI vertexAi) {
        return new VertexAiGeminiChatModel(
                vertexAi,
                VertexAiGeminiChatOptions.builder()
                        .withModel(chatProperties.getModel())
                        .withTemperature(chatProperties.getTemperature())
                        .build()
        );
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        You are a helpful assistant who can clearly and concisely answer questions about the type
                        of vehicle that is most suitable for traveling to a certain location in Romania in a certain
                        month of the year. You must provide one suggestion only.""")
                .build();
    }

}
