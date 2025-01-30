CREATE TABLE labels (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    description VARCHAR(50),
    color VARCHAR(7) NOT NULL,
    project BIGINT NOT NULL,

    CONSTRAINT pk_labels PRIMARY KEY (id),
    CONSTRAINT fk_labels_project FOREIGN KEY (project) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE INDEX idx_labels_project ON labels(project);