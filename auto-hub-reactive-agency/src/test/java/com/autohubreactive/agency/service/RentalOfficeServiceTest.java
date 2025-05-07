package com.autohubreactive.agency.service;

import com.autohubreactive.agency.mapper.RentalOfficeMapper;
import com.autohubreactive.agency.mapper.RentalOfficeMapperImpl;
import com.autohubreactive.agency.repository.BranchRepository;
import com.autohubreactive.agency.repository.RentalOfficeRepository;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.agency.RentalOfficeRequest;
import com.autohubreactive.dto.agency.RentalOfficeResponse;
import com.autohubreactive.model.agency.RentalOffice;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalOfficeServiceTest {

    @InjectMocks
    private RentalOfficeService rentalOfficeService;

    @Mock
    private RentalOfficeRepository rentalOfficeRepository;

    @Mock
    private BranchRepository branchRepository;

    @Spy
    private RentalOfficeMapper rentalOfficeMapper = new RentalOfficeMapperImpl();

    @Captor
    private ArgumentCaptor<RentalOffice> argumentCaptor = ArgumentCaptor.forClass(RentalOffice.class);

    @Test
    void findAllRentalOfficesTest_success() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        List<RentalOffice> rentalOffices = List.of(rentalOffice);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.findAll()).thenReturn(Flux.fromIterable(rentalOffices));

        rentalOfficeService.findAllRentalOffices()
                .as(StepVerifier::create)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    void findAllRentalOfficesTest_errorOnFindingAllRentalOffices() {
        when(rentalOfficeRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        rentalOfficeService.findAllRentalOffices()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findRentalOfficeByIdTest_success() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));

        rentalOfficeService.findRentalOfficeById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByIdTest_errorOnFindingById() {
        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        rentalOfficeService.findRentalOfficeById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void countRentalOfficesTest_success() {
        when(rentalOfficeRepository.count()).thenReturn(Mono.just(3L));

        rentalOfficeService.countRentalOffices()
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countRentalOfficesTest_errorOnCount() {
        when(rentalOfficeRepository.count()).thenReturn(Mono.error(new Throwable()));

        rentalOfficeService.countRentalOffices()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void saveRentalOfficeTest_success() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.just(rentalOffice));

        rentalOfficeService.saveRentalOffice(rentalOfficeRequest)
                .as(StepVerifier::create)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();

        verify(rentalOfficeRepository).save(argumentCaptor.capture());
        verify(rentalOfficeMapper).mapEntityToDto(any(RentalOffice.class));
    }

    @Test
    void saveRentalOfficeTest_errorOnSaving() {
        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.error(new Throwable()));

        rentalOfficeService.saveRentalOffice(rentalOfficeRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateRentalOfficeTest_success() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));
        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.just(rentalOffice));

        rentalOfficeService.updateRentalOffice("64f361caf291ae086e179547", rentalOfficeRequest)
                .as(StepVerifier::create)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    void updateRentalOfficeTest_errorOnSaving() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));
        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.error(new Throwable()));

        rentalOfficeService.updateRentalOffice("64f361caf291ae086e179547", rentalOfficeRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findRentalOfficeByNameTest_success() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(rentalOffice));

        rentalOfficeService.findRentalOfficesByFilterInsensitiveCase("name")
                .as(StepVerifier::create)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByNameTest_errorOnFindingByName() {
        when(rentalOfficeRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        rentalOfficeService.findRentalOfficesByFilterInsensitiveCase("name")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteRentalOfficeByIdTest_success() {
        when(rentalOfficeRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
        when(branchRepository.deleteByRentalOfficeId(any(ObjectId.class))).thenReturn(Mono.empty());

        rentalOfficeService.deleteRentalOfficeById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteRentalOfficeByIdTest_errorOnDeletingById() {
        when(rentalOfficeRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        rentalOfficeService.deleteRentalOfficeById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
