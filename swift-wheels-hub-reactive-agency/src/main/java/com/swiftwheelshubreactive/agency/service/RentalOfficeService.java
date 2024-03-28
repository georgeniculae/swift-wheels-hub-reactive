package com.swiftwheelshubreactive.agency.service;

import com.swiftwheelshubreactive.agency.mapper.RentalOfficeMapper;
import com.swiftwheelshubreactive.agency.repository.BranchRepository;
import com.swiftwheelshubreactive.agency.repository.RentalOfficeRepository;
import com.swiftwheelshubreactive.dto.RentalOfficeRequest;
import com.swiftwheelshubreactive.dto.RentalOfficeResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.RentalOffice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalOfficeService {

    private final RentalOfficeRepository rentalOfficeRepository;
    private final BranchRepository branchRepository;
    private final RentalOfficeMapper rentalOfficeMapper;

    public Flux<RentalOfficeResponse> findAllRentalOffices() {
        return rentalOfficeRepository.findAll()
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all rental offices: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<RentalOfficeResponse> findRentalOfficeById(String id) {
        return findEntityById(id)
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding rental office by id: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Flux<RentalOfficeResponse> findRentalOfficesByFilterInsensitiveCase(String name) {
        return rentalOfficeRepository.findAllByFilterInsensitiveCase(name)
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding rental office by name: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                })
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Rental office with name: " + name + " does not exist"
                                )
                        )
                );
    }

    public Mono<RentalOfficeResponse> saveRentalOffice(RentalOfficeRequest rentalOfficeRequest) {
        return rentalOfficeRepository.save(rentalOfficeMapper.mapDtoToEntity(rentalOfficeRequest))
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving rental office: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<RentalOfficeResponse> updateRentalOffice(String id, RentalOfficeRequest updatedRentalOfficeRequest) {
        return findEntityById(id)
                .flatMap(existingRentalOffice -> {
                    existingRentalOffice.setName(updatedRentalOfficeRequest.name());
                    existingRentalOffice.setContactAddress(updatedRentalOfficeRequest.contactAddress());
                    existingRentalOffice.setPhoneNumber(updatedRentalOfficeRequest.phoneNumber());

                    return rentalOfficeRepository.save(existingRentalOffice);
                })
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating rental office: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<Long> countRentalOffices() {
        return rentalOfficeRepository.count()
                .onErrorMap(e -> {
                    log.error("Error while counting rental offices: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<Void> deleteRentalOfficeById(String id) {
        return rentalOfficeRepository.deleteById(MongoUtil.getObjectId(id))
                .then(Mono.defer(() -> branchRepository.deleteByRentalOfficeId(MongoUtil.getObjectId(id))))
                .onErrorMap(e -> {
                    log.error("Error while deleting rental office: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });

    }

    public Mono<RentalOffice> findEntityById(String id) {
        return rentalOfficeRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Rental office with id " + id + " does not exist")
                        )
                );
    }

}
