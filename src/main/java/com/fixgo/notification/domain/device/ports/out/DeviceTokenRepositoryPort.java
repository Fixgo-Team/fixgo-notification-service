package com.fixgo.notification.domain.device.ports.out;

import com.fixgo.notification.domain.device.DeviceToken;
import java.util.Optional;
import java.util.UUID;

public interface DeviceTokenRepositoryPort {
    DeviceToken save(DeviceToken device);
    Optional<DeviceToken> findByToken(String token);
    Optional<DeviceToken> findById(UUID id);
    void delete(DeviceToken device);
}
