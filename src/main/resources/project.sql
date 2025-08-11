-- pg_trgm extension for trigram indexing
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- projects Table
CREATE TABLE IF NOT EXISTS projects
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL UNIQUE,
    description  TEXT         NOT NULL,
    owner_id     BIGINT       NOT NULL,
    period_start TIMESTAMPTZ  NOT NULL,
    period_end   TIMESTAMPTZ  NOT NULL,
    state        VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    max_members  INTEGER      NOT NULL,
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_projects_name_trigram ON projects USING gin (lower(name) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_projects_description_trigram ON projects USING gin (lower(description) gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_projects_state_deleted_start ON projects (state, is_deleted, period_start);
CREATE INDEX IF NOT EXISTS idx_projects_state_deleted_end ON projects (state, is_deleted, period_end);
CREATE INDEX IF NOT EXISTS idx_projects_created_at ON projects (created_at);


-- project_members Table
CREATE TABLE IF NOT EXISTS project_members
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    is_deleted BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_project_members_project FOREIGN KEY (project_id) REFERENCES projects (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uidx_project_members_project_user ON project_members (project_id, user_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_project_members_user_id ON project_members (user_id, is_deleted);


-- project_managers Table
CREATE TABLE IF NOT EXISTS project_managers
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    role       VARCHAR(50) NOT NULL,
    is_deleted BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_project_managers_project FOREIGN KEY (project_id) REFERENCES projects (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uidx_project_managers_project_user ON project_managers (project_id, user_id) WHERE is_deleted = false;
CREATE INDEX IF NOT EXISTS idx_project_managers_user_id ON project_managers (user_id, is_deleted);
