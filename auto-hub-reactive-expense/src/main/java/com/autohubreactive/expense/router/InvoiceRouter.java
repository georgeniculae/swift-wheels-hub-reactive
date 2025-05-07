package com.autohubreactive.expense.router;

import com.autohubreactive.expense.handler.InvoiceHandler;
import com.autohubreactive.expense.swaggeroperation.SwaggerRouteInvoiceOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class InvoiceRouter {

    private static final String REQUEST_MAPPING = "/invoices";

    @Bean
    @SwaggerRouteInvoiceOperation
    public RouterFunction<ServerResponse> routeInvoice(InvoiceHandler invoiceHandler) {
        return RouterFunctions.nest(RequestPredicates.path(REQUEST_MAPPING).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                RouterFunctions.route(RequestPredicates.GET(""), invoiceHandler::findAllInvoices)
                        .andRoute(RequestPredicates.GET("/active"), invoiceHandler::findAllActiveInvoices)
                        .andRoute(RequestPredicates.GET("/by-comments"), invoiceHandler::findInvoicesByComments)
                        .andRoute(RequestPredicates.GET("/by-customer/{customerUsername}"), invoiceHandler::findAllInvoicesByCustomerUsername)
                        .andRoute(RequestPredicates.GET("/count"), invoiceHandler::countInvoices)
                        .andRoute(RequestPredicates.GET("/count-active"), invoiceHandler::countAllActiveInvoices)
                        .andRoute(RequestPredicates.GET("/{id}"), invoiceHandler::findInvoiceById)
                        .andRoute(RequestPredicates.PUT("/{id}"), invoiceHandler::closeInvoice));
    }

}
