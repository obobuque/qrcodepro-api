# Guia de Contribuição — QR Pro API

Obrigado pelo interesse em contribuir! 🎉

---

## 🚀 Como Contribuir

### 1. Reportar Bugs

Use o template [Bug Report](.github/ISSUE_TEMPLATE/bug_report.md). Inclua:
- Passos para reproduzir
- Comportamento esperado vs atual
- Logs e stack traces
- Ambiente (Java version, SO, etc.)

### 2. Sugerir Features

Use o template [Feature Request](.github/ISSUE_TEMPLATE/feature_request.md). Descreva:
- O problema que a feature resolve
- Solução proposta
- Alternativas consideradas

### 3. Enviar Pull Requests

#### Setup
```bash
git clone https://github.com/obobuque/qrcodepro-api.git
cd qrcodepro-api
```

#### Branch naming
```
feature/qr-com-logo
fix/rate-limit-planos
docs/atualizar-readme
refactor/melhorar-cache
```

#### Antes de commitar
```bash
./mvnw clean compile
./mvnw verify
./mvnw test -Dtest=ArchitectureTest
```

#### Regras de código
- **Java 21** com Virtual Threads quando aplicável
- **Arquitetura Hexagonal** — nunca importe infrastructure em domain
- **Testes obrigatórios** para novas features
- **Flyway migrations** para alterações de schema
- **DTOs** para entrada/saída de controllers
- **Ports & Adapters** para novas integrações externas

#### Commit messages (Conventional Commits)
```
feat: adicionar QR com logo
fix: corrigir rate limit para planos dinâmicos
docs: atualizar README com novos endpoints
refactor: migrar storage para R2 SDK v2
test: adicionar testes para SubscriptionService
```

#### PR Checklist
- [ ] Código compila sem warnings
- [ ] Todos os testes passam (`./mvnw verify`)
- [ ] ArchUnit não quebra
- [ ] Flyway migrations testadas
- [ ] Documentação atualizada (README, API.md, etc.)
- [ ] Sem credenciais hardcoded
- [ ] Sem `System.out.println` (use SLF4J)

---

## 🏗 Arquitetura

### Regras de Dependência (ArchUnit)
- `domain` **não pode** depender de `application` ou `infrastructure`
- `application` **não pode** depender de `infrastructure`
- `infrastructure` **pode** depender de `domain` e `application`
- `shared` **não pode** depender de nenhum outro pacote

### Adicionando um novo adapter
1. Crie a interface Port em `application/port/out/`
2. Implemente em `infrastructure/adapter/out/.../`
3. Registre como Bean em `infrastructure/config/`
4. Adicione testes de integração

---

## 🧪 Testes

### Estrutura
```
src/test/java/com/qrpro/
├── architecture/       → ArchUnit tests
├── domain/            → Testes de lógica pura
├── application/       → Testes de serviços (mocks)
└── infrastructure/    → Testes de integração (Testcontainers)
```

### Rodar testes específicos
```bash
./mvnw test -Dtest=ArchitectureTest
./mvnw verify -Pit
./mvnw jacoco:report
```

---

## 📋 Backlog Atual

| # | Item | Status | Dificuldade |
|---|------|--------|-------------|
| 1 | Rate limit por plano | 🔄 | Média |
| 2 | QR com logo | 📋 | Média |
| 3 | Analytics avançado | 📋 | Alta |
| 4 | Webhooks de scan | 📋 | Média |
| 5 | Export CSV/PDF | 📋 | Baixa |
| 6 | Custom domains | 📋 | Alta |

Quer pegar um item? Comente na issue correspondente!

---

## 💬 Comunicação

- **Issues**: Bugs e features
- **Discussions**: Dúvidas e ideias gerais
- **Pull Requests**: Code review

---

## 📜 Código de Conduta

Seja respeitoso, construtivo e inclusivo. Diferenças de opinião são bem-vindas, mas sempre com empatia.

---

**Obrigado por contribuir!** 🙏
