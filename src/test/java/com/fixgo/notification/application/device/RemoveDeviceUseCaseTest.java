package com.fixgo.notification.application.device;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fixgo.notification.domain.device.DeviceAccessDeniedException;
import com.fixgo.notification.domain.device.DeviceNotFoundException;
import com.fixgo.notification.domain.device.DeviceToken;
import com.fixgo.notification.domain.device.Platform;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RemoveDeviceUseCaseTest {

    private final InMemoryDeviceTokenRepository repo = new InMemoryDeviceTokenRepository();
    private final RemoveDeviceUseCase useCase = new RemoveDeviceUseCase(repo);
    private final UUID user = UUID.randomUUID();

    private DeviceToken saved() {
        return repo.save(DeviceToken.register(user, "tok", Platform.ANDROID, Instant.now()));
    }

    @Test
    void removesOwnDevice() {
        DeviceToken d = saved();

        useCase.execute(user, d.id());

        assertThat(repo.store).isEmpty();
    }

    @Test
    void failsWhenDeviceDoesNotExist() {
        assertThatThrownBy(() -> useCase.execute(user, UUID.randomUUID()))
                .isInstanceOf(DeviceNotFoundException.class);
    }

    @Test
    void failsWhenDeviceBelongsToAnotherUser() {
        DeviceToken d = saved();

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), d.id()))
                .isInstanceOf(DeviceAccessDeniedException.class);
        assertThat(repo.store).hasSize(1);
    }
}
