package com.swiftwheelshub.lib.service;

import com.swiftwheelshub.lib.mapper.UserMapperImpl;
import com.swiftwheelshub.model.User;
import com.carrental.dto.UserDto;
import com.swiftwheelshub.lib.mapper.UserMapper;
import com.swiftwheelshub.lib.repository.UserRepository;
import com.swiftwheelshub.lib.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    void processSavedUserTest_success() {
        User user = TestUtils.getResourceAsJson("/data/UserDto.json", User.class);
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userService.processSavedUser(userDto))
                .expectNext(userDto)
                .verifyComplete();

        verify(userMapper).mapDtoToEntity(any(UserDto.class));
    }

    @Test
    void processSavedUserTest_error_existingUser() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(userService.processSavedUser(userDto))
                .expectError()
                .verify();
    }

    @Test
    void processUpdatedUserTest_success() {
        User user = TestUtils.getResourceAsJson("/data/UserDto.json", User.class);
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userService.processUpdatedUser(userDto))
                .expectNext(userDto)
                .verifyComplete();

        verify(userMapper).mapEntityToDto(any(User.class));
    }

}
