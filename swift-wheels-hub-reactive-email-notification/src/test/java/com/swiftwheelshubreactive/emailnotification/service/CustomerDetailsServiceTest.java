package com.swiftwheelshubreactive.emailnotification.service;

import com.swiftwheelshubreactive.dto.CustomerInfo;
import com.swiftwheelshubreactive.emailnotification.mapper.CustomerDetailsMapper;
import com.swiftwheelshubreactive.emailnotification.mapper.CustomerDetailsMapperImpl;
import com.swiftwheelshubreactive.emailnotification.model.CustomerDetails;
import com.swiftwheelshubreactive.emailnotification.repository.CustomerDetailsRepository;
import com.swiftwheelshubreactive.emailnotification.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDetailsServiceTest {

    @InjectMocks
    private CustomerDetailsService customerDetailsService;

    @Mock
    private CustomerDetailsRepository customerDetailsRepository;

    @Spy
    private CustomerDetailsMapper customerDetailsMapper = new CustomerDetailsMapperImpl();

    @Test
    void saveCustomerDetailsTest_success() {
        CustomerDetails customerDetails =
                TestUtil.getResourceAsJson("/data/CustomerDetails.json", CustomerDetails.class);

        CustomerInfo customerInfo =
                TestUtil.getResourceAsJson("/data/CustomerInfo.json", CustomerInfo.class);

        when(customerDetailsRepository.save(any(CustomerDetails.class))).thenReturn(Mono.just(customerDetails));

        customerDetailsService.saveCustomerDetails(customerInfo)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(customerDetailsMapper).mapToCustomerDetails(any(CustomerInfo.class));
    }

    @Test
    void saveCustomerDetailsTest_errorOnSaving() {
        CustomerInfo customerInfo =
                TestUtil.getResourceAsJson("/data/CustomerInfo.json", CustomerInfo.class);

        when(customerDetailsRepository.save(any(CustomerDetails.class))).thenReturn(Mono.error(new RuntimeException()));

        customerDetailsService.saveCustomerDetails(customerInfo)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
