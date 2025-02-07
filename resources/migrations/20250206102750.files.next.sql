CREATE TABLE IF NOT EXISTS files (
    id UUID PRIMARY KEY,
    name VARCHAR NOT NULL,
    total_size INTEGER,
    access_count INTEGER NOT NULL,
    hash VARCHAR,
    callback_url VARCHAR,
    created_at TIMESTAMP NOT NULL
);
