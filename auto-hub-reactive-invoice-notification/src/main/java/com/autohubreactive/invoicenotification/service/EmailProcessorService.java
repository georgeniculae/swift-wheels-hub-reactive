package com.autohubreactive.invoicenotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.dto.invoicenotification.EmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessorService {

    private final EmailService emailService;
    private final PdfService pdfService;
    private final InvoicePdfStorageService invoicePdfStorageService;

    public Mono<EmailResponse> sendEmail(InvoiceResponse invoiceResponse) {
        return pdfService.generatePdf(invoiceResponse)
                .flatMap(pdfBytes -> invoicePdfStorageService.savePdf(invoiceResponse, pdfBytes)
                        .thenReturn(pdfBytes))
                .flatMap(pdfBytes -> emailService.sendEmail(
                        invoiceResponse.customerEmail(),
                        invoiceResponse,
                        pdfBytes
                ))
                .onErrorResume(e -> {
                    log.error("Exception while sending email: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
