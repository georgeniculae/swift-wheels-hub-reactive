package com.carrental.agency.mapper;

import com.carrental.agency.util.AssertionUtils;
import com.carrental.agency.util.TestUtils;
import com.carrental.document.model.Branch;
import com.carrental.dto.BranchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BranchMapperTest {

    private final BranchMapper branchMapper = new BranchMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        BranchDto branchDto = assertDoesNotThrow(() -> branchMapper.mapEntityToDto(branch));

        assertNotNull(branchDto);
        AssertionUtils.assertBranch(branch, branchDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        BranchDto branchDto = assertDoesNotThrow(() -> branchMapper.mapEntityToDto(null));

        assertNull(branchDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Branch branch = assertDoesNotThrow(() -> branchMapper.mapDtoToEntity(branchDto));

        assertNotNull(branch);
        AssertionUtils.assertBranch(branch, branchDto);
    }

    @Test
    void mapDtoToEntityTest_null() {
        Branch branch = assertDoesNotThrow(() -> branchMapper.mapDtoToEntity(null));

        assertNull(branch);
    }

}
