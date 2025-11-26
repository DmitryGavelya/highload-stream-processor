CREATE TABLE user_filters (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id VARCHAR(255) NOT NULL UNIQUE,
                              field VARCHAR(255) NOT NULL,
                              operator VARCHAR(50) NOT NULL,
                              value VARCHAR(255)
);

CREATE INDEX idx_user_filters_user_id ON user_filters(user_id);