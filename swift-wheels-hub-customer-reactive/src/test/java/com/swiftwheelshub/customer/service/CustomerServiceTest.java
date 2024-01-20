package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.customer.mapper.CustomerMapper;
import com.swiftwheelshub.customer.mapper.CustomerMapperImpl;
import com.swiftwheelshub.customer.model.Outbox;
import com.swiftwheelshub.customer.util.AssertionUtils;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.CurrentUserDto;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.mapper.UserMapper;
import com.swiftwheelshub.lib.mapper.UserMapperImpl;
import com.swiftwheelshub.lib.repository.UserRepository;
import com.swiftwheelshub.lib.security.jwt.JwtService;
import com.swiftwheelshub.model.User;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.Month;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxService outboxService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private CustomerMapper customerMapper = new CustomerMapperImpl();

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    void registerUserTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);

        String token = "token";
        String encodedPassword = "$2a$12$ixPC0.X/n6gko36V356aS.CR0EOrPpRHv7Ez7jEoVnT.AnP3oa7xm";

        when(userRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(outboxService.saveOutbox(any(User.class), any(Outbox.Operation.class))).thenReturn(Mono.just(outbox));
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);

        StepVerifier.create(userService.registerUser(registerRequest))
                .expectNextMatches(actualResponse -> token.equals(actualResponse.getToken()))
                .verifyComplete();
    }

    @Test
    void registerUserTest_error_passwordTooShort() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);
        registerRequest.setPassword("123456");

        when(userRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(userService.registerUser(registerRequest))
                .expectError()
                .verify();
    }

    @Test
    void registerUserTest_error_existingUser() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(userService.registerUser(registerRequest))
                .expectError()
                .verify();
    }

    @Test
    void registerUserTest_error_customerUnder18YearsOld() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);
        registerRequest.setDateOfBirth(LocalDate.of(2015, Month.JANUARY, 11));

        when(userRepository.existsByUsername(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(userService.registerUser(registerRequest))
                .expectError()
                .verify();
    }

    @Test
    void getCurrentUserTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        CurrentUserDto currentUserDto =
                TestUtils.getResourceAsJson("/data/CurrentUserDto.json", CurrentUserDto.class);

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.getCurrentUser("alexandrupopescu"))
                .assertNext(actualCurrentUserDto -> AssertionUtils.assertCurrentUser(currentUserDto, user))
                .verifyComplete();

        verify(customerMapper, times(1)).mapUserToCurrentUserDto(any(User.class));
    }

    @Test
    void getCurrentUserTest_errorOnFindingByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(userService.getCurrentUser("alexandrupopescu"))
                .expectError(SwiftWheelsHubException.class)
                .verify();
    }

    @Test
    void findUserByUsernameTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
        String username = "admin";

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.findUserByUsername(username))
                .assertNext(actualUserDto -> AssertionUtils.assertUser(user, actualUserDto))
                .verifyComplete();
    }

    @Test
    void findUserByUsernameTest_errorOnFindingByUsername() {
        String username = "admin";

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(userService.findUserByUsername(username))
                .expectError()
                .verify();
    }

    @Test
    void countUsersTest_success() {
        when(userRepository.count()).thenReturn(Mono.just(5L));

        StepVerifier.create(userService.countUsers())
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    void countUsersTest_errorOnCounting() {
        when(userRepository.count()).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(userService.countUsers())
                .expectError()
                .verify();
    }

    @Test
    void updateUserTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.setOperation(Outbox.Operation.UPDATE);
        String id = "64f361caf291ae086e179547";
        String encodedPassword = "$2a$12$ixPC0.X/n6gko36V356aS.CR0EOrPpRHv7Ez7jEoVnT.AnP3oa7xm";

        when(userRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(outboxService.saveOutbox(any(User.class), any(Outbox.Operation.class))).thenReturn(Mono.just(outbox));

        StepVerifier.create(userService.updateUser(id, userDto))
                .assertNext(actualUserDto -> AssertionUtils.assertUser(user, actualUserDto))
                .verifyComplete();

        verify(userMapper, times(1)).mapEntityToDto(any(User.class));
    }

    @Test
    void updateUserTest_errorOnSavingUser() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        String id = "64f361caf291ae086e179547";

        when(userRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(userService.updateUser(id, userDto))
                .expectError(SwiftWheelsHubException.class)
                .verify();
    }

    @Test
    void updateUserTest_emptyResultOnFindingById() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        String id = "64f361caf291ae086e179547";

        when(userRepository.findById(any(ObjectId.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.updateUser(id, userDto))
                .expectError(SwiftWheelsHubException.class)
                .verify();
    }

    @Test
    void deleteUserByIdTest_success() {
        when(outboxService.processUserDeletion(anyString(), any(Outbox.Operation.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUserById("user"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteUserByIdTest_errorOnProcessingUserDeletion() {
        when(outboxService.processUserDeletion(anyString(), any(Outbox.Operation.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(userService.deleteUserById("user"))
                .expectError()
                .verify();
    }

}
