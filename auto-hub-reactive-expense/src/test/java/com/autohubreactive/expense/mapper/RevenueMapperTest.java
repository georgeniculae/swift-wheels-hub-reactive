package com.autohubreactive.expense.mapper;

import com.autohubreactive.dto.RevenueResponse;
import com.autohubreactive.expense.util.AssertionUtil;
import com.autohubreactive.expense.util.TestUtil;
import com.autohubreactive.model.Revenue;
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
        AssertionUtil.assertRevenueResponse(revenue, revenueResponse);
    }

}
