ALTER TABLE users ADD COLUMN api_key VARCHAR(64) UNIQUE;
CREATE INDEX idx_users_api_key ON users(api_key) WHERE api_key IS NOT NULL;
