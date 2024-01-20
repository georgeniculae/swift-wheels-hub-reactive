package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.RentalOfficeMapper;
import com.swiftwheelshub.agency.repository.RentalOfficeRepository;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.MongoUtil;
import com.swiftwheelshub.model.RentalOffice;
import com.carrental.dto.RentalOfficeDto;
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
    private final RentalOfficeMapper rentalOfficeMapper;

    public Flux<RentalOfficeDto> findAllRentalOffices() {
        return rentalOfficeRepository.findAll()
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all rental offices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<RentalOfficeDto> findRentalOfficeById(String id) {
        return findEntityById(id)
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding rental office by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<RentalOfficeDto> findRentalOfficesByNameInsensitiveCase(String name) {
        return rentalOfficeRepository.findAllByNameInsensitiveCase(name)
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding rental office by name: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
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

    public Mono<RentalOfficeDto> saveRentalOffice(RentalOfficeDto rentalOfficeDto) {
        return rentalOfficeRepository.save(rentalOfficeMapper.mapDtoToEntity(rentalOfficeDto))
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while saving rental office: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<RentalOfficeDto> updateRentalOffice(String id, RentalOfficeDto updatedRentalOfficeDto) {
        return findEntityById(id)
                .flatMap(existingRentalOffice -> {
                    existingRentalOffice.setName(updatedRentalOfficeDto.getName());
                    existingRentalOffice.setContactAddress(updatedRentalOfficeDto.getContactAddress());
                    existingRentalOffice.setLogoType(updatedRentalOfficeDto.getLogoType());

                    return rentalOfficeRepository.save(existingRentalOffice);
                })
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while updating rental office: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Long> countRentalOffices() {
        return rentalOfficeRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting rental offices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Void> deleteRentalOfficeById(String id) {
        return rentalOfficeRepository.deleteById(MongoUtil.getObjectId(id))
                .onErrorResume(e -> {
                    log.error("Error while deleting rental office: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
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
