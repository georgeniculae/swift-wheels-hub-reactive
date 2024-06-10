package com.swiftwheelshub.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    public Flux<String> getChatReply(String prompt) {
        return Mono.fromCallable(() -> getStream(prompt))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(ChatClient.ChatClientRequest.StreamResponseSpec::content);
    }

    private ChatClient.ChatClientRequest.StreamResponseSpec getStream(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream();
    }

}
