package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.dto.RequestDetails;
import com.swiftwheelshubreactive.dto.UserInfo;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    @Value("${webclient.url.swift-wheels-hub-customers}")
    private String url;

    private static final String SEPARATOR = "/";

    private final WebClient webClient;

    public Mono<UserInfo> getUserByUsername(RequestDetails requestDetails) {
        return webClient.get()
                .uri(url + SEPARATOR + "username" + SEPARATOR + "{username}", requestDetails.username())
                .headers(WebClientUtil.setHttpHeaders(requestDetails.apikey(), requestDetails.roles()))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserInfo.class)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

}
