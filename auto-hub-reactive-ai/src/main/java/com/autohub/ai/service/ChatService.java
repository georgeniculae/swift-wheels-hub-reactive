package com.autohub.ai.service;

import com.autohubreactive.dto.ai.CarSuggestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public Mono<CarSuggestionResponse> getChatReply(String text, Map<String, Object> params) {
        return Mono.fromCallable(() -> getCarSuggestionResponse(text, params))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private CarSuggestionResponse getCarSuggestionResponse(String text, Map<String, Object> params) {
        return chatClient.prompt()
                .user(userSpec -> userSpec.text(text).params(params))
                .call()
                .entity(CarSuggestionResponse.class);
    }

}
