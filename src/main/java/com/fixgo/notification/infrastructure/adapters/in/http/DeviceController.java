package com.fixgo.notification.infrastructure.adapters.in.http;

import com.fixgo.notification.domain.device.DeviceToken;
import com.fixgo.notification.domain.device.Platform;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort.RegisterDeviceCommand;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort.RegisterDeviceResult;
import com.fixgo.notification.domain.device.ports.in.RemoveDevicePort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final RegisterDevicePort registerDevice;
    private final RemoveDevicePort removeDevice;

    public DeviceController(RegisterDevicePort registerDevice, RemoveDevicePort removeDevice) {
        this.registerDevice = registerDevice;
        this.removeDevice = removeDevice;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> register(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody RegisterDeviceRequest body) {
        UUID userId = CurrentUser.fromAuthorization(authorization);
        RegisterDeviceResult result = registerDevice.execute(
                new RegisterDeviceCommand(userId, body.token(), body.platform()));
        HttpStatus status = result.created() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(DeviceResponse.from(result.device()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable UUID id) {
        removeDevice.execute(CurrentUser.fromAuthorization(authorization), id);
        return ResponseEntity.noContent().build();
    }

    public record RegisterDeviceRequest(
            @NotBlank @Size(max = 512) String token,
            @NotNull Platform platform) {}

    public record DeviceResponse(UUID id, Platform platform, boolean active,
                                 Instant createdAt, Instant lastSeenAt) {
        static DeviceResponse from(DeviceToken d) {
            return new DeviceResponse(d.id(), d.platform(), d.active(), d.createdAt(), d.lastSeenAt());
        }
    }
}
