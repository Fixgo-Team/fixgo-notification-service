package com.fixgo.notification.application.device;

import com.fixgo.notification.domain.device.DeviceToken;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort;
import com.fixgo.notification.domain.device.ports.out.DeviceTokenRepositoryPort;
import java.time.Clock;
import java.util.Optional;

public class RegisterDeviceUseCase implements RegisterDevicePort {

    private final DeviceTokenRepositoryPort repository;
    private final Clock clock;

    public RegisterDeviceUseCase(DeviceTokenRepositoryPort repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public RegisterDeviceResult execute(RegisterDeviceCommand command) {
        Optional<DeviceToken> existing = repository.findByToken(command.token());
        if (existing.isPresent()) {
            DeviceToken device = existing.get();
            device.refresh(command.userId(), clock.instant());
            return new RegisterDeviceResult(repository.save(device), false);
        }
        DeviceToken device = DeviceToken.register(
                command.userId(), command.token(), command.platform(), clock.instant());
        return new RegisterDeviceResult(repository.save(device), true);
    }
}
