# QR Pro API

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen?logo=spring" alt="Spring Boot 3.3">
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql" alt="PostgreSQL 16">
  <img src="https://img.shields.io/badge/Redis-Upstash-red?logo=redis" alt="Redis">
  <img src="https://img.shields.io/badge/Cloudflare-R2-orange?logo=cloudflare" alt="Cloudflare R2">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License: MIT">
</p>

<p align="center">
  <b>API SaaS para geracao e rastreamento de QR Codes — estaticos e dinamicos</b>
</p>

<p align="center">
  <a href="https://qrcodepro-api.onrender.com/swagger-ui/index.html">Documentacao Swagger</a> •
  <a href="https://qrcodepro-api.onrender.com">Demo</a> •
  <a href="#quick-start">Quick Start</a>
</p>

---

## Demo ao Vivo

**URL de Producao:** https://qrcodepro-api.onrender.com

> O plano free do Render hiberna apos 15 min de inatividade. O primeiro acesso pode levar ~30s.

---

## Features

| Feature | Descricao | Status |
|---------|-----------|--------|
| Auth JWT | Register/login com tokens HS512 (24h) | ✅ |
| API Key | Server-to-server com SHA-256 hash | ✅ |
| QR Estatico | PNG no Cloudflare R2, cores e tamanho customizaveis | ✅ |
| QR Dinamico | ShortCode editavel, redirect 302, analytics em tempo real | ✅ |
| Analytics | ScanCount via Redis batch, historico completo de scans | ✅ |
| Arquitetura Hexagonal | Ports & Adapters, testavel e desacoplada | ✅ |
| OpenAPI/Swagger | Documentacao interativa automatica | ✅ |
| Rate Limiting | Bucket4j com Redis (distribuido) | ✅ |

---

## Tabela de Endpoints

### Autenticacao

| Metodo | Endpoint | Auth | Descricao |
|--------|----------|------|-----------|
| POST | /api/v1/auth/register | ❌ | Criar conta |
| POST | /api/v1/auth/login | ❌ | Login → JWT |

### QR Codes

| Metodo | Endpoint | Auth | Descricao |
|--------|----------|------|-----------|
| POST | /api/v1/qr/static | JWT / X-API-Key | Gerar QR estatico |
| POST | /api/v1/qr/dynamic | JWT / X-API-Key | Gerar QR dinamico |
| GET | /api/v1/qr | JWT / X-API-Key | Listar meus QRs |
| GET | /api/v1/qr/{id} | JWT / X-API-Key | Detalhes do QR |
| GET | /api/v1/qr/{id}/image | JWT / X-API-Key | Imagem PNG |
| GET | /api/v1/qr/{id}/scans | JWT / X-API-Key | Historico de scans |
| PUT | /api/v1/qr/{id}/destination | JWT / X-API-Key | Editar URL dinamico |
| PATCH | /api/v1/qr/{id}/activate | JWT / X-API-Key | Ativar QR |
| PATCH | /api/v1/qr/{id}/deactivate | JWT / X-API-Key | Desativar QR |
| DELETE | /api/v1/qr/{id} | JWT / X-API-Key | Deletar QR |

### Publico

| Metodo | Endpoint | Auth | Descricao |
|--------|----------|------|-----------|
| POST | /api/v1/public/qr/static | ❌ | QR publico (demo) |
| GET | /r/{shortCode} | ❌ | Redirect 302 (QR dinamico) |

### API Keys

| Metodo | Endpoint | Auth | Descricao |
|--------|----------|------|-----------|
| POST | /api/v1/api-keys | JWT | Gerar API Key |
| GET | /api/v1/api-keys | JWT | Listar keys |
| DELETE | /api/v1/api-keys/{id} | JWT | Revogar key |

### Health

| Metodo | Endpoint | Auth | Descricao |
|--------|----------|------|-----------|
| GET | /actuator/health | ❌ | Health check |

---

## Quick Start

### 1. Login

```bash
curl -X POST https://qrcodepro-api.onrender.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"gabriel","password":"senha123"}'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "gabriel",
  "email": "gabriel@email.com"
}
```

### 2. Gerar QR Estatico

```bash
curl -X POST https://qrcodepro-api.onrender.com/api/v1/qr/static \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "https://meusite.com",
    "size": 300,
    "foregroundColor": "#000000",
    "backgroundColor": "#FFFFFF"
  }'
```

### 3. Gerar QR Dinamico (com analytics)

