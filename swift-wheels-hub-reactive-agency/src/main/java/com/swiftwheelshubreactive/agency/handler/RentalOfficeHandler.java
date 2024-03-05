package com.swiftwheelshubreactive.agency.handler;

import com.swiftwheelshubreactive.agency.service.RentalOfficeService;
import com.swiftwheelshubreactive.dto.RentalOfficeRequest;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RentalOfficeHandler {

    private static final String ID = "id";
    private static final String NAME = "name";
    private final RentalOfficeService rentalOfficeService;

    public Mono<ServerResponse> findAllRentalOffices(ServerRequest serverRequest) {
        return rentalOfficeService.findAllRentalOffices()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(rentalOfficeResponses -> ServerResponse.ok().bodyValue(rentalOfficeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findRentalOfficeById(ServerRequest serverRequest) {
        return rentalOfficeService.findRentalOfficeById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(rentalOfficeResponse -> ServerResponse.ok().bodyValue(rentalOfficeResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findRentalOfficesByNameInsensitiveCase(ServerRequest serverRequest) {
        return rentalOfficeService.findRentalOfficesByNameInsensitiveCase(ServerRequestUtil.getPathVariable(serverRequest, NAME))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(rentalOfficeResponses -> ServerResponse.ok().bodyValue(rentalOfficeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> countRentalOffices(ServerRequest serverRequest) {
        return rentalOfficeService.countRentalOffices()
                .flatMap(numberOfRentalOffices -> ServerResponse.ok().bodyValue(numberOfRentalOffices));
    }

    public Mono<ServerResponse> saveRentalOffice(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RentalOfficeRequest.class)
                .flatMap(rentalOfficeService::saveRentalOffice)
                .flatMap(rentalOfficeResponse -> ServerResponse.ok().bodyValue(rentalOfficeResponse));
    }

    public Mono<ServerResponse> updateRentalOffice(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RentalOfficeRequest.class)
                .flatMap(rentalOfficeRequest ->
                        rentalOfficeService.updateRentalOffice(ServerRequestUtil.getPathVariable(serverRequest, ID), rentalOfficeRequest))
                .flatMap(rentalOfficeResponse -> ServerResponse.ok().bodyValue(rentalOfficeResponse));
    }

    public Mono<ServerResponse> deleteRentalOfficeById(ServerRequest serverRequest) {
        return rentalOfficeService.deleteRentalOfficeById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .then(ServerResponse.noContent().build());
    }

}
