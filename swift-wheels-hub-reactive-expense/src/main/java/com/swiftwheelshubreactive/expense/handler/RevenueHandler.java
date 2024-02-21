package com.swiftwheelshubreactive.expense.handler;

import com.swiftwheelshubreactive.expense.service.RevenueService;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RevenueHandler {

    private static final String DATE_OF_REVENUE = "dateOfRevenue";
    private final RevenueService revenueService;

    public Mono<ServerResponse> findAllRevenues(ServerRequest serverRequest) {
        return revenueService.findAllRevenues()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(revenueDtoList -> ServerResponse.ok().bodyValue(revenueDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findRevenuesByDate(ServerRequest serverRequest) {
        return revenueService.findRevenuesByDate(ServerRequestUtil.getPathVariable(serverRequest, DATE_OF_REVENUE))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(revenueDtoList -> ServerResponse.ok().bodyValue(revenueDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getTotalAmount(ServerRequest serverRequest) {
        return revenueService.getTotalAmount()
                .flatMap(totalAmount -> ServerResponse.ok().bodyValue(totalAmount));
    }

}
