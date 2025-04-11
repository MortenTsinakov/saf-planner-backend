CREATE TABLE screenplays (
    id  BIGINT GENERATED ALWAYS AS IDENTITY,
    project BIGINT NOT NULL,
    content JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_screenplays PRIMARY KEY (id),
    CONSTRAINT fk_screenplays_project FOREIGN KEY (project) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE INDEX idx_screenplays_project ON screenplays(project);