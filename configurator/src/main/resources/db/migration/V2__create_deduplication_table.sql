CREATE TABLE user_deduplication
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    excluded_fields VARCHAR(255) NOT NULL,
    time_window_seconds INTEGER NOT NULL,
    UNIQUE (user_id)
);

CREATE INDEX idx_deduplication_columns_user_id ON user_deduplication (user_id);