```bash
curl -X POST https://qrcodepro-api.onrender.com/api/v1/qr/dynamic \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "destinationUrl": "https://promocao.com/black-friday",
    "size": 400
  }'
```

**Resposta:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "shortCode": "abc1234",
  "redirectUrl": "https://qrcodepro-api.onrender.com/r/abc1234",
  "imageUrl": "https://pub-0155d77db3504afe9926b532eae8b2c0.r2.dev/qr/abc1234.png",
  "scanCount": 0
}
```

### 4. Usar com API Key (server-to-server)

```bash
# Gerar API Key
curl -X POST https://qrcodepro-api.onrender.com/api/v1/api-keys?name=Producao \
  -H "Authorization: Bearer <SEU_TOKEN>"

# Usar a key
curl -H "X-API-Key: qrpro_live_..." \
  https://qrcodepro-api.onrender.com/api/v1/qr
```

---

## Arquitetura

```
Infrastructure (adapters)
  Web <- JWT Filter <- SecurityConfig
  JPA -> PostgreSQL
  Redis -> Cache + Scan Buffer
  S3 -> Cloudflare R2
  ZXing -> Geracao de QR
       ↑↓ ports
Application (use cases + services)
  AuthService, QrCodeService,
  ProcessScanService, ApiKeyService
  DTOs, Mappers, Events
       ↑↓ ports
Domain (regras puras)
  User, QrCode, ScanEvent, ApiKey
  ShortCode, QrCodeDesign, LogoConfig
  QrCodeGenerator, QrCodeValidator
```

---

## Stack Tecnica

| Camada | Tecnologia | Versao |
|--------|-----------|--------|
| Java | OpenJDK | 21 |
| Spring Boot | Framework | 3.3.0 |
| Spring Security | JWT + API Key | 6.3 |
| Spring Data JPA | PostgreSQL | 3.3 |
| Flyway | Migrations | 10.15 |
| Redis | Lettuce | 6.2 |
| ZXing | QR Generation | 3.5.3 |
| AWS SDK | S3 (R2) | 1.12.261 |
| Bucket4j | Rate Limiting | 8.10 |
| ArchUnit | Arquitetura | 1.2 |
| Swagger/OpenAPI | Docs | 2.5 |

---

## Rodando Localmente

### Pre-requisitos

- Java 21+
- Docker + Docker Compose
- Maven 3.9+

### 1. Clone o repositorio

```bash
git clone https://github.com/obobuque/qrcodepro-api.git
cd qrcodepro-api
```

### 2. Suba as dependencias

```bash
docker-compose up -d
```

> Isso sobe PostgreSQL e Redis localmente.

### 3. Configure o ambiente

```bash
cp .env.example .env
# Edite .env com suas credenciais
```

### 4. Rode a aplicacao

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Acesse: http://localhost:8080/swagger-ui/index.html

---

## Testes

```bash
# Unitarios + Integracao
./mvnw test

# Com cobertura
./mvnw verify
```

**33 testes passando**, incluindo:
- ArchUnit (protecao de arquitetura hexagonal)
- Testes de integracao com banco real
- Testes de controller com HTTP real

---

## Deploy

### Render (Producao)

1. Conecte seu repo GitHub ao Render
2. Crie um Web Service Java
3. Configure as variaveis de ambiente (veja .env.example)
4. Auto-deploy a cada git push

**Build Command:**
```bash
mvn clean package -DskipTests
```

**Start Command:**
```bash
java -Dspring.profiles.active=prod -jar target/qrpro-api-1.0.0.jar
```

---

## Roadmap

| # | Feature | Status |
|---|---------|--------|
| 1 | Documentacao publica | Em andamento |
| 2 | Frontend demo/vitrine | Em andamento |
| 3 | Planos e limites (Free/Pro/Business) | Planejado |
| 4 | Stripe (checkout + webhooks) | Planejado |
| 5 | QR com logo (feature Pro) | Planejado |
| 6 | Analytics avancado (graficos) | Planejado |
| 7 | Webhooks de scan | Planejado |
| 8 | Custom domains | Futuro |

---

## Contribuindo

Veja [CONTRIBUTING.md](CONTRIBUTING.md) para detalhes.

## Licenca

Distribuido sob licenca MIT. Veja [LICENSE](LICENSE) para mais informacoes.

---

<p align="center">
  Feito com cafe e Java por <a href="https://github.com/obobuque">@obobuque</a>
</p>
