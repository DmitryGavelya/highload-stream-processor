CREATE TABLE user_deduplication
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    column_name VARCHAR(255) NOT NULL,
    UNIQUE (user_id, column_name)
);

CREATE INDEX idx_deduplication_columns_user_id ON user_deduplication (user_id);