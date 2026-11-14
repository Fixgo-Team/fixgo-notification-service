package com.fixgo.notification.infrastructure.config;

import com.fixgo.notification.application.device.RegisterDeviceUseCase;
import com.fixgo.notification.application.device.RemoveDeviceUseCase;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort;
import com.fixgo.notification.domain.device.ports.in.RemoveDevicePort;
import com.fixgo.notification.domain.device.ports.out.DeviceTokenRepositoryPort;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires use cases (plain classes) to their ports. */
@Configuration
class BeanConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    RegisterDevicePort registerDevicePort(DeviceTokenRepositoryPort repository, Clock clock) {
        return new RegisterDeviceUseCase(repository, clock);
    }

    @Bean
    RemoveDevicePort removeDevicePort(DeviceTokenRepositoryPort repository) {
        return new RemoveDeviceUseCase(repository);
    }
}
