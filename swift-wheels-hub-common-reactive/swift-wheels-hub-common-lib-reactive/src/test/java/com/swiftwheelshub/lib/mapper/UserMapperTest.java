package com.swiftwheelshub.lib.mapper;

import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.lib.util.AssertionUtils;
import com.swiftwheelshub.lib.util.TestUtils;
import com.swiftwheelshub.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        UserDto userDto = assertDoesNotThrow(() -> userMapper.mapEntityToDto(user));

        assertNotNull(userDto);
        AssertionUtils.assertUser(user, userDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        UserDto userDto = assertDoesNotThrow(() -> userMapper.mapEntityToDto(null));

        assertNull(userDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        User user = assertDoesNotThrow(() -> userMapper.mapDtoToEntity(userDto));

        assertNotNull(user);
        AssertionUtils.assertUser(user, userDto);
    }

    @Test
    void mapDtoToEntityTest_null() {
        User user = assertDoesNotThrow(() -> userMapper.mapDtoToEntity(null));

        assertNull(user);
    }

}
