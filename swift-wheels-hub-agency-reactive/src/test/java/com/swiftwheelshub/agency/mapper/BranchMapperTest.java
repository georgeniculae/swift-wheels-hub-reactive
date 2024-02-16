package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.BranchResponse;
import com.swiftwheelshub.model.Branch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BranchMapperTest {

    private final BranchMapper branchMapper = new BranchMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse = Assertions.assertDoesNotThrow(() -> branchMapper.mapEntityToDto(branch));

        assertNotNull(branchResponse);
        AssertionUtils.assertBranchResponse(branch, branchResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(branchMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntityTest_success() {
        BranchRequest branchRequest =
                TestUtils.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        Branch branch = Assertions.assertDoesNotThrow(() -> branchMapper.mapDtoToEntity(branchRequest));

        assertNotNull(branch);
        AssertionUtils.assertBranchRequest(branch, branchRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(branchMapper.mapDtoToEntity(null));
    }

}
