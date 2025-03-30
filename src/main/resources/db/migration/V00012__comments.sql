CREATE TABLE comments (
    id BIGINT NOT NULL,
    content TEXT,
    author BIGINT NOT NULL,
    fragment BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_author FOREIGN KEY (author) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_fragment FOREIGN KEY (fragment) REFERENCES fragments(id) ON DELETE CASCADE
);

CREATE INDEX idx_comments_fragment ON comments(fragment);