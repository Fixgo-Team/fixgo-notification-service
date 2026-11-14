package com.fixgo.notification.application.device;

import com.fixgo.notification.domain.device.DeviceToken;
import com.fixgo.notification.domain.device.ports.out.DeviceTokenRepositoryPort;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Fake repository for use-case tests (no database, no mocks). */
class InMemoryDeviceTokenRepository implements DeviceTokenRepositoryPort {

    final Map<UUID, DeviceToken> store = new HashMap<>();

    @Override
    public DeviceToken save(DeviceToken device) {
        store.put(device.id(), device);
        return device;
    }

    @Override
    public Optional<DeviceToken> findByToken(String token) {
        return store.values().stream().filter(d -> d.token().equals(token)).findFirst();
    }

    @Override
    public Optional<DeviceToken> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void delete(DeviceToken device) {
        store.remove(device.id());
    }
}
