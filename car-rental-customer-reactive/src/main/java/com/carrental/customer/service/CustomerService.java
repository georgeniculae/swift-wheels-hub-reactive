package com.carrental.customer.service;

import com.carrental.customer.mapper.CustomerMapper;
import com.carrental.customer.model.Outbox;
import com.carrental.document.dto.CurrentUserDto;
import com.carrental.document.model.Role;
import com.carrental.document.model.User;
import com.carrental.dto.AuthenticationResponse;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserDto;
import com.carrental.lib.aspect.LogActivity;
import com.carrental.lib.exceptionhandling.CarRentalException;
import com.carrental.lib.exceptionhandling.CarRentalResponseStatusException;
import com.carrental.lib.mapper.UserMapper;
import com.carrental.lib.repository.UserRepository;
import com.carrental.lib.security.jwt.JwtService;
import com.carrental.lib.util.MongoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;
    private final OutboxService outboxService;

    @Transactional
    @LogActivity(
            activityDescription = "User registration",
            sentParameters = "registerRequest"
    )
    public Mono<AuthenticationResponse> registerUser(RegisterRequest registerRequest) {
        return validateRegisterRequest(registerRequest)
                .map(userMapper::mapRegisterRequestToUser)
                .flatMap(this::saveNewUser)
                .flatMap(savedUser -> outboxService.saveOutbox(savedUser, Outbox.Operation.CREATE))
                .map(outbox -> jwtService.generateToken(outbox.getContent()))
                .map(token -> new AuthenticationResponse().token(token))
                .onErrorResume(e -> {
                    log.error("Error while registering user: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<CurrentUserDto> getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .map(customerMapper::mapUserToCurrentUserDto)
                .onErrorResume(e -> {
                    log.error("Error while getting current user: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<UserDto> findUserByUsername(String username) {
        return findByUsername(username)
                .map(customerMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding by username: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Long> countUsers() {
        return userRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting users: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    @Transactional
    @LogActivity(
            sentParameters = {"id", "userDto"},
            activityDescription = "User update"
    )
    public Mono<UserDto> updateUser(String id, UserDto userDto) {
        return findEntityById(id)
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    user.setFirstName(userDto.getFirstName());
                    user.setLastName(userDto.getLastName());
                    user.setEmail(userDto.getEmail());
                    user.setDateOfBirth(userDto.getDateOfBirth());
                    user.setAddress(userDto.getAddress());

                    return saveEntity(user);
                })
                .flatMap(this::processOutboxForUpdate)
                .map(userMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding by id: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    @LogActivity(
            activityDescription = "User deletion",
            sentParameters = "username"
    )
    public Mono<Void> deleteUserById(String username) {
        return outboxService.processUserDeletion(username, Outbox.Operation.DELETE)
                .onErrorResume(e -> {
                    log.error("Error while deleting user: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e));
                });
    }

    private Mono<User> saveNewUser(User user) {
        user.setRole(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return saveEntity(user);
    }

    private Mono<User> saveEntity(User user) {
        return userRepository.save(user);
    }

    private Mono<User> findEntityById(String id) {
        return userRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User with id " + id + " doesn't exist"
                                )
                        )
                );
    }

    private Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User with username " + username + " doesn't exist"
                                )
                        )
                );
    }

    private Mono<RegisterRequest> validateRegisterRequest(RegisterRequest request) {
        return userRepository.existsByUsername(request.getUsername())
                .flatMap(present -> {
                    if (present) {
                        return Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "User already exists"
                                )
                        );
                    }

                    String password = Optional.ofNullable(request.getPassword()).orElseThrow();
                    if (password.length() < 8) {
                        return Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Password too short"
                                )
                        );
                    }

                    LocalDate dateOfBirth = Optional.ofNullable(request.getDateOfBirth()).orElseThrow();
                    if (Period.between(dateOfBirth, LocalDate.now()).getYears() < 18) {
                        return Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Customer is under 18 years old"
                                )
                        );
                    }

                    return Mono.just(request);
                });
    }

    private Mono<User> processOutboxForUpdate(User user) {
        return outboxService.saveOutbox(user, Outbox.Operation.UPDATE)
                .map(Outbox::getContent);
    }

}
