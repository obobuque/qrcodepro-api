-- Tabela de planos (dados imutáveis, populada via migration)
CREATE TABLE plans (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price_cents INT NOT NULL DEFAULT 0,
    max_qr_codes INT NOT NULL,
    max_scans_per_month INT NOT NULL,
    allows_dynamic BOOLEAN NOT NULL DEFAULT false,
    allows_custom_logo BOOLEAN NOT NULL DEFAULT false,
    allows_custom_colors BOOLEAN NOT NULL DEFAULT false,
    allows_webhooks BOOLEAN NOT NULL DEFAULT false,
    api_rate_limit_per_minute INT NOT NULL DEFAULT 60,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Tabela de assinaturas dos usuarios
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_id VARCHAR(20) NOT NULL REFERENCES plans(id),
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    stripe_subscription_id VARCHAR(100),
    stripe_customer_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, active) WHERE active = true
);

-- Indices
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_active ON subscriptions(active);
CREATE INDEX idx_subscriptions_plan_id ON subscriptions(plan_id);

-- Popular planos padrao
INSERT INTO plans (id, name, price_cents, max_qr_codes, max_scans_per_month, allows_dynamic, allows_custom_logo, allows_custom_colors, allows_webhooks, api_rate_limit_per_minute) VALUES
    ('free', 'Free', 0, 10, 100, false, false, true, false, 30),
    ('starter', 'Starter', 900, 100, 10000, true, false, true, false, 120),
    ('pro', 'Pro', 2900, 1000, 100000, true, true, true, false, 300),
    ('business', 'Business', 9900, 999999, 1000000, true, true, true, true, 1000);
