package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.BranchMapper;
import com.swiftwheelshub.agency.repository.BranchRepository;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.MongoUtil;
import com.swiftwheelshub.model.Branch;
import com.carrental.dto.BranchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {

    private final BranchRepository branchRepository;
    private final RentalOfficeService rentalOfficeService;
    private final BranchMapper branchMapper;

    public Flux<BranchDto> findAllBranches() {
        return branchRepository.findAll()
                .map(branchMapper::mapEntityToDto).onErrorResume(e -> {
                    log.error("Error while finding all branches: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BranchDto> findBranchById(String id) {
        return findEntityById(id)
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding branch by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<BranchDto> findBranchesByFilterInsensitiveCase(String filter) {
        return branchRepository.findAllByFilterInsensitiveCase(filter)
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding branch by filter: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                })
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Branch with filter: " + filter + " does not exist"
                                )
                        )
                );
    }

    public Mono<Long> countBranches() {
        return branchRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting branches: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BranchDto> saveBranch(BranchDto branchDto) {
        return rentalOfficeService.findEntityById(branchDto.getRentalOfficeId())
                .flatMap(rentalOffice -> {
                    Branch newBranch = branchMapper.mapDtoToEntity(branchDto);
                    newBranch.setRentalOffice(rentalOffice);

                    return branchRepository.save(newBranch);
                })
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while saving branch: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BranchDto> updateBranch(String id, BranchDto updatedBranchDto) {
        return findEntityById(id)
                .flatMap(exitingBranch ->
                        rentalOfficeService.findEntityById(updatedBranchDto.getRentalOfficeId())
                                .map(rentalOffice -> {
                                    exitingBranch.setName(updatedBranchDto.getName());
                                    exitingBranch.setAddress(updatedBranchDto.getAddress());
                                    exitingBranch.setRentalOffice(rentalOffice);

                                    return exitingBranch;
                                }))
                .flatMap(branchRepository::save)
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while updating branch: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Void> deleteBranchById(String id) {
        return branchRepository.deleteById(MongoUtil.getObjectId(id))
                .onErrorResume(e -> {
                    log.error("Error while deleting branch: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Branch> findEntityById(String id) {
        return branchRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Branch with id " + id + " does not exist"
                                )
                        )
                );
    }

}