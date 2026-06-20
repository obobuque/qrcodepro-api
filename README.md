# QR Pro API

API SaaS para geração e rastreamento de QR codes. Crie QR codes estáticos e dinâmicos, rastreie scans em tempo real e altere o destino dos QR dinâmicos sem reimprimir.

## Funcionalidades

- QR codes estáticos (conteúdo fixo)
- QR codes dinâmicos (destino alterável sem reimprimir)
- Rastreamento de scans (IP, user agent, timestamp)
- Autenticação JWT
- Cache Redis para redirects em alta velocidade
- Storage de imagens (local em dev, S3/MinIO em prod)
- Rate limiting por IP

## Stack

- Java 21 + Spring Boot 3.3
- PostgreSQL + Flyway
- Redis (cache + buffer de scans)
- MinIO (storage S3-compatível)
- Nginx (reverse proxy + rate limiting)
- Docker Compose

## Endpoints

### Auth
### Usuário
### QR Codes (requer Bearer token)
## Como rodar localmente

### Pré-requisitos
- Docker e Docker Compose
- Java 21
- Maven 3.9+

### 1. Clonar o repositório
```bash
git clone https://github.com/seu-usuario/qrpro-api.git
cd qrpro-api
```

### 2. Configurar o ambiente
```bash
cp backend/src/main/resources/application-dev.yml.example backend/src/main/resources/application-dev.yml
# Editar application-dev.yml com suas configurações locais
```

### 3. Subir a infraestrutura
```bash
docker compose up -d postgres redis minio
```

### 4. Rodar a API
```bash
cd backend
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Subir tudo com Docker (opcional)
```bash
docker compose up --build
```

## Exemplo de uso

### Registrar e autenticar
```bash
# Registrar
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"gabriel","email":"gabriel@email.com","password":"senha123"}'

# Resposta
{"token": "eyJhbGci...", "type": "Bearer", "username": "gabriel", "email": "gabriel@email.com"}
```

### Criar QR code estático
```bash
TOKEN="eyJhbGci..."

curl -X POST http://localhost:8080/api/v1/qr/static \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"content":"https://meusite.com","ownerId":"seu-uuid"}'
```

### Criar QR code dinâmico
```bash
curl -X POST http://localhost:8080/api/v1/qr/dynamic \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"initialUrl":"https://meusite.com/promo","ownerId":"seu-uuid"}'
```

### Alterar destino do QR dinâmico
```bash
curl -X PUT http://localhost:8080/api/v1/qr/{id}/destination \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"newUrl":"https://meusite.com/nova-promo"}'
```

## Testes

```bash
cd backend
mvn test
```

33 testes: 4 arquitetura (ArchUnit) + 10 integração + 19 unitários.

## Variáveis de ambiente (produção)

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `SPRING_DATA_REDIS_HOST` | Host do Redis |
| `SPRING_DATA_REDIS_PORT` | Porta do Redis (padrão 6379) |
| `MINIO_ENDPOINT` | Endpoint do MinIO/S3 |
| `MINIO_ACCESS_KEY` | Access key |
| `MINIO_SECRET_KEY` | Secret key |
| `MINIO_BUCKET` | Nome do bucket |
| `JWT_SECRET` | Secret JWT (mínimo 64 caracteres) |
| `JWT_EXPIRATION` | Expiração em ms (padrão 3600000 = 1h) |
| `QR_BASE_URL` | URL base para QR dinâmicos (ex: https://qrpro.com/r/) |

## Licença

MIT
