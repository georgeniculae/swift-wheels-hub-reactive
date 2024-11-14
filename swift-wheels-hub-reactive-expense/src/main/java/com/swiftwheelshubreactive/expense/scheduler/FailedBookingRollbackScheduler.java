package com.swiftwheelshubreactive.expense.scheduler;

import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.BookingRollbackResponse;
import com.swiftwheelshubreactive.expense.model.FailedBookingRollback;
import com.swiftwheelshubreactive.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshubreactive.expense.service.BookingService;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedBookingRollbackScheduler {

    private final FailedBookingRollbackRepository failedBookingRollbackRepository;
    private final BookingService bookingService;

    @Value("${apikey.secret}")
    private String apikey;

    @Value("${apikey.machine-role}")
    private String machineRole;

    @Scheduled(fixedDelay = 5000L)
    public void processFailedBookingRollback() {
        failedBookingRollbackRepository.findAll()
                .flatMap(this::getRollbackBooking)
                .filter(BookingRollbackResponse::isSuccessful)
                .flatMap(this::deleteFailedBooking)
                .subscribe();
    }

    private Mono<BookingRollbackResponse> getRollbackBooking(FailedBookingRollback failedBookingRollback) {
        return bookingService.rollbackBooking(getAuthenticationInfo(), failedBookingRollback.getBookingId(), 0);
    }

    private Mono<Void> deleteFailedBooking(BookingRollbackResponse bookingRollbackResponse) {
        return failedBookingRollbackRepository.deleteById(MongoUtil.getObjectId(bookingRollbackResponse.bookingId()));
    }

    private AuthenticationInfo getAuthenticationInfo() {
        return AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(getRoles())
                .build();
    }

    private List<String> getRoles() {
        return List.of(machineRole);
    }

}
