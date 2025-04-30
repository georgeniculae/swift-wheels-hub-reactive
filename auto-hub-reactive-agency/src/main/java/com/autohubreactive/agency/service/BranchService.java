package com.autohubreactive.agency.service;

import com.autohubreactive.agency.mapper.BranchMapper;
import com.autohubreactive.agency.repository.BranchRepository;
import com.autohubreactive.agency.repository.EmployeeRepository;
import com.autohubreactive.dto.BranchRequest;
import com.autohubreactive.dto.BranchResponse;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.exception.AutoHubNotFoundException;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import com.autohubreactive.lib.util.MongoUtil;
import com.autohubreactive.model.Branch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {

    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;
    private final RentalOfficeService rentalOfficeService;
    private final BranchMapper branchMapper;

    public Flux<BranchResponse> findAllBranches() {
        return branchRepository.findAll()
                .map(branchMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all branches: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
                });
    }

    public Mono<BranchResponse> findBranchById(String id) {
        return findEntityById(id)
                .map(branchMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding branch by id: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<BranchResponse> findBranchesByFilterInsensitiveCase(String filter) {
        return branchRepository.findAllByFilterInsensitiveCase(filter)
                .map(branchMapper::mapEntityToDto)
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("Branch with filter: " + filter + " does not exist")))
                .onErrorMap(e -> {
                    log.error("Error while finding branch by filter: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Long> countBranches() {
        return branchRepository.count()
                .onErrorMap(e -> {
                    log.error("Error while counting branches: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
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
                .onErrorMap(e -> {
                    log.error("Error while saving branch: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<BranchResponse> updateBranch(String id, BranchRequest branchRequest) {
        return Mono.zip(
                        findEntityById(id),
                        rentalOfficeService.findEntityById(branchRequest.rentalOfficeId()),
                        (existingBranch, rentalOffice) -> branchMapper.getUpdatedBranch(existingBranch, branchRequest, rentalOffice)
                )
                .flatMap(branchRepository::save)
                .map(branchMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating branch: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Void> deleteBranchById(String id) {
        return branchRepository.deleteById(MongoUtil.getObjectId(id))
                .then(Mono.defer(() -> employeeRepository.deleteByBranchId(MongoUtil.getObjectId(id))))
                .onErrorMap(e -> {
                    log.error("Error while deleting branch: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Branch> findEntityById(String id) {
        return branchRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("Branch with id " + id + " does not exist")));
    }

}
