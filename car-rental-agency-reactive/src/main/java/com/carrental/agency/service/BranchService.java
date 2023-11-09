package com.carrental.agency.service;

import com.carrental.agency.mapper.BranchMapper;
import com.carrental.agency.repository.BranchRepository;
import com.carrental.document.model.Branch;
import com.carrental.dto.BranchDto;
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
public class BranchService {

    private final BranchRepository branchRepository;
    private final RentalOfficeService rentalOfficeService;
    private final BranchMapper branchMapper;

    public Flux<BranchDto> findAllBranches() {
        return branchRepository.findAll()
                .map(branchMapper::mapEntityToDto).onErrorResume(e -> {
                    log.error("Error while finding all branches: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<BranchDto> findBranchById(String id) {
        return findEntityById(id)
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding branch by id: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Flux<BranchDto> findBranchesByFilterInsensitiveCase(String filter) {
        return branchRepository.findAllByFilterInsensitiveCase(filter)
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding branch by filter: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                })
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
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

                    return Mono.error(new CarRentalException(e.getMessage()));
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

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<BranchDto> updateBranch(String id, BranchDto updatedBranchDto) {
        return findEntityById(id)
                .flatMap(exitingBranch ->
                        rentalOfficeService.findEntityById(updatedBranchDto.getRentalOfficeId())
                                .flatMap(rentalOffice -> {
                                    exitingBranch.setName(updatedBranchDto.getName());
                                    exitingBranch.setAddress(updatedBranchDto.getAddress());
                                    exitingBranch.setRentalOffice(rentalOffice);

                                    return branchRepository.save(exitingBranch);
                                }))
                .map(branchMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while updating branch: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Void> deleteBranchById(String id) {
        return branchRepository.deleteById(MongoUtil.getObjectId(id))
                .onErrorResume(e -> {
                    log.error("Error while deleting branch: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Branch> findEntityById(String id) {
        return branchRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Branch with id " + id + " does not exist"
                                )
                        )
                );
    }

}
