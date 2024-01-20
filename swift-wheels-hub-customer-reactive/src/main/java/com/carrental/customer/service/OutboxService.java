package com.carrental.customer.service;

import com.carrental.customer.model.Outbox;
import com.carrental.customer.repository.OutboxRepository;
import com.carrental.document.model.User;
import com.carrental.dto.UserDto;
import com.carrental.lib.exceptionhandling.CarRentalException;
import com.carrental.lib.mapper.UserMapper;
import com.carrental.lib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final UserRepository userRepository;
    private final SavedUserProducerService savedUserProducerService;
    private final UpdatedUserProducerService updatedUserProducerService;
    private final DeletedUserProducerService deletedUserProducerService;
    private final UserMapper userMapper;

    public Flux<Void> handleOutboxes() {
        return outboxRepository.findAll()
                .concatMap(this::processUser)
                .concatMap(outboxRepository::delete)
                .onErrorResume(e -> {
                    log.error("Error while processing/sending user: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Void> processUserDeletion(String username, Outbox.Operation operation) {
        return userRepository.deleteByUsername(username)
                .then(saveOutbox(User.builder().username(username).build(), operation))
                .then();
    }

    public Mono<Outbox> saveOutbox(User user, Outbox.Operation operation) {
        Outbox outbox = Outbox.builder()
                .operation(operation)
                .content(user)
                .build();

        return outboxRepository.save(outbox);
    }

    private Mono<Outbox> processUser(Outbox outbox) {
        return Mono.just(outbox)
                .flatMap(createdOutbox -> sendUserToCorrespondingTopic(outbox, createdOutbox))
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new CarRentalException("Sending user failed")))
                .map(response -> outbox);
    }

    private Mono<Boolean> sendUserToCorrespondingTopic(Outbox outbox, Outbox createdOutbox) {
        if (Outbox.Operation.CREATE.equals(createdOutbox.getOperation())) {
            return savedUserProducerService.sendSavedUser(getUserDto(outbox));
        }

        if (Outbox.Operation.UPDATE.equals(createdOutbox.getOperation())) {
            return updatedUserProducerService.sendUpdatedUser(getUserDto(outbox));
        }

        return deletedUserProducerService.sendDeletedUser(outbox.getContent().getUsername());
    }

    private UserDto getUserDto(Outbox outbox) {
        return userMapper.mapEntityToDto(outbox.getContent());
    }

}
