package com.swiftwheelshub.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ChatClient chatClient;

    public Flux<String> openChatDiscussion(String prompt) {
        return Mono.fromCallable(() -> chatClient.prompt()
                        .user(prompt)
                        .stream())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(ChatClient.ChatClientRequest.StreamResponseSpec::content);
    }

}
