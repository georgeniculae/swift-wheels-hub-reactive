package com.carrental.agency.mapper;

import com.carrental.agency.util.AssertionUtils;
import com.carrental.agency.util.TestUtils;
import com.carrental.document.model.RentalOffice;
import com.carrental.dto.RentalOfficeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class RentalOfficeMapperTest {

    private final RentalOfficeMapper rentalOfficeMapper = new RentalOfficeMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeDto rentalOfficeDto = assertDoesNotThrow(() -> rentalOfficeMapper.mapEntityToDto(rentalOffice));

        assertNotNull(rentalOfficeDto);
        AssertionUtils.assertRentalOffice(rentalOffice, rentalOfficeDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        RentalOfficeDto rentalOfficeDto = assertDoesNotThrow(() -> rentalOfficeMapper.mapEntityToDto(null));

        assertNull(rentalOfficeDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        RentalOfficeDto rentalOfficeDto = TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        RentalOffice rentalOffice = assertDoesNotThrow(() -> rentalOfficeMapper.mapDtoToEntity(rentalOfficeDto));

        assertNotNull(rentalOffice);
        AssertionUtils.assertRentalOffice(rentalOffice, rentalOfficeDto);
    }

    @Test
    void mapDtoToEntityTest_null() {
        RentalOffice rentalOffice = assertDoesNotThrow(() -> rentalOfficeMapper.mapDtoToEntity(null));

        assertNull(rentalOffice);
    }

}
