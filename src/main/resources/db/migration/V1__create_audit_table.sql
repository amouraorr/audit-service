-- V1: create audits table
CREATE TABLE IF NOT EXISTS audits (
    id uuid PRIMARY KEY,
    event_type varchar(100) NOT NULL,
    source varchar(255),
    payload jsonb,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_audits_created_at ON audits(created_at);
CREATE INDEX IF NOT EXISTS idx_audits_event_type ON audits(event_type);