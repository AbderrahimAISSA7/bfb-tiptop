-- Initial schema for TheTipTop
CREATE SCHEMA IF NOT EXISTS thetiptop;

-- 1) prizes
CREATE TABLE IF NOT EXISTS thetiptop.prizes (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    image           VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2) users
CREATE TABLE IF NOT EXISTS thetiptop.users (
    id                  BIGSERIAL PRIMARY KEY,
    first_name          VARCHAR(100),
    last_name           VARCHAR(100),
    provider            VARCHAR(50),
    provider_id         VARCHAR(100),
    avatar              VARCHAR(255),
    phone               VARCHAR(32),
    email               VARCHAR(255) UNIQUE,
    password            VARCHAR(255),
    email_verified_at   TIMESTAMPTZ,
    sex                 VARCHAR(10),
    age                 INTEGER,
    role                VARCHAR(20) NOT NULL DEFAULT 'USER',
    deleted_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3) concours
CREATE TABLE IF NOT EXISTS thetiptop.concours (
    id              BIGSERIAL PRIMARY KEY,
    start_date      TIMESTAMPTZ NOT NULL,
    end_date        TIMESTAMPTZ NOT NULL,
    winner_id       BIGINT REFERENCES thetiptop.users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4) codes
CREATE TABLE IF NOT EXISTS thetiptop.codes (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(64) NOT NULL UNIQUE,
    prize_id        BIGINT NOT NULL REFERENCES thetiptop.prizes(id) ON DELETE RESTRICT,
    status          VARCHAR(32) NOT NULL DEFAULT 'NEW',
    expiration_date TIMESTAMPTZ,
    issue_date      TIMESTAMPTZ,
    use_date        TIMESTAMPTZ,
    claim_date      TIMESTAMPTZ,
    validated_at    TIMESTAMPTZ,
    validated_by    BIGINT REFERENCES thetiptop.users(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5) participations
CREATE TABLE IF NOT EXISTS thetiptop.participations (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES thetiptop.users(id) ON DELETE CASCADE,
    code_id         BIGINT NOT NULL REFERENCES thetiptop.codes(id) ON DELETE RESTRICT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_participations_code UNIQUE (code_id)
);

-- 6) newsletters
CREATE TABLE IF NOT EXISTS thetiptop.newsletters (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Frequently filtered columns
CREATE INDEX IF NOT EXISTS idx_users_email ON thetiptop.users (email);
CREATE INDEX IF NOT EXISTS idx_codes_code ON thetiptop.codes (code);
CREATE INDEX IF NOT EXISTS idx_participations_user_id ON thetiptop.participations (user_id);
CREATE INDEX IF NOT EXISTS idx_participations_code_id ON thetiptop.participations (code_id);

