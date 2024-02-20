package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private KeycloakUserService keycloakUserService;

    @Test
    void findUserByUsernameTest_success() {
        UserInfo userInfo =
                TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        when(keycloakUserService.findUserByUsername(anyString())).thenReturn(userInfo);

        customerService.findUserByUsername("1")
                .as(StepVerifier::create)
                .expectNext(userInfo)
                .verifyComplete();
    }

    @Test
    void findUserByUsernameTest_errorOnFindingByUsername() {
        when(keycloakUserService.findUserByUsername(anyString())).thenThrow(new SwiftWheelsHubException(""));

        customerService.findUserByUsername("1")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
