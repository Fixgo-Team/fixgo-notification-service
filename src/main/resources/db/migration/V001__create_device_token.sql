CREATE TABLE device_token (
    id           CHAR(36)     NOT NULL,
    user_id      CHAR(36)     NOT NULL,
    token        VARCHAR(512) NOT NULL,
    platform     VARCHAR(16)  NOT NULL,
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   DATETIME(6)  NOT NULL,
    last_seen_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_device_token_token UNIQUE (token),
    CONSTRAINT ck_device_token_platform CHECK (platform IN ('ANDROID')),
    INDEX idx_device_token_user (user_id)
);
