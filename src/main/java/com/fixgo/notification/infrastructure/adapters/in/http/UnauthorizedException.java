package com.fixgo.notification.infrastructure.adapters.in.http;

class UnauthorizedException extends RuntimeException {
    UnauthorizedException(String message) {
        super(message);
    }
}
