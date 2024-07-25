package com.swiftwheelshubreactive.customer.service;

import com.swiftwheelshubreactive.customer.util.TestUtil;
import com.swiftwheelshubreactive.dto.RegisterRequest;
import com.swiftwheelshubreactive.dto.RegistrationResponse;
import com.swiftwheelshubreactive.dto.RequestDetails;
import com.swiftwheelshubreactive.dto.UserInfo;
import com.swiftwheelshubreactive.dto.UserUpdateRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private KeycloakUserService keycloakUserService;

    @Mock
    private BookingService bookingService;

    @Test
    void findUserByUsernameTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

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

    @Test
    void getCurrentUserTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        when(keycloakUserService.getCurrentUser(anyString())).thenReturn(userInfo);

        customerService.getCurrentUser("user")
                .as(StepVerifier::create)
                .expectNext(userInfo)
                .verifyComplete();
    }

    @Test
    void getCurrentUserTest_errorOnGettingUser() {
        when(keycloakUserService.getCurrentUser(anyString())).thenThrow(new SwiftWheelsHubException(""));

        customerService.getCurrentUser("user")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void countUsersTest_success() {
        when(keycloakUserService.countUsers()).thenReturn(1);

        customerService.countUsers()
                .as(StepVerifier::create)
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void countUsersTest_errorOnCountingUsers() {
        when(keycloakUserService.countUsers()).thenThrow(new SwiftWheelsHubException(""));

        customerService.countUsers()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void registerUserTest_success() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequestPasswordTooShort.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtil.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        when(keycloakUserService.registerCustomer(any(RegisterRequest.class))).thenReturn(registrationResponse);

        customerService.registerUser(registerRequest)
                .as(StepVerifier::create)
                .expectNext(registrationResponse)
                .verifyComplete();
    }

    @Test
    void registerUserTest_errorOnRegisteringCustomer() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequestPasswordTooShort.json", RegisterRequest.class);

        when(keycloakUserService.registerCustomer(any(RegisterRequest.class))).thenThrow(new SwiftWheelsHubException(""));

        customerService.registerUser(registerRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateUserTest_success() {
        UserUpdateRequest userUpdateRequest =
                TestUtil.getResourceAsJson("/data/UserUpdateRepresentation.json", UserUpdateRequest.class);

        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        when(keycloakUserService.updateUser(anyString(), any(UserUpdateRequest.class))).thenReturn(userInfo);

        customerService.updateUser("1", userUpdateRequest)
                .as(StepVerifier::create)
                .expectNext(userInfo)
                .verifyComplete();
    }

    @Test
    void updateUserTest_errorOnUpdatingUser() {
        UserUpdateRequest userUpdateRequest =
                TestUtil.getResourceAsJson("/data/UserUpdateRepresentation.json", UserUpdateRequest.class);

        when(keycloakUserService.updateUser(anyString(), any(UserUpdateRequest.class))).thenThrow(new SwiftWheelsHubException(""));

        customerService.updateUser("1", userUpdateRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteUserByUsernameTest_success() {
        RequestDetails requestDetails = RequestDetails.builder()
                .apikey("apikey")
                .roles(List.of("admin"))
                .build();

        doNothing().when(keycloakUserService).deleteUserByUsername(anyString());
        when(bookingService.deleteBookingsByUsername(any(RequestDetails.class), anyString())).thenReturn(Mono.empty());

        customerService.deleteUserByUsername(requestDetails, "user")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteUserByUsernameTest_errorOnDeleting() {
        RequestDetails requestDetails = RequestDetails.builder()
                .apikey("apikey")
                .roles(List.of("admin"))
                .build();

        doThrow(new SwiftWheelsHubException("error")).when(keycloakUserService).deleteUserByUsername(anyString());

        customerService.deleteUserByUsername(requestDetails, "user")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void signOutTest_success() {
        doNothing().when(keycloakUserService).signOut(anyString());

        customerService.signOut("1")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void signOutTest_errorOnDeleting() {
        doThrow(new SwiftWheelsHubException("")).when(keycloakUserService).signOut(anyString());

        customerService.signOut("1")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
