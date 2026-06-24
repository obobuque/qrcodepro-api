CREATE TABLE api_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    key_hash VARCHAR(64) NOT NULL,
    name VARCHAR(100),
    last_used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    active BOOLEAN DEFAULT TRUE,
    UNIQUE(key_hash)
);

CREATE INDEX idx_api_keys_user_id ON api_keys(user_id);
CREATE INDEX idx_api_keys_key_hash ON api_keys(key_hash);
