package com.swiftwheelshubreactive.agency.service;

import com.swiftwheelshubreactive.agency.mapper.BranchMapper;
import com.swiftwheelshubreactive.agency.repository.BranchRepository;
import com.swiftwheelshubreactive.agency.repository.EmployeeRepository;
import com.swiftwheelshubreactive.dto.BranchRequest;
import com.swiftwheelshubreactive.dto.BranchResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.Branch;
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

                    return new SwiftWheelsHubException(e.getMessage());
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
                .onErrorMap(e -> {
                    log.error("Error while finding branch by filter: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                })
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("Branch with filter: " + filter + " does not exist")));
    }

    public Mono<Long> countBranches() {
        return branchRepository.count()
                .onErrorMap(e -> {
                    log.error("Error while counting branches: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
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
                        (existingBranch, rentalOffice) -> {
                            Branch updatedBranch = branchMapper.getNewBranchInstance(existingBranch);

                            updatedBranch.setName(branchRequest.name());
                            updatedBranch.setAddress(branchRequest.address());
                            updatedBranch.setRentalOffice(rentalOffice);

                            return updatedBranch;
                        }
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
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("Branch with id " + id + " does not exist")));
    }

}
