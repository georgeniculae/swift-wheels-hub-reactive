package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.BranchMapper;
import com.swiftwheelshub.agency.repository.BranchRepository;
import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.BranchResponse;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.MongoUtil;
import com.swiftwheelshub.model.Branch;
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

    public Flux<BranchResponse> findAllBranches() {
        return branchRepository.findAll()
                .map(branchMapper::mapEntityToDto).onErrorResume(e -> {
                    log.error("Error while finding all branches: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BranchResponse> findBranchById(String id) {
        return findEntityById(id)
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding branch by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<BranchResponse> findBranchesByFilterInsensitiveCase(String filter) {
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

    public Mono<BranchResponse> saveBranch(BranchRequest branchRequest) {
        return rentalOfficeService.findEntityById(branchRequest.rentalOfficeId())
                .flatMap(rentalOffice -> {
                    Branch newBranch = branchMapper.mapDtoToEntity(branchRequest);
                    newBranch.setRentalOffice(rentalOffice);

                    return branchRepository.save(newBranch);
                })
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while saving branch: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BranchResponse> updateBranch(String id, BranchRequest updatedBranchDto) {
        return findEntityById(id)
                .flatMap(exitingBranch ->
                        rentalOfficeService.findEntityById(updatedBranchDto.rentalOfficeId())
                                .map(rentalOffice -> {
                                    exitingBranch.setName(updatedBranchDto.name());
                                    exitingBranch.setAddress(updatedBranchDto.address());
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
