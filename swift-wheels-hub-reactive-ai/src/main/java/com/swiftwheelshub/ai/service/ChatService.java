package com.swiftwheelshub.ai.service;

import com.swiftwheelshubreactive.dto.CarSuggestionResponse;
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
        return Mono.fromCallable(() -> chatClient.prompt()
                        .user(userSpec -> userSpec.text(text).params(params))
                        .call()
                        .entity(CarSuggestionResponse.class))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
