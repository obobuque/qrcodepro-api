# API Documentation — QR Pro API

> Documentação completa dos endpoints REST. Para documentação interativa, acesse `/swagger-ui.html` em ambiente de desenvolvimento.

---

## 🔐 Autenticação

A API suporta dois métodos:

### 1. JWT (Bearer Token)
```
Authorization: Bearer <token>
```

### 2. API Key
```
X-API-Key: <api-key>
```

---

## 📊 Códigos de Status

| Código | Significado |
|--------|-------------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request — dados inválidos |
| 401 | Unauthorized — token/API key inválido |
| 403 | Forbidden — acesso negado |
| 404 | Not Found — recurso não existe |
| 409 | Conflict — conflito |
| 429 | Too Many Requests — limite de plano ou rate limit |
| 500 | Internal Server Error |

### Erro 429 — Limite de Plano
```json
{
  "type": "https://qrcodepro-api.onrender.com/errors/plan-limit-exceeded",
  "title": "Plan limit exceeded",
  "status": 429,
  "planId": "free",
  "limitType": "qr_codes",
  "upgradeUrl": "https://qrcodepro-api.onrender.com/api/v1/checkout"
}
```

---

## 👤 Autenticação

### POST /api/v1/auth/register
Registra um novo usuário (recebe plano Free automaticamente).

**Request:**
```json
{
  "username": "gabriel",
  "email": "gabriel@email.com",
  "password": "senha123"
}
```

**Response 201:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "gabriel",
  "email": "gabriel@email.com",
  "plan": "free",
  "createdAt": "2026-06-24T10:00:00Z"
}
```

**Response 409:** Username ou email já existe.

---

### POST /api/v1/auth/login
Autentica e retorna JWT.

**Request:**
```json
{
  "username": "gabriel",
  "password": "senha123"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiIs...",
  "type": "Bearer",
  "expiresIn": 86400
}
```

---

## 📱 QR Codes

### POST /api/v1/qr/static
Gera um QR Code estático (conteúdo fixo).

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Request:**
```json
{
  "content": "https://meusite.com",
  "size": 300,
  "foregroundColor": "#000000",
  "backgroundColor": "#FFFFFF"
}
```

**Response 201:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "type": "STATIC",
  "content": "https://meusite.com",
  "size": 300,
  "imageUrl": "https://pub-***.r2.dev/qr/550e8400-e29b-41d4-a716-446655440001.png",
  "scanCount": 0,
  "createdAt": "2026-06-24T10:00:00Z"
}
```

**Response 429:** Limite de QR codes do plano atingido.

---

### POST /api/v1/qr/dynamic
Gera um QR Code dinâmico (URL editável).

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Request:**
```json
{
  "initialUrl": "https://campanha.com/promo",
  "size": 300,
  "foregroundColor": "#000000",
  "backgroundColor": "#FFFFFF"
}
```

**Response 201:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "type": "DYNAMIC",
  "shortCode": "a3f9k2",
  "currentUrl": "https://campanha.com/promo",
  "redirectUrl": "https://qrcodepro-api.onrender.com/r/a3f9k2",
  "size": 300,
  "imageUrl": "https://pub-***.r2.dev/qr/550e8400-e29b-41d4-a716-446655440002.png",
  "scanCount": 0,
  "active": true,
  "createdAt": "2026-06-24T10:00:00Z"
}
```

**Response 429:** Plano não permite QR dinâmico ou limite de QR atingido.

---

### GET /api/v1/qr
Lista todos os QR codes do usuário.

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Query params:** `page`, `size`, `type` (STATIC/DYNAMIC), `active` (boolean)

**Response 200:**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "type": "STATIC",
      "content": "https://meusite.com",
      "imageUrl": "https://pub-***.r2.dev/qr/...",
      "scanCount": 42,
      "createdAt": "2026-06-24T10:00:00Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

---

### GET /api/v1/qr/{id}
Detalhes de um QR code específico.

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Response 200:** Objeto QRCode completo.

---

### GET /api/v1/qr/{id}/image
Download da imagem PNG do QR code.

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Response 200:** `image/png` (binary)

---

### GET /api/v1/qr/{id}/scans
Histórico de scans de um QR code.

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Query params:** `page`, `size`, `from`, `to` (ISO 8601)

**Response 200:**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440003",
      "qrCodeId": "550e8400-e29b-41d4-a716-446655440002",
      "scannedAt": "2026-06-24T14:30:00Z",
      "ipAddress": "192.168.1.1",
      "userAgent": "Mozilla/5.0...",
      "referer": "https://google.com",
      "country": "BR",
      "device": "mobile"
    }
  ],
  "totalElements": 42,
  "totalPages": 1,
  "size": 50,
  "number": 0
}
```

