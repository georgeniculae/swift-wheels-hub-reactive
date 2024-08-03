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
public class RequestTraceGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestTraceGatewayFilterFactory.ServiceIdConfig> {

    private static final String SERVICE_ID = "X_SERVICE_ID";
    private static final String X_REQUEST_PATH = "X-PATH";

    public RequestTraceGatewayFilterFactory() {
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
                .request(requestBuilder -> {
                    requestBuilder.header(SERVICE_ID, serviceIdConfig.getServiceId());
                    requestBuilder.header(X_REQUEST_PATH, exchange.getRequest().getURI().getPath());
                })
                .build();
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class ServiceIdConfig {

        private String serviceId;

    }

}
