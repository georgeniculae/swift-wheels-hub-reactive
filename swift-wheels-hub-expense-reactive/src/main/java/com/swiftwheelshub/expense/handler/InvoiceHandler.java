package com.swiftwheelshub.expense.handler;

import com.swiftwheelshub.dto.InvoiceDto;
import com.swiftwheelshub.expense.service.InvoiceService;
import com.swiftwheelshub.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InvoiceHandler {

    private static final String CUSTOMER_ID = "customerId";
    private static final String ID = "id";
    private static final String COMMENTS = "comments";
    private final InvoiceService invoiceService;

    public Mono<ServerResponse> findAllInvoices(ServerRequest serverRequest) {
        return invoiceService.findAllInvoices()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(invoiceDtoList -> ServerResponse.ok().bodyValue(invoiceDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findAllActiveInvoices(ServerRequest serverRequest) {
        return invoiceService.findAllActiveInvoices()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(invoiceDtoList -> ServerResponse.ok().bodyValue(invoiceDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findAllInvoicesByCustomerUsername(ServerRequest serverRequest) {
        return invoiceService.findAllInvoicesByCustomerUsername(ServerRequestUtil.getPathVariable(serverRequest, CUSTOMER_ID))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(invoiceDtoList -> ServerResponse.ok().bodyValue(invoiceDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findInvoiceById(ServerRequest serverRequest) {
        return invoiceService.findInvoiceById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(invoiceDto -> ServerResponse.ok().bodyValue(invoiceDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findInvoiceByComments(ServerRequest serverRequest) {
        return invoiceService.findInvoicesByComments(ServerRequestUtil.getPathVariable(serverRequest, COMMENTS))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(invoiceDto -> ServerResponse.ok().bodyValue(invoiceDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> countInvoices(ServerRequest serverRequest) {
        return invoiceService.countInvoices()
                .flatMap(numberOfInvoices -> ServerResponse.ok().bodyValue(numberOfInvoices))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> countAllActiveInvoices(ServerRequest serverRequest) {
        return invoiceService.countAllActiveInvoices()
                .flatMap(numberOfActiveInvoices -> ServerResponse.ok().bodyValue(numberOfActiveInvoices))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> closeInvoice(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(InvoiceDto.class)
                .flatMap(invoiceDto -> invoiceService.closeInvoice(ServerRequestUtil.getApiKeyHeader(serverRequest), ServerRequestUtil.getPathVariable(serverRequest, ID), invoiceDto))
                .flatMap(invoiceDto -> ServerResponse.ok().bodyValue(invoiceDto));
    }

}
