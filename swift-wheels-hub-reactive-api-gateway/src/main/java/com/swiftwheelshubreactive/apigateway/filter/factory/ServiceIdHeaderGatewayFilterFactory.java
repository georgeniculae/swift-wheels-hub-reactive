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
public class ServiceIdHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<ServiceIdHeaderGatewayFilterFactory.ServiceIdConfig> {

    private static final String SERVICE_ID = "X-SERVICE-ID";

    public ServiceIdHeaderGatewayFilterFactory() {
        super(ServiceIdConfig.class);
    }

    @Override
    public GatewayFilter apply(ServiceIdConfig serviceIdConfig) {
        return (exchange, chain) -> Mono.just(getExchangeWithUpdatedHeaders(serviceIdConfig, exchange))
                .flatMap(chain::filter);
    }

    private ServerWebExchange getExchangeWithUpdatedHeaders(ServiceIdConfig serviceIdConfig,
                                                            ServerWebExchange exchange) {
        return exchange.mutate()
                .request(requestBuilder -> requestBuilder.header(SERVICE_ID, serviceIdConfig.getServiceId()))
                .build();
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class ServiceIdConfig {

        private String serviceId;

    }

}
