-- projects 테이블
CREATE TABLE IF NOT EXISTS projects
(
    id                   BIGSERIAL PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL UNIQUE,
    description          TEXT         NOT NULL,
    owner_id             BIGINT       NOT NULL,
    period_start         TIMESTAMPTZ  NOT NULL,
    period_end           TIMESTAMPTZ  NOT NULL,
    state                VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    max_members          INTEGER      NOT NULL,
    current_member_count INTEGER      NOT NULL DEFAULT 0,
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_projects_name_trigram
    ON projects USING gin (lower(name) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_projects_description_trigram
    ON projects USING gin (lower(description) gin_trgm_ops);

-- CREATE INDEX IF NOT EXISTS idx_projects_name_prefix
--     ON projects ( lower(name) text_pattern_ops );

-- CREATE INDEX IF NOT EXISTS idx_projects_description_prefix
--     ON projects ( lower(description) text_pattern_ops );

-- project_managers 테이블
CREATE TABLE IF NOT EXISTS project_members
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    is_deleted BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- project_members 테이블
CREATE TABLE IF NOT EXISTS project_managers
(
    id         BIGSERIAL PRIMARY KEY,
    project_id BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    role       VARCHAR(50) NOT NULL,
    is_deleted BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);