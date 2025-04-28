package com.autohubreactive.customer.service;

import com.autohubreactive.customer.util.TestUtil;
import com.autohubreactive.dto.RegisterRequest;
import com.autohubreactive.dto.RegistrationResponse;
import com.autohubreactive.dto.UserInfo;
import com.autohubreactive.dto.UserUpdateRequest;
import com.autohubreactive.exception.AutoHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    private UsernameProducerService usernameProducerService;

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
        when(keycloakUserService.findUserByUsername(anyString())).thenThrow(new AutoHubException(""));

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
        when(keycloakUserService.getCurrentUser(anyString())).thenThrow(new AutoHubException(""));

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
        when(keycloakUserService.countUsers()).thenThrow(new AutoHubException(""));

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

        when(keycloakUserService.registerCustomer(any(RegisterRequest.class))).thenThrow(new AutoHubException(""));

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

        when(keycloakUserService.updateUser(anyString(), any(UserUpdateRequest.class))).thenThrow(new AutoHubException(""));

        customerService.updateUser("1", userUpdateRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteUserByUsernameTest_success() {
        doNothing().when(keycloakUserService).deleteUserByUsername(anyString());
        when(usernameProducerService.sendUsername(anyString())).thenReturn(Mono.empty());

        customerService.deleteUserByUsername("user")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteUserByUsernameTest_errorOnDeleting() {
        doThrow(new AutoHubException("error")).when(keycloakUserService).deleteUserByUsername(anyString());

        customerService.deleteUserByUsername("user")
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
        doThrow(new AutoHubException("")).when(keycloakUserService).signOut(anyString());

        customerService.signOut("1")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
