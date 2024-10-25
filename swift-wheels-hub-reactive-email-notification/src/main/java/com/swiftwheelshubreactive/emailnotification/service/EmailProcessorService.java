package com.swiftwheelshubreactive.emailnotification.service;

import com.swiftwheelshubreactive.dto.EmailResponse;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.emailnotification.repository.CustomerDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProcessorService {

    private final CustomerDetailsRepository customerDetailsRepository;
    private final EmailService emailService;

    public Mono<EmailResponse> sendEmail(InvoiceResponse invoiceResponse) {
        return customerDetailsRepository.findByUsername(invoiceResponse.customerUsername())
                .flatMap(customerDetails -> emailService.sendEmail(customerDetails.getEmail(), invoiceResponse))
                .onErrorResume(e -> {
                    log.error("Exception while sending email: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
