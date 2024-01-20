package com.carrental.agency.service;

import com.carrental.agency.mapper.RentalOfficeMapper;
import com.carrental.agency.mapper.RentalOfficeMapperImpl;
import com.carrental.agency.repository.RentalOfficeRepository;
import com.carrental.agency.util.TestUtils;
import com.carrental.document.model.RentalOffice;
import com.carrental.dto.RentalOfficeDto;
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

    @Spy
    private RentalOfficeMapper rentalOfficeMapper = new RentalOfficeMapperImpl();

    @Captor
    private ArgumentCaptor<RentalOffice> argumentCaptor = ArgumentCaptor.forClass(RentalOffice.class);

    @Test
    void findAllRentalOfficesTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        List<RentalOffice> rentalOffices = List.of(rentalOffice);
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.findAll()).thenReturn(Flux.fromIterable(rentalOffices));

        StepVerifier.create(rentalOfficeService.findAllRentalOffices())
                .expectNext(rentalOfficeDto)
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
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));

        StepVerifier.create(rentalOfficeService.findRentalOfficeById("64f361caf291ae086e179547"))
                .expectNext(rentalOfficeDto)
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
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.just(rentalOffice));

        StepVerifier.create(rentalOfficeService.saveRentalOffice(rentalOfficeDto))
                .expectNext(rentalOfficeDto)
                .verifyComplete();

        verify(rentalOfficeRepository, times(1)).save(argumentCaptor.capture());
        verify(rentalOfficeMapper, times(1)).mapEntityToDto(any(RentalOffice.class));
    }

    @Test
    void saveRentalOfficeTest_errorOnSaving() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.saveRentalOffice(rentalOfficeDto))
                .expectError()
                .verify();
    }

    @Test
    void updateRentalOfficeTest_success() {
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        RentalOfficeDto rentalOfficeDto = TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));
        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.just(rentalOffice));

        StepVerifier.create(rentalOfficeService.updateRentalOffice("64f361caf291ae086e179547", rentalOfficeDto))
                .expectNext(rentalOfficeDto)
                .verifyComplete();
    }

    @Test
    void updateRentalOfficeTest_errorOnSaving() {
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        RentalOfficeDto rentalOfficeDto = TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(rentalOffice));
        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.updateRentalOffice("64f361caf291ae086e179547", rentalOfficeDto))
                .expectError()
                .verify();
    }

    @Test
    void findRentalOfficeByNameTest_success() {
        RentalOffice rentalOffice =
                TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        when(rentalOfficeRepository.findAllByNameInsensitiveCase(anyString())).thenReturn(Flux.just(rentalOffice));

        StepVerifier.create(rentalOfficeService.findRentalOfficesByNameInsensitiveCase("name"))
                .expectNext(rentalOfficeDto)
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByNameTest_errorOnFindingByName() {
        when(rentalOfficeRepository.findAllByNameInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(rentalOfficeService.findRentalOfficesByNameInsensitiveCase("name"))
                .expectError()
                .verify();
    }

    @Test
    void deleteRentalOfficeByIdTest_success() {
        when(rentalOfficeRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());

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
