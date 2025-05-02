package com.autohubreactive.agency.mapper;

import com.autohubreactive.agency.util.AssertionUtil;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.BranchRequest;
import com.autohubreactive.dto.BranchResponse;
import com.autohubreactive.model.Branch;
import com.autohubreactive.model.RentalOffice;
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
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchResponse branchResponse = Assertions.assertDoesNotThrow(() -> branchMapper.mapEntityToDto(branch));

        assertNotNull(branchResponse);
        AssertionUtil.assertBranchResponse(branch, branchResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(branchMapper.mapEntityToDto(null));
    }

    @Test
    void getNewBranchTest_success() {
        BranchRequest branchRequest =
                TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        Branch branch = Assertions.assertDoesNotThrow(() -> branchMapper.getNewBranch(branchRequest, rentalOffice));

        assertNotNull(branch);
        AssertionUtil.assertBranchRequest(branch, branchRequest);
    }

    @Test
    void getNewBranchTest_null() {
        assertNull(branchMapper.getNewBranch(null, null));
    }

}
