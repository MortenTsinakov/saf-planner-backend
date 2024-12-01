CREATE TABLE refresh_tokens (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    token VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    user_id BIGINT,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT fk_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE UNIQUE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);