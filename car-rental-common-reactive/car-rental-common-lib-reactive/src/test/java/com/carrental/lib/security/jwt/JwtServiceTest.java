package com.carrental.lib.security.jwt;

import com.carrental.document.model.User;
import com.carrental.lib.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Test
    void generateTokenTest_success() {
        ReflectionTestUtils.setField(jwtService, "signingKey", "413F4428472B4B6250655368566D5970337336763979244226452948404D6351");

        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        assertDoesNotThrow(() -> jwtService.generateToken(user));
    }

}
