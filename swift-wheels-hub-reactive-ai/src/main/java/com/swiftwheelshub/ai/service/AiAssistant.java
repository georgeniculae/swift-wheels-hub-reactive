package com.swiftwheelshub.ai.service;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AiAssistant {

    @UserMessage("{{message}}")
    String chat(@V("message") String userMessage);

}
