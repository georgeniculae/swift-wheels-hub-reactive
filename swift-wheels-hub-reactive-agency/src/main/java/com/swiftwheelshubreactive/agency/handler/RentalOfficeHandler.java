package com.swiftwheelshubreactive.agency.handler;

import com.swiftwheelshubreactive.agency.service.RentalOfficeService;
import com.swiftwheelshubreactive.agency.validator.RentalOfficeRequestValidator;
import com.swiftwheelshubreactive.dto.RentalOfficeRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RentalOfficeHandler {

    private static final String ID = "id";
    private static final String FILTER = "filter";
    private final RentalOfficeService rentalOfficeService;
    private final RentalOfficeRequestValidator rentalOfficeRequestValidator;

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> findAllRentalOffices(ServerRequest serverRequest) {
        return rentalOfficeService.findAllRentalOffices()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(rentalOfficeResponses -> ServerResponse.ok().bodyValue(rentalOfficeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findRentalOfficeById(ServerRequest serverRequest) {
        return rentalOfficeService.findRentalOfficeById(serverRequest.pathVariable(ID))
                .flatMap(rentalOfficeResponse -> ServerResponse.ok().bodyValue(rentalOfficeResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> findRentalOfficesByFilterInsensitiveCase(ServerRequest serverRequest) {
        return rentalOfficeService.findRentalOfficesByFilterInsensitiveCase(serverRequest.pathVariable(FILTER))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(rentalOfficeResponses -> ServerResponse.ok().bodyValue(rentalOfficeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> countRentalOffices(ServerRequest serverRequest) {
        return rentalOfficeService.countRentalOffices()
                .flatMap(numberOfRentalOffices -> ServerResponse.ok().bodyValue(numberOfRentalOffices));
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> saveRentalOffice(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RentalOfficeRequest.class)
                .flatMap(rentalOfficeRequestValidator::validateBody)
                .flatMap(rentalOfficeService::saveRentalOffice)
                .flatMap(rentalOfficeResponse -> ServerResponse.ok().bodyValue(rentalOfficeResponse));
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> updateRentalOffice(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RentalOfficeRequest.class)
                .flatMap(rentalOfficeRequestValidator::validateBody)
                .flatMap(rentalOfficeRequest -> rentalOfficeService.updateRentalOffice(
                                serverRequest.pathVariable(ID),
                                rentalOfficeRequest
                        )
                )
                .flatMap(rentalOfficeResponse -> ServerResponse.ok().bodyValue(rentalOfficeResponse));
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> deleteRentalOfficeById(ServerRequest serverRequest) {
        return rentalOfficeService.deleteRentalOfficeById(serverRequest.pathVariable(ID))
                .then(ServerResponse.noContent().build());
    }

}
