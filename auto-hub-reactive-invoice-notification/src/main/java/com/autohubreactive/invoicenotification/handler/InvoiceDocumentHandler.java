package com.autohubreactive.invoicenotification.handler;

import com.autohubreactive.invoicenotification.service.InvoicePdfStorageService;
import com.autohubreactive.invoicenotification.util.Constants;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InvoiceDocumentHandler {

    private final InvoicePdfStorageService invoicePdfStorageService;

    public Mono<ServerResponse> getInvoiceDocument(ServerRequest serverRequest) {
        String invoiceId = serverRequest.pathVariable("invoiceId");

        return invoicePdfStorageService.retrievePdf(invoiceId)
                .flatMap(pdfBytes -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(invoiceId))
                        .bodyValue(pdfBytes))
                .onErrorResume(
                        AutoHubResponseStatusException.class,
                        e -> ServerResponse.status(e.getStatusCode()).build()
                );
    }

    private String getContentDisposition(String invoiceId) {
        return ContentDisposition.attachment()
                .filename(Constants.INVOICE_FILENAME_PREFIX + invoiceId + Constants.PDF_EXTENSION)
                .build()
                .toString();
    }

}
