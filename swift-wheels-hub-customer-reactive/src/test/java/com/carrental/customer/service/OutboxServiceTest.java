package com.carrental.customer.service;

import com.carrental.customer.model.Outbox;
import com.carrental.customer.repository.OutboxRepository;
import com.carrental.customer.util.TestUtils;
import com.swiftwheelshub.lib.mapper.UserMapper;
import com.swiftwheelshub.lib.mapper.UserMapperImpl;
import com.swiftwheelshub.lib.repository.UserRepository;
import com.swiftwheelshub.model.User;
import com.carrental.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @InjectMocks
    private OutboxService outboxService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SavedUserProducerService savedUserProducerService;

    @Mock
    private UpdatedUserProducerService updatedUserProducerService;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    void handleOutboxesTest_successCreateOperation() {
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(savedUserProducerService.sendSavedUser(any(UserDto.class))).thenReturn(Mono.just(true));
        when(outboxRepository.delete(any(Outbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.handleOutboxes())
                .expectComplete()
                .verify();

        verify(userMapper, times(1)).mapEntityToDto(any(User.class));
    }

    @Test
    void handleOutboxesTest_errorOnSendingUser() {
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(savedUserProducerService.sendSavedUser(any(UserDto.class))).thenReturn(Mono.just(false));

        StepVerifier.create(outboxService.handleOutboxes())
                .expectError()
                .verify();

        verify(userMapper, times(1)).mapEntityToDto(any(User.class));
    }

    @Test
    void handleOutboxesTest_successUpdateOperation() {
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        outbox.setOperation(Outbox.Operation.UPDATE);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(updatedUserProducerService.sendUpdatedUser(any(UserDto.class))).thenReturn(Mono.just(true));
        when(outboxRepository.delete(any(Outbox.class))).thenReturn(Mono.empty());

        StepVerifier.create(outboxService.handleOutboxes())
                .expectComplete()
                .verify();
    }

    @Test
    void handleOutboxesTest_errorOnFindingAll() {
        when(outboxRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(outboxService.handleOutboxes())
                .expectError()
                .verify();
    }

    @Test
    void saveOutboxTest_success() {
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(outbox));

        StepVerifier.create(outboxService.saveOutbox(user, Outbox.Operation.CREATE))
                .expectNext(outbox)
                .verifyComplete();
    }

    @Test
    void deleteUserByIdTest_success() {
        Outbox outbox = TestUtils.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(userRepository.deleteByUsername(anyString())).thenReturn(Mono.empty());
        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(outbox));

        StepVerifier.create(outboxService.processUserDeletion("user", Outbox.Operation.DELETE))
                .expectComplete()
                .verify();
    }

}
