package com.swiftwheelshubreactive.emailnotification.service;

import com.swiftwheelshubreactive.dto.CustomerInfo;
import com.swiftwheelshubreactive.emailnotification.mapper.CustomerDetailsMapper;
import com.swiftwheelshubreactive.emailnotification.repository.CustomerDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerDetailsService {

    private final CustomerDetailsRepository customerDetailsRepository;
    private final CustomerDetailsMapper customerDetailsMapper;

    public Mono<Void> saveCustomerDetails(CustomerInfo customerInfo) {
        return customerDetailsRepository.save(customerDetailsMapper.mapToCustomerDetails(customerInfo))
                .then()
                .onErrorResume(e -> {
                    log.error("Exception while saving customer details: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