---

### PUT /api/v1/qr/{id}/destination
Atualiza a URL de destino de um QR dinâmico.

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Request:**
```json
{
  "newUrl": "https://campanha.com/nova-promo"
}
```

**Response 200:** QRCode atualizado.

**Response 400:** QR é estático (não editável).

---

### PATCH /api/v1/qr/{id}/activate
Ativa um QR code.

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Response 204:** No Content

---

### PATCH /api/v1/qr/{id}/deactivate
Desativa um QR code (scans retornam 410).

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Response 204:** No Content

---

### DELETE /api/v1/qr/{id}
Remove um QR code e sua imagem.

**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`

**Response 204:** No Content

---

## 🌐 Público

### POST /api/v1/public/qr/static
Gera QR estático sem autenticação (demo).

**Request:**
```json
{
  "content": "https://example.com",
  "size": 300
}
```

**Response 201:** Retorna imagem PNG diretamente (`image/png`).

**Limites:** 5 requisições por minuto por IP.

---

### GET /r/{shortCode}
Redirect para a URL atual do QR dinâmico.

**Auth:** ❌ Nenhuma

**Response 302:** Redirect para `currentUrl`

**Response 410:** QR desativado

**Response 404:** Short code não encontrado

---

## 🔑 API Keys

### POST /api/v1/api-keys
Gera uma nova API Key.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "name": "Produção App Mobile",
  "expiresInDays": 90
}
```

**Response 201:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440004",
  "name": "Produção App Mobile",
  "apiKey": "qrpro_live_0d3213459881427b913929e65610aa60",
  "createdAt": "2026-06-24T10:00:00Z",
  "expiresAt": "2026-09-22T10:00:00Z"
}
```

> ⚠️ A chave plain só é exibida neste momento. Depois só o hash SHA-256 é armazenado.

---

### GET /api/v1/api-keys
Lista API Keys do usuário (sem exibir a chave plain).

**Headers:** `Authorization: Bearer <token>`

**Response 200:** Array de API Keys (sem `apiKey` field).

---

### DELETE /api/v1/api-keys/{id}
Revoga uma API Key.

**Headers:** `Authorization: Bearer <token>`

**Response 204:** No Content

---

## 💳 Stripe

### POST /api/v1/checkout
Cria uma sessão de checkout Stripe.

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "planId": "starter",
  "successUrl": "https://meusite.com/sucesso",
  "cancelUrl": "https://meusite.com/cancelado"
}
```

**Response 200:**
```json
{
  "sessionId": "cs_test_a1b2c3d4e5f6...",
  "checkoutUrl": "https://checkout.stripe.com/c/pay/cs_test_a1b2c3d4e5f6..."
}
```

**Planos válidos:** `starter`, `pro`, `business`

---

### POST /api/v1/webhooks/stripe
Recebe webhooks do Stripe.

**Auth:** Validação via `Stripe-Signature` header

**Eventos processados:**
- `checkout.session.completed` → Ativa assinatura
- `customer.subscription.deleted` → Desativa assinatura
- `invoice.paid` → Renova assinatura

**Response 200:** `{ "received": true }`

---

## 🏥 Health

### GET /actuator/health
Verifica saúde da aplicação.

**Auth:** ❌ Nenhuma

**Response 200:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

## 📁 Frontend Demo

### GET /
Página de demonstração do serviço.

**Auth:** ❌ Nenhuma

**Response 200:** `text/html` — Página interativa com formulários para testar a API.

---

## 📝 Notas

- Todos os timestamps são em **UTC** (ISO 8601)
- IDs de recursos são **UUID v4**
- Short codes são **alphanumeric, 6 caracteres**
- Rate limits são por **minuto** e variam por plano
- Scans são processados em **batch** (Redis buffer → PostgreSQL a cada 10s)
