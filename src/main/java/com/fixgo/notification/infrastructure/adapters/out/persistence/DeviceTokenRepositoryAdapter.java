package com.fixgo.notification.infrastructure.adapters.out.persistence;

import com.fixgo.notification.domain.device.DeviceToken;
import com.fixgo.notification.domain.device.Platform;
import com.fixgo.notification.domain.device.ports.out.DeviceTokenRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class DeviceTokenRepositoryAdapter implements DeviceTokenRepositoryPort {

    private final SpringDataDeviceTokenRepository jpa;

    DeviceTokenRepositoryAdapter(SpringDataDeviceTokenRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public DeviceToken save(DeviceToken device) {
        return toDomain(jpa.save(toEntity(device)));
    }

    @Override
    public Optional<DeviceToken> findByToken(String token) {
        return jpa.findByToken(token).map(DeviceTokenRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<DeviceToken> findById(UUID id) {
        return jpa.findById(id.toString()).map(DeviceTokenRepositoryAdapter::toDomain);
    }

    @Override
    public void delete(DeviceToken device) {
        jpa.deleteById(device.id().toString());
    }

    private static DeviceTokenEntity toEntity(DeviceToken d) {
        DeviceTokenEntity e = new DeviceTokenEntity();
        e.id = d.id().toString();
        e.userId = d.userId().toString();
        e.token = d.token();
        e.platform = d.platform().name();
        e.active = d.active();
        e.createdAt = d.createdAt();
        e.lastSeenAt = d.lastSeenAt();
        return e;
    }

    private static DeviceToken toDomain(DeviceTokenEntity e) {
        return DeviceToken.restore(UUID.fromString(e.id), UUID.fromString(e.userId), e.token,
                Platform.valueOf(e.platform), e.active, e.createdAt, e.lastSeenAt);
    }
}
