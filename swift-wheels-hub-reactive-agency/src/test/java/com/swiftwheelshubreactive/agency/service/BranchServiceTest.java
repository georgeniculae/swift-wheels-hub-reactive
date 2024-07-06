package com.swiftwheelshubreactive.agency.service;

import com.swiftwheelshubreactive.agency.mapper.BranchMapper;
import com.swiftwheelshubreactive.agency.mapper.BranchMapperImpl;
import com.swiftwheelshubreactive.agency.repository.BranchRepository;
import com.swiftwheelshubreactive.agency.repository.EmployeeRepository;
import com.swiftwheelshubreactive.agency.util.TestUtils;
import com.swiftwheelshubreactive.dto.BranchRequest;
import com.swiftwheelshubreactive.dto.BranchResponse;
import com.swiftwheelshubreactive.model.Branch;
import com.swiftwheelshubreactive.model.RentalOffice;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @InjectMocks
    private BranchService branchService;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private RentalOfficeService rentalOfficeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private BranchMapper branchMapper = new BranchMapperImpl();

    @Test
    void findAllBranchesTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        List<Branch> branches = List.of(branch);

        when(branchRepository.findAll()).thenReturn(Flux.fromIterable(branches));

        branchService.findAllBranches()
                .as(StepVerifier::create)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    void findAllBranchesTest_errorOnFindingBranches() {
        when(branchRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(branchService.findAllBranches())
                .expectError()
                .verify();
    }

    @Test
    void findBranchByIdTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchService.findBranchById("64f361caf291ae086e179547"))
                .expectNext(branchResponse)
                .verifyComplete();

        verify(branchMapper).mapEntityToDto(any(Branch.class));
    }

    @Test
    void findBranchByIdTest_errorOnFindingById() {
        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(branchService.findBranchById("64f361caf291ae086e179547"))
                .expectError()
                .verify();

        verify(branchMapper, never()).mapEntityToDto(any(Branch.class));
    }

    @Test
    void findBranchByFilterTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        when(branchRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(branch));

        StepVerifier.create(branchService.findBranchesByFilterInsensitiveCase("search"))
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    void findBranchByFilterTest_errorOnFindingByFilter() {
        when(branchRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(branchService.findBranchesByFilterInsensitiveCase("search"))
                .expectError()
                .verify();
    }

    @Test
    void countBranchesTest_success() {
        when(branchRepository.count()).thenReturn(Mono.just(2L));

        StepVerifier.create(branchService.countBranches())
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void countBranchesTest_errorOnCounting() {
        when(branchRepository.count()).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(branchService.countBranches())
                .expectError()
                .verify();
    }

    @Test
    void saveBranchTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchRequest branchRequest =
                TestUtils.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchService.saveBranch(branchRequest))
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    void saveBranchTest_errorOnSave() {
        BranchRequest branchRequest =
                TestUtils.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(branchService.saveBranch(branchRequest))
                .expectError()
                .verify();
    }

    @Test
    void updateBranchTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchRequest branchRequest =
                TestUtils.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchService.updateBranch("64f361caf291ae086e179547", branchRequest))
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    void updateBranchTest_errorOnSaving() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchRequest branchRequest =
                TestUtils.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));
        when(branchRepository.save(branch)).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(branchService.updateBranch("64f361caf291ae086e179547", branchRequest))
                .expectError()
                .verify();
    }

    @Test
    void deleteBranchByIdTest_success() {
        when(branchRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
        when(employeeRepository.deleteByBranchId(any(ObjectId.class))).thenReturn(Mono.empty());

        StepVerifier.create(branchService.deleteBranchById("64f361caf291ae086e179547"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteBranchByIdTest_errorOnDeletingById() {
        when(branchRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(branchService.deleteBranchById("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

}
