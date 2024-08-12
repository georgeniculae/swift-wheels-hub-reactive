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

    @Bean
    public VertexAI vertexAI(@Value("${spring.ai.vertex.ai.gemini.project-id}") String projectId,
                             @Value("${spring.ai.vertex.ai.gemini.location}") String location) {
        return new VertexAI(projectId, location);
    }

    @Bean
    public ChatModel chatModel(@Value("${spring.ai.vertex.ai.gemini.chat.options.model}") String model,
                               @Value("${spring.ai.vertex.ai.gemini.chat.options.temperature}") Float temperature,
                               VertexAI vertexAi) {
        return new VertexAiGeminiChatModel(
                vertexAi,
                VertexAiGeminiChatOptions.builder()
                        .withModel(model)
                        .withTemperature(temperature)
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
