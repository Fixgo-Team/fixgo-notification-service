package com.fixgo.notification.application.device;

import static org.assertj.core.api.Assertions.assertThat;

import com.fixgo.notification.domain.device.Platform;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort.RegisterDeviceCommand;
import com.fixgo.notification.domain.device.ports.in.RegisterDevicePort.RegisterDeviceResult;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RegisterDeviceUseCaseTest {

    private final InMemoryDeviceTokenRepository repo = new InMemoryDeviceTokenRepository();
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-13T10:00:00Z"), ZoneOffset.UTC);
    private final RegisterDeviceUseCase useCase = new RegisterDeviceUseCase(repo, clock);
    private final UUID user = UUID.randomUUID();

    @Test
    void createsDeviceWhenTokenIsNew() {
        RegisterDeviceResult result = useCase.execute(new RegisterDeviceCommand(user, "tok", Platform.ANDROID));

        assertThat(result.created()).isTrue();
        assertThat(repo.store).hasSize(1);
        assertThat(result.device().userId()).isEqualTo(user);
    }

    @Test
    void isIdempotentWhenTokenAlreadyExists() {
        useCase.execute(new RegisterDeviceCommand(user, "tok", Platform.ANDROID));

        RegisterDeviceResult second = useCase.execute(new RegisterDeviceCommand(user, "tok", Platform.ANDROID));

        assertThat(second.created()).isFalse();
        assertThat(repo.store).hasSize(1);
    }

    @Test
    void reassignsTokenToNewUserAndReactivatesIt() {
        RegisterDeviceResult first = useCase.execute(new RegisterDeviceCommand(user, "tok", Platform.ANDROID));
        first.device().deactivate();
        UUID other = UUID.randomUUID();

        RegisterDeviceResult second = useCase.execute(new RegisterDeviceCommand(other, "tok", Platform.ANDROID));

        assertThat(second.created()).isFalse();
        assertThat(second.device().userId()).isEqualTo(other);
        assertThat(second.device().active()).isTrue();
    }
}
