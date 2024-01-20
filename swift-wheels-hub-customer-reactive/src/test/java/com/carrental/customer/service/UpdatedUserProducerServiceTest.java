package com.carrental.customer.service;

import com.carrental.customer.util.TestUtils;
import com.carrental.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MimeType;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatedUserProducerServiceTest {

    @InjectMocks
    private UpdatedUserProducerService updatedUserProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Test
    void sendSavedUserTest_success() {
        ReflectionTestUtils.setField(updatedUserProducerService, "updatedUserBinderName", "updated-user");
        ReflectionTestUtils.setField(updatedUserProducerService, "updatedUserMimeType", "application/json");

        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(streamBridge.send(anyString(), any(Object.class), any(MimeType.class))).thenReturn(true);

        StepVerifier.create(updatedUserProducerService.sendUpdatedUser(userDto))
                .expectNext(true)
                .verifyComplete();
    }

}
