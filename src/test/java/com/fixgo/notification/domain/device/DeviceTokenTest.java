package com.fixgo.notification.domain.device;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeviceTokenTest {

    private static final Instant T0 = Instant.parse("2026-11-13T10:00:00Z");
    private static final Instant T1 = Instant.parse("2026-11-13T11:00:00Z");
    private final UUID user = UUID.randomUUID();

    @Test
    void registerCreatesActiveDevice() {
        DeviceToken d = DeviceToken.register(user, "abc", Platform.ANDROID, T0);

        assertThat(d.id()).isNotNull();
        assertThat(d.userId()).isEqualTo(user);
        assertThat(d.active()).isTrue();
        assertThat(d.createdAt()).isEqualTo(T0);
        assertThat(d.lastSeenAt()).isEqualTo(T0);
    }

    @Test
    void registerRejectsBlankToken() {
        assertThatThrownBy(() -> DeviceToken.register(user, "  ", Platform.ANDROID, T0))
                .isInstanceOf(InvalidDeviceTokenException.class);
    }

    @Test
    void registerRejectsTokenLongerThan512() {
        assertThatThrownBy(() -> DeviceToken.register(user, "a".repeat(513), Platform.ANDROID, T0))
                .isInstanceOf(InvalidDeviceTokenException.class);
    }

    @Test
    void refreshReassignsOwnerReactivatesAndUpdatesLastSeen() {
        DeviceToken d = DeviceToken.register(user, "abc", Platform.ANDROID, T0);
        d.deactivate();
        UUID other = UUID.randomUUID();

        d.refresh(other, T1);

        assertThat(d.userId()).isEqualTo(other);
        assertThat(d.active()).isTrue();
        assertThat(d.lastSeenAt()).isEqualTo(T1);
        assertThat(d.createdAt()).isEqualTo(T0);
    }

    @Test
    void belongsToOnlyItsOwner() {
        DeviceToken d = DeviceToken.register(user, "abc", Platform.ANDROID, T0);

        assertThat(d.belongsTo(user)).isTrue();
        assertThat(d.belongsTo(UUID.randomUUID())).isFalse();
    }
}
