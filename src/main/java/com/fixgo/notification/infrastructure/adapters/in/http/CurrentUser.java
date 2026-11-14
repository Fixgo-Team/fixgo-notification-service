package com.fixgo.notification.infrastructure.adapters.in.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.UUID;

/**
 * Reads the user id (JWT "sub") from the Authorization header.
 * The signature is NOT verified here: the API Gateway validates the token before routing
 * (NFR-04), so this service must only be reachable through the gateway.
 */
final class CurrentUser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private CurrentUser() {}

    static UUID fromAuthorization(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authentication token required");
        }
        try {
            String[] parts = header.substring(7).split("\\.");
            JsonNode payload = MAPPER.readTree(Base64.getUrlDecoder().decode(parts[1]));
            return UUID.fromString(payload.get("sub").asText());
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid authentication token");
        }
    }
}
