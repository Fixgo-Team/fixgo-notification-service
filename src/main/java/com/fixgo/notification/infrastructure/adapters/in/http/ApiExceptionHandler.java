package com.fixgo.notification.infrastructure.adapters.in.http;

import com.fixgo.notification.domain.device.DeviceAccessDeniedException;
import com.fixgo.notification.domain.device.DeviceNotFoundException;
import com.fixgo.notification.domain.device.InvalidDeviceTokenException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps exceptions to the standard ErrorResponse of 07-api/contracts/openapi/_shared.yaml. */
@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<Map<String, Object>> unauthorized(UnauthorizedException e) {
        return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", e.getMessage());
    }

    @ExceptionHandler(DeviceAccessDeniedException.class)
    ResponseEntity<Map<String, Object>> forbidden(DeviceAccessDeniedException e) {
        return error(HttpStatus.FORBIDDEN, "FORBIDDEN", e.getMessage());
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    ResponseEntity<Map<String, Object>> notFound(DeviceNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler({InvalidDeviceTokenException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Map<String, Object>> badRequest(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid request data");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        List<Map<String, String>> details = e.getBindingResult().getFieldErrors().stream()
                .map(f -> Map.of("field", f.getField(), "message", String.valueOf(f.getDefaultMessage())))
                .toList();
        return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR", "message", "Invalid request data", "details", details));
    }

    private static ResponseEntity<Map<String, Object>> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of("error", code, "message", message));
    }
}
