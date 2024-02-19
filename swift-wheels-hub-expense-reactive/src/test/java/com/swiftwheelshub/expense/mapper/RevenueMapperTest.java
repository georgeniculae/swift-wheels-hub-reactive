package com.swiftwheelshub.expense.mapper;

import com.swiftwheelshub.dto.RevenueRequest;
import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.model.Revenue;
import com.swiftwheelshub.expense.util.AssertionUtils;
import com.swiftwheelshub.expense.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RevenueMapperTest {

    private final RevenueMapper rentalOfficeMapper = new RevenueMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Revenue revenue = TestUtils.getResourceAsJson("/data/Revenue.json", Revenue.class);

        RevenueResponse revenueResponse = rentalOfficeMapper.mapEntityToDto(revenue);

        assertNotNull(revenueResponse);
        AssertionUtils.assertRevenueResponse(revenue, revenueResponse);
    }

}
