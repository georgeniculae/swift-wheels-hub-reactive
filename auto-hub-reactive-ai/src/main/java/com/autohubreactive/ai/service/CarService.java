package com.autohubreactive.ai.service;

import com.autohubreactive.ai.util.Constants;
import com.autohubreactive.dto.ai.AvailableCarDetails;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import com.autohubreactive.lib.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final WebClient webClient;

    @Value("${webclient.url.auto-hub-agency-cars}")
    private String url;

    public Flux<AvailableCarDetails> getAllAvailableCars(String apikey, List<String> roles) {
        return webClient.get()
                .uri(url + Constants.SEPARATOR + "availability")
                .headers(WebClientUtil.setHttpHeaders(apikey, roles))
                .retrieve()
                .bodyToFlux(AvailableCarDetails.class)
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error while sending request to: {}, error: {}", url, e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

}
