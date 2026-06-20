CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE qr_codes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id UUID NOT NULL,
    type VARCHAR(10) NOT NULL,           -- STATIC ou DYNAMIC
    short_code VARCHAR(7) UNIQUE,
    content TEXT,
    destination_url TEXT,
    foreground_color VARCHAR(7) DEFAULT '#000000',
    background_color VARCHAR(7) DEFAULT '#FFFFFF',
    dot_style VARCHAR(20) DEFAULT 'SQUARE',
    error_correction_level VARCHAR(1) DEFAULT 'M',
    logo_image_url TEXT,
    logo_size_percent INT DEFAULT 0,
    image_url TEXT,
    image_format VARCHAR(10) DEFAULT 'PNG',
    size_pixels INT DEFAULT 300,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    expires_at TIMESTAMP WITH TIME ZONE,
    scan_count BIGINT DEFAULT 0,
    last_scan_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_qr_codes_owner ON qr_codes(owner_id);
CREATE INDEX idx_qr_codes_short_code ON qr_codes(short_code) WHERE type = 'DYNAMIC';
CREATE INDEX idx_qr_codes_active ON qr_codes(active) WHERE active = true;
CREATE INDEX idx_qr_codes_expires ON qr_codes(expires_at) WHERE expires_at IS NOT NULL;

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_qr_codes_updated_at
    BEFORE UPDATE ON qr_codes
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
