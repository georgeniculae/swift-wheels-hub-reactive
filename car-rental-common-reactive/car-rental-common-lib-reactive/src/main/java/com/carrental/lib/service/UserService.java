package com.carrental.lib.service;

import com.carrental.document.model.User;
import com.carrental.dto.UserDto;
import com.carrental.lib.exceptionhandling.CarRentalResponseStatusException;
import com.carrental.lib.mapper.UserMapper;
import com.carrental.lib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "userConsumer", name = "enabled")
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Mono<UserDto> processSavedUser(UserDto userDto) {
        return userRepository.existsByUsername(userDto.getUsername())
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "User already exists"
                                )
                        )
                )
                .flatMap(existsByUsername -> userRepository.save(userMapper.mapDtoToEntity(userDto)))
                .map(userMapper::mapEntityToDto);
    }

    public Mono<UserDto> processUpdatedUser(UserDto userDto) {
        return userRepository.findByUsername(userDto.getUsername())
                .map(existingUser -> updateExistingUser(userDto, existingUser))
                .flatMap(userRepository::save)
                .map(userMapper::mapEntityToDto);
    }

    public Mono<Void> processDeletedUser(String username) {
        return userRepository.deleteByUsername(username);
    }

    private User updateExistingUser(UserDto userDto, User existingUser) {
        existingUser.setPassword(userDto.getPassword());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setDateOfBirth(userDto.getDateOfBirth());
        existingUser.setAddress(userDto.getAddress());

        return existingUser;
    }

}
