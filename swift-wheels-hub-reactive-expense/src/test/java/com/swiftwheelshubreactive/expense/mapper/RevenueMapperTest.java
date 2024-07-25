package com.swiftwheelshubreactive.expense.mapper;

import com.swiftwheelshubreactive.dto.RevenueResponse;
import com.swiftwheelshubreactive.expense.util.AssertionUtils;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import com.swiftwheelshubreactive.model.Revenue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RevenueMapperTest {

    private final RevenueMapper rentalOfficeMapper = new RevenueMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Revenue revenue = TestUtil.getResourceAsJson("/data/Revenue.json", Revenue.class);

        RevenueResponse revenueResponse = rentalOfficeMapper.mapEntityToDto(revenue);

        assertNotNull(revenueResponse);
        AssertionUtils.assertRevenueResponse(revenue, revenueResponse);
    }

}
