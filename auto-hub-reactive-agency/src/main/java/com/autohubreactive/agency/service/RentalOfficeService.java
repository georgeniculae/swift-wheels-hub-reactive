package com.autohubreactive.agency.service;

import com.autohubreactive.agency.mapper.RentalOfficeMapper;
import com.autohubreactive.agency.repository.BranchRepository;
import com.autohubreactive.agency.repository.RentalOfficeRepository;
import com.autohubreactive.dto.RentalOfficeRequest;
import com.autohubreactive.dto.RentalOfficeResponse;
import com.autohubreactive.exception.AutoHubNotFoundException;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import com.autohubreactive.lib.util.MongoUtil;
import com.autohubreactive.model.RentalOffice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<RentalOfficeResponse> findRentalOfficeById(String id) {
        return findEntityById(id)
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding rental office by id: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<RentalOfficeResponse> findRentalOfficesByFilterInsensitiveCase(String name) {
        return rentalOfficeRepository.findAllByFilterInsensitiveCase(name)
                .map(rentalOfficeMapper::mapEntityToDto)
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("Rental office with name: " + name + " does not exist")))
                .onErrorMap(e -> {
                    log.error("Error while finding rental office by name: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<RentalOfficeResponse> saveRentalOffice(RentalOfficeRequest rentalOfficeRequest) {
        return rentalOfficeRepository.save(rentalOfficeMapper.mapDtoToEntity(rentalOfficeRequest))
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving rental office: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<RentalOfficeResponse> updateRentalOffice(String id, RentalOfficeRequest updatedRentalOfficeRequest) {
        return findEntityById(id)
                .flatMap(existingRentalOffice -> {
                    RentalOffice updatedRentalOffice = rentalOfficeMapper.getUpdatedRentalOffice(existingRentalOffice, updatedRentalOfficeRequest);

                    updatedRentalOffice.setName(updatedRentalOfficeRequest.name());
                    updatedRentalOffice.setContactAddress(updatedRentalOfficeRequest.contactAddress());
                    updatedRentalOffice.setPhoneNumber(updatedRentalOfficeRequest.phoneNumber());

                    return rentalOfficeRepository.save(updatedRentalOffice);
                })
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating rental office: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Long> countRentalOffices() {
        return rentalOfficeRepository.count()
                .onErrorMap(e -> {
                    log.error("Error while counting rental offices: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Void> deleteRentalOfficeById(String id) {
        return rentalOfficeRepository.deleteById(MongoUtil.getObjectId(id))
                .then(Mono.defer(() -> branchRepository.deleteByRentalOfficeId(MongoUtil.getObjectId(id))))
                .onErrorMap(e -> {
                    log.error("Error while deleting rental office: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });

    }

    public Mono<RentalOffice> findEntityById(String id) {
        return rentalOfficeRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("Rental office with id " + id + " does not exist")));
    }

}
