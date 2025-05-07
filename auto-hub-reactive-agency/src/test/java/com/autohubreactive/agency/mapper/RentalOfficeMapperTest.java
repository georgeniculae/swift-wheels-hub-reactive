package com.autohubreactive.agency.mapper;

import com.autohubreactive.agency.util.AssertionUtil;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.agency.RentalOfficeRequest;
import com.autohubreactive.dto.agency.RentalOfficeResponse;
import com.autohubreactive.model.agency.RentalOffice;
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
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeResponse rentalOfficeDto = rentalOfficeMapper.mapEntityToDto(rentalOffice);

        assertNotNull(rentalOfficeDto);
        AssertionUtil.assertRentalOfficeResponse(rentalOffice, rentalOfficeDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(rentalOfficeMapper.mapEntityToDto(null));
    }

    @Test
    void getNewRentalOfficeTest_success() {
        RentalOfficeRequest rentalOfficeDto =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOffice rentalOffice = rentalOfficeMapper.getNewRentalOffice(rentalOfficeDto);

        assertNotNull(rentalOffice);
        AssertionUtil.assertRentalOfficeRequest(rentalOffice, rentalOfficeDto);
    }

    @Test
    void getNewRentalOfficeTest_null() {
        assertNull(rentalOfficeMapper.getNewRentalOffice(null));
    }

}
