package com.swiftwheelshubreactive.agency.service;

import com.swiftwheelshubreactive.agency.mapper.RentalOfficeMapper;
import com.swiftwheelshubreactive.agency.mapper.RentalOfficeMapperImpl;
import com.swiftwheelshubreactive.agency.repository.BranchRepository;
import com.swiftwheelshubreactive.agency.repository.RentalOfficeRepository;
import com.swiftwheelshubreactive.agency.util.TestUtils;
import com.swiftwheelshubreactive.dto.RentalOfficeRequest;
import com.swiftwheelshubreactive.dto.RentalOfficeResponse;
import com.swiftwheelshubreactive.model.RentalOffice;
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
import static org.mockito.Mockito.times;
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
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        List<RentalOffice> rentalOffices = List.of(rentalOffice);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.findAll()).thenReturn(Flux.fromIterable(rentalOffices));

        StepVerifier.create(rentalOfficeService.findAllRentalOffices())
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    void findAllRentalOfficesTest_errorOnFindingAllRentalOffices() {
        when(rentalOfficeRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.findAllRentalOffices())
                .expectError()
                .verify();
    }

    @Test
    void findRentalOfficeByIdTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));

        StepVerifier.create(rentalOfficeService.findRentalOfficeById("64f361caf291ae086e179547"))
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByIdTest_errorOnFindingById() {
        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.findRentalOfficeById("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void countRentalOfficesTest_success() {
        when(rentalOfficeRepository.count()).thenReturn(Mono.just(3L));

        StepVerifier.create(rentalOfficeService.countRentalOffices())
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countRentalOfficesTest_errorOnCount() {
        when(rentalOfficeRepository.count()).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.countRentalOffices())
                .expectError()
                .verify();
    }

    @Test
    void saveRentalOfficeTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeRequest rentalOfficeRequest =
                TestUtils.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.just(rentalOffice));

        StepVerifier.create(rentalOfficeService.saveRentalOffice(rentalOfficeRequest))
                .expectNext(rentalOfficeResponse)
                .verifyComplete();

        verify(rentalOfficeRepository, times(1)).save(argumentCaptor.capture());
        verify(rentalOfficeMapper, times(1)).mapEntityToDto(any(RentalOffice.class));
    }

    @Test
    void saveRentalOfficeTest_errorOnSaving() {
        RentalOfficeRequest rentalOfficeRequest =
                TestUtils.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.saveRentalOffice(rentalOfficeRequest))
                .expectError()
                .verify();
    }

    @Test
    void updateRentalOfficeTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeRequest rentalOfficeRequest =
                TestUtils.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));
        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.just(rentalOffice));

        StepVerifier.create(rentalOfficeService.updateRentalOffice("64f361caf291ae086e179547", rentalOfficeRequest))
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    void updateRentalOfficeTest_errorOnSaving() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeRequest rentalOfficeRequest =
                TestUtils.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));
        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.updateRentalOffice("64f361caf291ae086e179547", rentalOfficeRequest))
                .expectError()
                .verify();
    }

    @Test
    void findRentalOfficeByNameTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(rentalOffice));

        StepVerifier.create(rentalOfficeService.findRentalOfficesByFilterInsensitiveCase("name"))
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByNameTest_errorOnFindingByName() {
        when(rentalOfficeRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.findRentalOfficesByFilterInsensitiveCase("name"))
                .expectError()
                .verify();
    }

    @Test
    void deleteRentalOfficeByIdTest_success() {
        when(rentalOfficeRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());
        when(branchRepository.deleteByRentalOfficeId(any(ObjectId.class))).thenReturn(Mono.empty());

        StepVerifier.create(rentalOfficeService.deleteRentalOfficeById("64f361caf291ae086e179547"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteRentalOfficeByIdTest_errorOnDeletingById() {
        when(rentalOfficeRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.deleteRentalOfficeById("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

}
