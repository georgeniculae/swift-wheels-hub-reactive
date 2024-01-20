package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.BranchMapper;
import com.swiftwheelshub.agency.mapper.BranchMapperImpl;
import com.swiftwheelshub.agency.repository.BranchRepository;
import com.swiftwheelshub.agency.util.TestUtils;
import com.carrental.document.model.Branch;
import com.carrental.document.model.RentalOffice;
import com.carrental.dto.BranchDto;
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
import static org.mockito.Mockito.times;
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

    @Spy
    private BranchMapper branchMapper = new BranchMapperImpl();

    @Test
    void findAllBranchesTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        List<Branch> branches = List.of(branch);

        when(branchRepository.findAll()).thenReturn(Flux.fromIterable(branches));

        branchService.findAllBranches()
                .as(StepVerifier::create)
                .expectNext(branchDto)
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
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchService.findBranchById("64f361caf291ae086e179547"))
                .expectNext(branchDto)
                .verifyComplete();

        verify(branchMapper, times(1)).mapEntityToDto(any(Branch.class));
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
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        when(branchRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(branch));

        StepVerifier.create(branchService.findBranchesByFilterInsensitiveCase("search"))
                .expectNext(branchDto)
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
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchService.saveBranch(branchDto))
                .expectNext(branchDto)
                .verifyComplete();
    }

    @Test
    void saveBranchTest_errorOnSave() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(branchService.saveBranch(branchDto))
                .expectError()
                .verify();
    }

    @Test
    void updateBranchTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));
        when(branchRepository.save(branch)).thenReturn(Mono.just(branch));

        StepVerifier.create(branchService.updateBranch("64f361caf291ae086e179547", branchDto))
                .expectNext(branchDto)
                .verifyComplete();
    }

    @Test
    void updateBranchTest_errorOnSaving() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyString())).thenReturn(Mono.just(rentalOffice));
        when(branchRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(branch));
        when(branchRepository.save(branch)).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(branchService.updateBranch("64f361caf291ae086e179547", branchDto))
                .expectError()
                .verify();
    }

    @Test
    void deleteBranchByIdTest_success() {
        when(branchRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());

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
