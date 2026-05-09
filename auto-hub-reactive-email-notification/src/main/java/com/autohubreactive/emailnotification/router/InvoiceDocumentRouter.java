package com.autohubreactive.emailnotification.router;

import com.autohubreactive.emailnotification.handler.InvoiceDocumentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class InvoiceDocumentRouter {

    @Bean
    public RouterFunction<ServerResponse> routeInvoiceDocument(InvoiceDocumentHandler invoiceDocumentHandler) {
        return RouterFunctions.nest(
                RequestPredicates.path("/invoices").and(RequestPredicates.accept(MediaType.APPLICATION_PDF)),
                RouterFunctions.route(
                        RequestPredicates.GET("/{invoiceId}/document"),
                        invoiceDocumentHandler::getInvoiceDocument
                )
        );
    }

}
