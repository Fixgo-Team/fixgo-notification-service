package com.fixgo.notification.infrastructure.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "device_token")
class DeviceTokenEntity {

    @Id
    @Column(length = 36)
    String id;

    @Column(name = "user_id", length = 36, nullable = false)
    String userId;

    @Column(length = 512, nullable = false, unique = true)
    String token;

    @Column(length = 16, nullable = false)
    String platform;

    @Column(nullable = false)
    boolean active;

    @Column(name = "created_at", nullable = false)
    Instant createdAt;

    @Column(name = "last_seen_at", nullable = false)
    Instant lastSeenAt;

    protected DeviceTokenEntity() {}
}
