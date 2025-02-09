CREATE TABLE IF NOT EXISTS fragments (
    id UUID PRIMARY KEY,
    file_id UUID NOT NULL,
    external_file_id VARCHAR NOT NULL,
    index INTEGER NOT NULL,
    hash VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL
);
