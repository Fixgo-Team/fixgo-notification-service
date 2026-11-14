package com.fixgo.notification.application.device;

import com.fixgo.notification.domain.device.DeviceAccessDeniedException;
import com.fixgo.notification.domain.device.DeviceNotFoundException;
import com.fixgo.notification.domain.device.DeviceToken;
import com.fixgo.notification.domain.device.ports.in.RemoveDevicePort;
import com.fixgo.notification.domain.device.ports.out.DeviceTokenRepositoryPort;
import java.util.UUID;

public class RemoveDeviceUseCase implements RemoveDevicePort {

    private final DeviceTokenRepositoryPort repository;

    public RemoveDeviceUseCase(DeviceTokenRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID userId, UUID deviceId) {
        DeviceToken device = repository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
        if (!device.belongsTo(userId)) {
            throw new DeviceAccessDeniedException();
        }
        repository.delete(device);
    }
}
