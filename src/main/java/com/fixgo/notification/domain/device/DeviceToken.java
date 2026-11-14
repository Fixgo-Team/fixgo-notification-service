package com.fixgo.notification.domain.device;

import java.time.Instant;
import java.util.UUID;

/** An FCM registration token of one physical device, owned by one user. */
public class DeviceToken {

    public static final int MAX_TOKEN_LENGTH = 512;

    private final UUID id;
    private UUID userId;
    private final String token;
    private final Platform platform;
    private boolean active;
    private final Instant createdAt;
    private Instant lastSeenAt;

    private DeviceToken(UUID id, UUID userId, String token, Platform platform,
                        boolean active, Instant createdAt, Instant lastSeenAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.platform = platform;
        this.active = active;
        this.createdAt = createdAt;
        this.lastSeenAt = lastSeenAt;
    }

    public static DeviceToken register(UUID userId, String token, Platform platform, Instant now) {
        if (token == null || token.isBlank()) {
            throw new InvalidDeviceTokenException("Token must not be blank");
        }
        if (token.length() > MAX_TOKEN_LENGTH) {
            throw new InvalidDeviceTokenException("Token must not exceed " + MAX_TOKEN_LENGTH + " characters");
        }
        return new DeviceToken(UUID.randomUUID(), userId, token, platform, true, now, now);
    }

    /** Rebuilds an existing device from storage (used by persistence adapters). */
    public static DeviceToken restore(UUID id, UUID userId, String token, Platform platform,
                                      boolean active, Instant createdAt, Instant lastSeenAt) {
        return new DeviceToken(id, userId, token, platform, active, createdAt, lastSeenAt);
    }

    /** The same token registered again: it now belongs to {@code userId} and is active. */
    public void refresh(UUID userId, Instant now) {
        this.userId = userId;
        this.active = true;
        this.lastSeenAt = now;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean belongsTo(UUID userId) {
        return this.userId.equals(userId);
    }

    public UUID id() { return id; }
    public UUID userId() { return userId; }
    public String token() { return token; }
    public Platform platform() { return platform; }
    public boolean active() { return active; }
    public Instant createdAt() { return createdAt; }
    public Instant lastSeenAt() { return lastSeenAt; }
}
