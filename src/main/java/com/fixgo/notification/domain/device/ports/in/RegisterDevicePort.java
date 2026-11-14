package com.fixgo.notification.domain.device.ports.in;

import com.fixgo.notification.domain.device.DeviceToken;
import com.fixgo.notification.domain.device.Platform;
import java.util.UUID;

public interface RegisterDevicePort {

    RegisterDeviceResult execute(RegisterDeviceCommand command);

    record RegisterDeviceCommand(UUID userId, String token, Platform platform) {}

    /** {@code created} is false when the token already existed and was only refreshed. */
    record RegisterDeviceResult(DeviceToken device, boolean created) {}
}
