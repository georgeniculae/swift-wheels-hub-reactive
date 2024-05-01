package com.swiftwheelshubreactive.apigateway.filter.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LanguageHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<LanguageHeaderGatewayFilterFactory.LanguageConfig> {

    private static final String LANGUAGE = "Language";

    public LanguageHeaderGatewayFilterFactory() {
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
