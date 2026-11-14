package com.fixgo.notification.infrastructure.adapters.in.http;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fixgo.notification.domain.device.DeviceAccessDeniedException;
import com.fixgo.notification.domain.device.DeviceToken;
import com.fixgo.notification.domain.device.Platform;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort.RegisterDeviceResult;
import com.fixgo.notification.domain.device.ports.in.RemoveDevicePort;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DeviceControllerTest {

    private final RegisterDevicePort register = mock(RegisterDevicePort.class);
    private final RemoveDevicePort remove = mock(RemoveDevicePort.class);
    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new DeviceController(register, remove))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    private final UUID user = UUID.randomUUID();

    private String bearer(UUID sub) {
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        String header = enc.encodeToString("{\"alg\":\"none\"}".getBytes());
        String payload = enc.encodeToString(("{\"sub\":\"" + sub + "\"}").getBytes());
        return "Bearer " + header + "." + payload + ".sig";
    }

    private DeviceToken device() {
        return DeviceToken.register(user, "tok", Platform.ANDROID, Instant.now());
    }

    @Test
    void postReturns201WhenTokenIsNew() throws Exception {
        when(register.execute(any())).thenReturn(new RegisterDeviceResult(device(), true));

        mvc.perform(post("/devices").header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"tok\",\"platform\":\"ANDROID\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void postReturns200WhenTokenAlreadyExisted() throws Exception {
        when(register.execute(any())).thenReturn(new RegisterDeviceResult(device(), false));

        mvc.perform(post("/devices").header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"tok\",\"platform\":\"ANDROID\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void postReturns400WhenTokenIsBlank() throws Exception {
        mvc.perform(post("/devices").header("Authorization", bearer(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"\",\"platform\":\"ANDROID\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void postReturns401WithoutToken() throws Exception {
        mvc.perform(post("/devices").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"tok\",\"platform\":\"ANDROID\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void deleteReturns204() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(delete("/devices/" + id).header("Authorization", bearer(user)))
                .andExpect(status().isNoContent());
        verify(remove).execute(user, id);
    }

    @Test
    void deleteReturns403WhenDeviceBelongsToAnotherUser() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new DeviceAccessDeniedException()).when(remove).execute(user, id);

        mvc.perform(delete("/devices/" + id).header("Authorization", bearer(user)))
                .andExpect(status().isForbidden());
    }
}
