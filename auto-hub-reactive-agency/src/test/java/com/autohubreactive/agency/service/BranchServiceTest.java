package com.autohubreactive.agency.service;

import com.autohubreactive.agency.mapper.BranchMapper;
import com.autohubreactive.agency.mapper.BranchMapperImpl;
import com.autohubreactive.agency.repository.BranchRepository;
import com.autohubreactive.agency.repository.EmployeeRepository;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.BranchRequest;
import com.autohubreactive.dto.BranchResponse;
import com.autohubreactive.model.agency.Branch;
import com.autohubreactive.model.agency.RentalOffice;
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
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

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

        branchService.findAllBranches()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findBranchByIdTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));

        branchService.findBranchById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectNext(branchResponse)
                .verifyComplete();

        verify(branchMapper).mapEntityToDto(any(Branch.class));
    }

    @Test
    void findBranchByIdTest_errorOnFindingById() {
        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        branchService.findBranchById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();

        verify(branchMapper, never()).mapEntityToDto(any(Branch.class));
    }

    @Test
    void findBranchByFilterTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        when(branchRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(branch));

        branchService.findBranchesByFilterInsensitiveCase("search")
                .as(StepVerifier::create)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    void findBranchByFilterTest_errorOnFindingByFilter() {
        when(branchRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        branchService.findBranchesByFilterInsensitiveCase("search")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void countBranchesTest_success() {
        when(branchRepository.count()).thenReturn(Mono.just(2L));

        branchService.countBranches()
                .as(StepVerifier::create)
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void countBranchesTest_errorOnCounting() {
        when(branchRepository.count()).thenReturn(Mono.error(new Throwable()));

        branchService.countBranches()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void saveBranchTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchRequest branchRequest =
                TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(branch));

        branchService.saveBranch(branchRequest)
                .as(StepVerifier::create)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    void saveBranchTest_errorOnSave() {
        BranchRequest branchRequest =
                TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.error(new Throwable()));

        branchService.saveBranch(branchRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateBranchTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchRequest branchRequest =
                TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(branch));

        branchService.updateBranch("64f361caf291ae086e179547", branchRequest)
                .as(StepVerifier::create)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    void updateBranchTest_errorOnSaving() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchRequest branchRequest =
                TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));
        when(branchRepository.save(branch)).thenReturn(Mono.error(new Throwable()));

        branchService.updateBranch("64f361caf291ae086e179547", branchRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteBranchByIdTest_success() {
        when(branchRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
        when(employeeRepository.deleteByBranchId(any(ObjectId.class))).thenReturn(Mono.empty());

        branchService.deleteBranchById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteBranchByIdTest_errorOnDeletingById() {
        when(branchRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        branchService.deleteBranchById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
