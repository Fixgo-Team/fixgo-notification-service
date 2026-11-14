package com.fixgo.notification.infrastructure.adapters.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataDeviceTokenRepository extends JpaRepository<DeviceTokenEntity, String> {
    Optional<DeviceTokenEntity> findByToken(String token);
}
