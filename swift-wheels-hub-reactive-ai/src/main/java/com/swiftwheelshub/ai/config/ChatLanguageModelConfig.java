package com.swiftwheelshub.ai.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatLanguageModelConfig {

    @Value("${spring.ai.vertex.ai.gemini.project-id}")
    private String projectId;

    @Value("${spring.ai.vertex.ai.gemini.location}")
    private String location;

    @Value("${spring.ai.vertex.ai.gemini.chat.options.model}")
    private String model;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return VertexAiGeminiChatModel.builder()
                .location(location)
                .project(projectId)
                .modelName(model)
                .build();
    }

}
