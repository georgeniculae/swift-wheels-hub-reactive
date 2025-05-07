package com.autohubreactive.emailnotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.dto.emailnotification.EmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessorService {

    private final EmailService emailService;

    public Mono<EmailResponse> sendEmail(InvoiceResponse invoiceResponse) {
        return emailService.sendEmail(invoiceResponse.customerEmail(), invoiceResponse)
                .onErrorResume(e -> {
                    log.error("Exception while sending email: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
