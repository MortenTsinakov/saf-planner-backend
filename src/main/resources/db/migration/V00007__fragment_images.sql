CREATE TABLE fragment_images (
    id VARCHAR(36),
    fragment BIGINT NOT NULL,
    file_extension VARCHAR(7) NOT NULL,

    CONSTRAINT pk_fragment_images PRIMARY KEY (id),
    CONSTRAINT fk_fragment_images_fragment FOREIGN KEY (fragment) REFERENCES fragments(id) ON DELETE CASCADE
);

CREATE INDEX idx_fragment_images_fragment ON fragment_images(fragment);