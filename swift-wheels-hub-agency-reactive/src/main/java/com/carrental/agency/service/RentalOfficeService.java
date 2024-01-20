package com.carrental.agency.service;

import com.carrental.agency.mapper.RentalOfficeMapper;
import com.carrental.agency.repository.RentalOfficeRepository;
import com.carrental.document.model.RentalOffice;
import com.carrental.dto.RentalOfficeDto;
import com.carrental.lib.exceptionhandling.CarRentalException;
import com.carrental.lib.exceptionhandling.CarRentalResponseStatusException;
import com.carrental.lib.util.MongoUtil;
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

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<RentalOfficeDto> findRentalOfficeById(String id) {
        return findEntityById(id)
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding rental office by id: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Flux<RentalOfficeDto> findRentalOfficesByNameInsensitiveCase(String name) {
        return rentalOfficeRepository.findAllByNameInsensitiveCase(name)
                .map(rentalOfficeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding rental office by name: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                })
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
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

                    return Mono.error(new CarRentalException(e.getMessage()));
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

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Long> countRentalOffices() {
        return rentalOfficeRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting rental offices: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Void> deleteRentalOfficeById(String id) {
        return rentalOfficeRepository.deleteById(MongoUtil.getObjectId(id))
                .onErrorResume(e -> {
                    log.error("Error while deleting rental office: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });

    }

    public Mono<RentalOffice> findEntityById(String id) {
        return rentalOfficeRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Rental office with id " + id + " does not exist")
                        )
                );
    }

}
