package com.fixgo.notification.domain.device;

import java.util.UUID;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(UUID id) {
        super("Device " + id + " not found");
    }
}
