CREATE TABLE fragments (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    short_description TEXT,
    long_description TEXT,
    duration_in_seconds INTEGER NOT NULL,
    on_timeline BOOLEAN NOT NULL,
    position INTEGER NOT NULL,
    project BIGINT NOT NULL,

    CONSTRAINT pk_fragments PRIMARY KEY (id),
    CONSTRAINT fk_fragments_project FOREIGN KEY (project) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE INDEX idx_fragments_project ON fragments(project);
