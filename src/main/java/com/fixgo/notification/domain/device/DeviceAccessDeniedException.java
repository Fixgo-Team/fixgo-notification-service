package com.fixgo.notification.domain.device;

public class DeviceAccessDeniedException extends RuntimeException {
    public DeviceAccessDeniedException() {
        super("The device belongs to another user");
    }
}
