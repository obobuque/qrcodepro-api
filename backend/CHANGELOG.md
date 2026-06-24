# Changelog

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [1.0.0] — 2026-06-24

### ✨ Adicionado
- **Autenticação JWT** — Login e registro com tokens HS512
- **API Keys** — Autenticação server-to-server com SHA-256
- **QR Code Estático** — Geração PNG com ZXing, cores customizáveis
- **QR Code Dinâmico** — URL editável com short code e redirect 302
- **Analytics de Scans** — Buffer Redis + batch processor (500/10s)
- **Planos de Assinatura** — Free, Starter ($9), Pro ($29), Business ($99)
- **Stripe Checkout** — Sessões de pagamento com mode=SUBSCRIPTION
- **Stripe Webhooks** — checkout.session.completed, customer.subscription.deleted, invoice.paid
- **Rate Limiting** — Bucket4j (60/min global, planos dinâmicos em backlog)
- **Cloudflare R2** — Storage de imagens com AWS SDK v1
- **Flyway Migrations** — V1 a V7
- **Arquitetura Hexagonal** — Ports & Adapters com ArchUnit
- **Testes** — 33 testes (unit, integration, architecture)
- **Frontend Demo** — Página `/` com formulários interativos
- **Swagger/OpenAPI** — Documentação interativa em `/swagger-ui.html`
- **Spring Boot Actuator** — Health checks para DB, Redis, disk

### 🔧 Corrigido
- JWT filter não registrado na chain → addFilterBefore() no SecurityConfig
- /error bloqueado pelo Spring Security → .requestMatchers("/error").permitAll()
- UUID gerado novo a cada toDomain() → QrCode.reconstruct() preserva ID
- Flyway checksum conflito V3 → DELETE FROM flyway_schema_history + recriação
- ArchUnit quebrando com AWS SDK → Adicionar com.amazonaws.. na whitelist
- Storage local some no Render → Migrado para Cloudflare R2
- R2 retornando 401/SignatureDoesNotMatch → Secret key recriada + região us-east-1
- ScanCount sempre zero → Serialização flat JSON no Redis + batch processor
- AuthControllerIT falhando → @BeforeEach com JdbcTemplate limpando tabelas
- Bean passwordEncoder duplicado → Removido de BeanConfig, mantido em SecurityConfig
- GlobalExceptionHandler duplicado → Unificado em web/advice/
- Webhook retornando 403 → Adicionado /api/v1/webhooks/** à whitelist
- Frontend demo retornando 403 → Adicionado / e /index.html à whitelist

### 🏗 Refatorado
- Separação clara domain/application/infrastructure
- DTOs para todos os endpoints
- Mappers Domain ↔ DTO
- Eventos de domínio desacoplados

---

## [Próximas Versões]

### [1.1.0] — Rate Limit por Plano
- Bucket4j com limites dinâmicos (30/120/300/1000 por minuto)

### [1.2.0] — QR com Logo
- Overlay de imagem central no QR Code

### [1.3.0] — Analytics Avançado
- Endpoints: scans por dia/semana/mês
- Agrupamento por device/browser/país
- Export CSV

### [1.4.0] — Webhooks de Scan
- Notificar URL configurada quando QR é escaneado

### [1.5.0] — Export CSV/PDF
- Download de relatórios de scans

### [2.0.0] — Custom Domains
- Domínios personalizados para redirects
- SSL automático via Let's Encrypt
