package com.swiftwheelshubreactive.expense.handler;

import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.expense.service.InvoiceService;
import com.swiftwheelshubreactive.expense.validator.InvoiceRequestValidator;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final InvoiceRequestValidator invoiceRequestValidator;

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findAllInvoices(ServerRequest serverRequest) {
        return invoiceService.findAllInvoices()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(invoiceDtoList -> ServerResponse.ok().bodyValue(invoiceDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findAllActiveInvoices(ServerRequest serverRequest) {
        return invoiceService.findAllActiveInvoices()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(invoiceDtoList -> ServerResponse.ok().bodyValue(invoiceDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findAllInvoicesByCustomerUsername(ServerRequest serverRequest) {
        return invoiceService.findAllInvoicesByCustomerUsername(ServerRequestUtil.getPathVariable(serverRequest, CUSTOMER_ID))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(invoiceDtoList -> ServerResponse.ok().bodyValue(invoiceDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findInvoiceById(ServerRequest serverRequest) {
        return invoiceService.findInvoiceById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(invoiceDto -> ServerResponse.ok().bodyValue(invoiceDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findInvoicesByComments(ServerRequest serverRequest) {
        return invoiceService.findInvoicesByComments(ServerRequestUtil.getPathVariable(serverRequest, COMMENTS))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(invoiceDto -> ServerResponse.ok().bodyValue(invoiceDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> countInvoices(ServerRequest serverRequest) {
        return invoiceService.countInvoices()
                .flatMap(numberOfInvoices -> ServerResponse.ok().bodyValue(numberOfInvoices))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> countAllActiveInvoices(ServerRequest serverRequest) {
        return invoiceService.countAllActiveInvoices()
                .flatMap(numberOfActiveInvoices -> ServerResponse.ok().bodyValue(numberOfActiveInvoices))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> closeInvoice(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(InvoiceRequest.class)
                .flatMap(invoiceRequestValidator::validateBody)
                .flatMap(invoiceRequest -> invoiceService.closeInvoice(
                                ServerRequestUtil.getApiKeyHeader(serverRequest),
                                ServerRequestUtil.getRolesHeader(serverRequest),
                                ServerRequestUtil.getPathVariable(serverRequest, ID),
                                invoiceRequest
                        )
                )
                .flatMap(invoiceResponse -> ServerResponse.ok().bodyValue(invoiceResponse));
    }

}
