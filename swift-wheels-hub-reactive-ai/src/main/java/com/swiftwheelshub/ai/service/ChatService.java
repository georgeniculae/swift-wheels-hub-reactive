package com.swiftwheelshub.ai.service;

import com.swiftwheelshubreactive.dto.CarSuggestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public Mono<CarSuggestionResponse> getChatReply(String prompt) {
        return Mono.fromCallable(() -> chatClient.prompt()
                        .user(prompt)
                        .call()
                        .entity(CarSuggestionResponse.class))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
