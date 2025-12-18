CREATE TABLE deduplication_config (
    user_id VARCHAR(255) PRIMARY KEY,
    time_window_seconds INTEGER NOT NULL DEFAULT 300,
    excluded_fields TEXT
);

CREATE INDEX idx_deduplication_config_user_id ON deduplication_config (user_id);

