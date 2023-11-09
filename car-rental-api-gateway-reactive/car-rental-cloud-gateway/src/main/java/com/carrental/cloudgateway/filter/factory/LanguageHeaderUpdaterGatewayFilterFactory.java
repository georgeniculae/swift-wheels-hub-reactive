package com.carrental.cloudgateway.filter.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LanguageHeaderUpdaterGatewayFilterFactory extends AbstractGatewayFilterFactory<LanguageHeaderUpdaterGatewayFilterFactory.LanguageConfig> {

    private static final String LANGUAGE = "Language";

    public LanguageHeaderUpdaterGatewayFilterFactory() {
        super(LanguageConfig.class);
    }

    @Override
    public GatewayFilter apply(LanguageConfig languageConfig) {
        return (exchange, chain) -> Mono.just(getExchangeWithUpdatedHeaders(languageConfig, exchange))
                .flatMap(chain::filter);
    }

    private ServerWebExchange getExchangeWithUpdatedHeaders(LanguageConfig languageConfig,
                                                            ServerWebExchange exchange) {
        return exchange.mutate()
                .request(requestBuilder -> requestBuilder.header(LANGUAGE, languageConfig.getLanguage()))
                .build();
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class LanguageConfig {

        private String language;

    }

}
