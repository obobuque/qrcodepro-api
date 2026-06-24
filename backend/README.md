# QR Pro API 🚀

[![Java](https://img.shields.io/badge/Java-21-blue)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Upstash-red)](https://upstash.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> API profissional para geração e gerenciamento de QR Codes — estáticos e dinâmicos, com analytics, planos de assinatura (Stripe) e arquitetura hexagonal.

🌐 **Produção**: [https://qrcodepro-api.onrender.com](https://qrcodepro-api.onrender.com)

---

## ✨ Funcionalidades

| Feature | Status |
|---------|--------|
| QR Code Estático (PNG) | ✅ |
| QR Code Dinâmico (URL editável) | ✅ |
| Analytics de Scans (batch Redis) | ✅ |
| Autenticação JWT + API Key | ✅ |
| Planos de Assinatura (Free/Starter/Pro/Business) | ✅ |
| Checkout Stripe + Webhooks | ✅ |
| Rate Limiting (Bucket4j) | ✅ |
| Storage Cloudflare R2 | ✅ |
| Frontend Demo | ✅ |
| QR com Logo | 📋 |
| Webhooks de Scan | 📋 |
| Export CSV/PDF | 📋 |
| Custom Domains | 📋 |

---

## 🛠 Stack Tecnológica

- **Java 21** — LTS com Virtual Threads
- **Spring Boot 3.3** — Framework principal
- **Spring Security** — JWT + API Key auth
- **Spring Data JPA** — PostgreSQL 16
- **Flyway** — Migrations (V1–V7)
- **Redis (Upstash)** — Cache + Buffer de scans
- **Cloudflare R2** — Storage de imagens (AWS SDK v1)
- **ZXing** — Geração de QR Codes
- **Bucket4j** — Rate limiting
- **Stripe Java 24.0.0** — Pagamentos
- **ArchUnit** — Testes de arquitetura
- **Testcontainers** — Testes de integração
- **Swagger/OpenAPI** — Documentação interativa

---

## 🏗 Arquitetura

Arquitetura **Hexagonal (Ports & Adapters)**:

```
com.qrpro/
├── domain/          → User, QrCode, ScanEvent, Plan, Subscription
├── application/     → Services, Ports, DTOs, Mappers, Events
├── infrastructure/  → Controllers, Persistence, Cache, Storage, QR Generator
└── shared/          → Constants, validation
```

**Regras ArchUnit:** `domain` não depende de `application` ou `infrastructure`.

---

## 🔌 Endpoints

### Autenticação
| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/api/v1/auth/register` | ❌ | Criar conta |
| POST | `/api/v1/auth/login` | ❌ | Login → JWT |

### QR Codes
| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/api/v1/qr/static` | JWT/API Key | Gerar QR estático |
| POST | `/api/v1/qr/dynamic` | JWT/API Key | Gerar QR dinâmico |
| GET | `/api/v1/qr` | JWT/API Key | Listar QRs |
| GET | `/api/v1/qr/{id}` | JWT/API Key | Detalhes |
| GET | `/api/v1/qr/{id}/image` | JWT/API Key | Download PNG |
| GET | `/api/v1/qr/{id}/scans` | JWT/API Key | Histórico |
| PUT | `/api/v1/qr/{id}/destination` | JWT/API Key | Editar URL |
| PATCH | `/api/v1/qr/{id}/activate` | JWT/API Key | Ativar |
| PATCH | `/api/v1/qr/{id}/deactivate` | JWT/API Key | Desativar |
| DELETE | `/api/v1/qr/{id}` | JWT/API Key | Deletar |

### Público
| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/api/v1/public/qr/static` | ❌ | QR demo |
| GET | `/r/{shortCode}` | ❌ | Redirect 302 |

### API Keys
| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/api/v1/api-keys` | JWT | Gerar key |
| GET | `/api/v1/api-keys` | JWT | Listar keys |
| DELETE | `/api/v1/api-keys/{id}` | JWT | Revogar |

### Stripe
| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/api/v1/checkout` | JWT | Checkout |
| POST | `/api/v1/webhooks/stripe` | Stripe-Sig | Webhooks |

### Outros
| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/actuator/health` | ❌ | Health |
| GET | `/` | ❌ | Frontend demo |
| GET | `/swagger-ui.html` | ❌ | API Docs |

---

## 💳 Planos e Preços

| Plano | Preço | QR/mês | Scans/mês | Dynamic | Logo | Custom Colors | Webhooks | API Rate |
|-------|-------|--------|-----------|---------|------|---------------|----------|----------|
| **Free** | $0 | 10 | 100 | ❌ | ❌ | ✅ | ❌ | 30/min |
| **Starter** | $9 | 100 | 10.000 | ✅ | ❌ | ✅ | ❌ | 120/min |
| **Pro** | $29 | 1.000 | 100.000 | ✅ | ✅ | ✅ | ❌ | 300/min |
| **Business** | $99 | Ilimitado | 1.000.000 | ✅ | ✅ | ✅ | ✅ | 1000/min |

---

## 🚀 Instalação

```bash
git clone https://github.com/obobuque/qrcodepro-api.git
cd qrcodepro-api

# Dev local
docker-compose up -d
./mvnw clean spring-boot:run -Dspring-boot.run.profiles=dev

# Produção
mvn clean package -DskipTests
java -Dspring.profiles.active=prod -jar target/qrpro-api-1.0.0.jar
```

---

## ⚙️ Variáveis de Ambiente

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/qrpro
SPRING_DATASOURCE_USERNAME=qrpro
SPRING_DATASOURCE_PASSWORD=***
SPRING_DATA_REDIS_HOST=***
SPRING_DATA_REDIS_PORT=6379
JWT_SECRET=***
JWT_EXPIRATION=86400000
QR_BASE_URL=https://qrcodepro-api.onrender.com
R2_ACCESS_KEY_ID=***
R2_SECRET_ACCESS_KEY=***
R2_PUBLIC_URL=https://pub-***.r2.dev
STRIPE_SECRET_KEY=sk_***
STRIPE_WEBHOOK_SECRET=whsec_***
PORT=10000
```

---

## 🧪 Testes

```bash
./mvnw test
./mvnw verify
./mvnw test -Dtest=ArchitectureTest
```

**33 testes passando** — ArchitectureTest, AuthServiceTest, JwtTokenProviderTest, QrCodeValidatorTest, UserRepositoryAdapterIT, AuthControllerIT.

---

## 📖 Uso Rápido

```bash
TOKEN=$(curl -s -X POST https://qrcodepro-api.onrender.com/api/v1/auth/login   -H "Content-Type: application/json"   -d '{"username":"gabriel","password":"senha123"}' |   python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

curl -s -X POST -H "Authorization: Bearer $TOKEN"   -H "Content-Type: application/json"   -d '{"content":"https://example.com","size":300}'   https://qrcodepro-api.onrender.com/api/v1/qr/static
```

---

## 🤝 Contribuição

Veja [CONTRIBUTING.md](CONTRIBUTING.md).

## 📜 Licença

MIT — veja [LICENSE](LICENSE).

## 👤 Autor

**Gabriel** — [GitHub](https://github.com/obobuque)

> ⭐ Deixe uma star se te ajudou!
