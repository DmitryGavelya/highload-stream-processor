CREATE TABLE user_enrichment
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    column_name VARCHAR(255) NOT NULL,
    UNIQUE (user_id, column_name)
);

CREATE INDEX idx_user_enrichment_user_id ON user_enrichment (user_id);