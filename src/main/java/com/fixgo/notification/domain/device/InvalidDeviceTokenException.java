package com.fixgo.notification.domain.device;

public class InvalidDeviceTokenException extends RuntimeException {
    public InvalidDeviceTokenException(String message) {
        super(message);
    }
}
