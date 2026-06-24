# API Documentation — QR Pro API

> Documentação completa dos endpoints REST. Acesse `/swagger-ui.html` para docs interativas.

---

## 🔐 Autenticação

**JWT:** `Authorization: Bearer <token>`
**API Key:** `X-API-Key: <api-key>`

---

## 📊 Códigos de Status

| Código | Significado |
|--------|-------------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 429 | Too Many Requests — limite de plano |
| 500 | Internal Server Error |

### Erro 429
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
**Request:**
```json
{"username": "gabriel", "email": "gabriel@email.com", "password": "senha123"}
```
**Response 201:**
```json
{"id": "...", "username": "gabriel", "email": "gabriel@email.com", "plan": "free", "createdAt": "2026-06-24T10:00:00Z"}
```

### POST /api/v1/auth/login
**Request:**
```json
{"username": "gabriel", "password": "senha123"}
```
**Response 200:**
```json
{"token": "eyJhbGciOiJIUzUxMiIs...", "type": "Bearer", "expiresIn": 86400}
```

---

## 📱 QR Codes

### POST /api/v1/qr/static
**Headers:** `Authorization: Bearer <token>` ou `X-API-Key: <key>`
**Request:**
```json
{"content": "https://meusite.com", "size": 300, "foregroundColor": "#000000", "backgroundColor": "#FFFFFF"}
```
**Response 201:**
```json
{"id": "...", "type": "STATIC", "content": "https://meusite.com", "size": 300, "imageUrl": "https://pub-***.r2.dev/qr/....png", "scanCount": 0, "createdAt": "..."}
```

### POST /api/v1/qr/dynamic
**Request:**
```json
{"initialUrl": "https://campanha.com/promo", "size": 300}
```
**Response 201:**
```json
{"id": "...", "type": "DYNAMIC", "shortCode": "a3f9k2", "currentUrl": "https://campanha.com/promo", "redirectUrl": "https://qrcodepro-api.onrender.com/r/a3f9k2", "imageUrl": "...", "scanCount": 0, "active": true, "createdAt": "..."}
```

### GET /api/v1/qr
**Query:** `page`, `size`, `type`, `active`
**Response 200:** Paginação de QRs.

### GET /api/v1/qr/{id}
**Response 200:** Objeto QRCode completo.

### GET /api/v1/qr/{id}/image
**Response 200:** `image/png` (binary)

### GET /api/v1/qr/{id}/scans
**Query:** `page`, `size`, `from`, `to`
**Response 200:**
```json
{"content": [{"id": "...", "qrCodeId": "...", "scannedAt": "2026-06-24T14:30:00Z", "ipAddress": "192.168.1.1", "userAgent": "...", "country": "BR", "device": "mobile"}], "totalElements": 42, "totalPages": 1}
```

### PUT /api/v1/qr/{id}/destination
**Request:** `{"newUrl": "https://nova-url.com"}`
**Response 200:** QRCode atualizado.
**Response 400:** QR é estático.

### PATCH /api/v1/qr/{id}/activate
**Response 204**

### PATCH /api/v1/qr/{id}/deactivate
**Response 204**

### DELETE /api/v1/qr/{id}
**Response 204**

---

## 🌐 Público

### POST /api/v1/public/qr/static
**Auth:** ❌
**Request:** `{"content": "https://example.com", "size": 300}`
**Response 201:** `image/png`
**Limites:** 5 req/min por IP.

### GET /r/{shortCode}
**Auth:** ❌
**Response 302:** Redirect para `currentUrl`
**Response 410:** QR desativado
**Response 404:** Short code não encontrado

---

## 🔑 API Keys

### POST /api/v1/api-keys
**Headers:** `Authorization: Bearer <token>`
**Request:** `{"name": "App Mobile", "expiresInDays": 90}`
**Response 201:**
```json
{"id": "...", "name": "App Mobile", "apiKey": "qrpro_live_...", "createdAt": "...", "expiresAt": "..."}
```
> ⚠️ Chave plain só aparece neste momento.

### GET /api/v1/api-keys
**Response 200:** Array (sem `apiKey`).

### DELETE /api/v1/api-keys/{id}
**Response 204**

---

## 💳 Stripe

### POST /api/v1/checkout
**Request:** `{"planId": "starter", "successUrl": "...", "cancelUrl": "..."}`
**Response 200:**
```json
{"sessionId": "cs_test_...", "checkoutUrl": "https://checkout.stripe.com/..."}
```

### POST /api/v1/webhooks/stripe
**Auth:** `Stripe-Signature`
**Eventos:** `checkout.session.completed`, `customer.subscription.deleted`, `invoice.paid`
**Response 200:** `{"received": true}`

---

## 🏥 Health

### GET /actuator/health
**Response 200:**
```json
{"status": "UP", "components": {"db": {"status": "UP"}, "redis": {"status": "UP"}, "diskSpace": {"status": "UP"}}}
```

---

## 📝 Notas

- Timestamps em **UTC** (ISO 8601)
- IDs são **UUID v4**
- Short codes: **alphanumeric, 6 chars**
- Rate limits por **minuto**, variam por plano
- Scans em **batch** (Redis → PostgreSQL a cada 10s)
