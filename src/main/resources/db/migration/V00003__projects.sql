CREATE TABLE projects (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    estimated_length_in_seconds INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner BIGINT NOT NULL,

    CONSTRAINT pk_projects PRIMARY KEY (id),
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_projects_owner ON projects(owner);