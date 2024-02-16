package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.RentalOfficeRequest;
import com.swiftwheelshub.dto.RentalOfficeResponse;
import com.swiftwheelshub.model.RentalOffice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class RentalOfficeMapperTest {

    private final RentalOfficeMapper rentalOfficeMapper = new RentalOfficeMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        RentalOffice rentalOffice = TestUtils.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeResponse rentalOfficeDto = rentalOfficeMapper.mapEntityToDto(rentalOffice);

        assertNotNull(rentalOfficeDto);
        AssertionUtils.assertRentalOfficeResponse(rentalOffice, rentalOfficeDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(rentalOfficeMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntityTest_success() {
        RentalOfficeRequest rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeRequest.class);

        RentalOffice rentalOffice = rentalOfficeMapper.mapDtoToEntity(rentalOfficeDto);

        assertNotNull(rentalOffice);
        AssertionUtils.assertRentalOfficeRequest(rentalOffice, rentalOfficeDto);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(rentalOfficeMapper.mapDtoToEntity(null));
    }

}
