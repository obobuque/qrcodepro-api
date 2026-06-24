# Changelog

Formato: [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) + [SemVer](https://semver.org/lang/pt-BR/)

---

## [1.0.0] — 2026-06-24

### ✨ Adicionado
- JWT auth (HS512) + API Keys (SHA-256)
- QR estático (PNG, ZXing) e dinâmico (short code, redirect 302)
- Analytics de scans (Redis buffer + batch processor)
- Planos: Free, Starter ($9), Pro ($29), Business ($99)
- Stripe Checkout + Webhooks
- Rate limiting (Bucket4j)
- Cloudflare R2 storage
- Flyway V1–V7
- Arquitetura hexagonal + ArchUnit
- 33 testes (unit, integration, architecture)
- Frontend demo + Swagger
- Spring Boot Actuator

### 🔧 Corrigido
- JWT filter na chain, /error bloqueado, UUID reconstruído
- Flyway checksum, ArchUnit AWS SDK, R2 401/SignatureDoesNotMatch
- ScanCount zero, AuthControllerIT, passwordEncoder duplicado
- GlobalExceptionHandler duplicado, webhook 403, frontend 403

### 🏗 Refatorado
- Separação domain/application/infrastructure
- DTOs, Mappers, Eventos desacoplados

---

## [Próximas Versões]

### [1.1.0] — Rate Limit por Plano
- Bucket4j dinâmico (30/120/300/1000/min)

### [1.2.0] — QR com Logo
- Overlay de imagem central

### [1.3.0] — Analytics Avançado
- Scans por período, device/browser/país, export CSV

### [1.4.0] — Webhooks de Scan
- Notificar URL configurada

### [1.5.0] — Export CSV/PDF
- Relatórios de scans

### [2.0.0] — Custom Domains
- Domínios personalizados + SSL
