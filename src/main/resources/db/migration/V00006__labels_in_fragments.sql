CREATE TABLE labels_in_fragments (
    label BIGINT NOT NULL,
    fragment BIGINT NOT NULL,

    CONSTRAINT pk_labels_in_fragments PRIMARY KEY (label, fragment),
    CONSTRAINT fk_labels_in_fragments_label FOREIGN KEY (label) REFERENCES labels(id) ON DELETE CASCADE,
    CONSTRAINT fk_labels_in_fragments_fragment FOREIGN KEY (fragment) REFERENCES fragments(id) ON DELETE CASCADE
);

CREATE INDEX idx_labels_in_fragments_fragment ON labels_in_fragments(fragment);