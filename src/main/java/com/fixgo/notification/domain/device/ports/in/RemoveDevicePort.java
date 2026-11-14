package com.fixgo.notification.domain.device.ports.in;

import java.util.UUID;

public interface RemoveDevicePort {
    void execute(UUID userId, UUID deviceId);
}
