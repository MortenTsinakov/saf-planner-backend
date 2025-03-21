CREATE TABLE shared_projects (
    project BIGINT NOT NULL,
    shared_with BIGINT NOT NULL,
    shared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_shared_projects PRIMARY KEY (project, shared_with),
    CONSTRAINT fk_shared_projects_project FOREIGN KEY (project) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_shared_projects_shared_with FOREIGN KEY (shared_with) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_shared_projects_shared_with ON shared_projects(shared_with);