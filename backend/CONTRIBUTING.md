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
Use o template [Feature Request](.github/ISSUE_TEMPLATE/feature_request.md).

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
- **Ports & Adapters** para novas integrações

#### Commit messages (Conventional Commits)
```
feat: adicionar QR com logo
fix: corrigir rate limit
docs: atualizar README
refactor: migrar storage
test: adicionar testes
```

#### PR Checklist
- [ ] Código compila sem warnings
- [ ] Todos os testes passam
- [ ] ArchUnit não quebra
- [ ] Flyway migrations testadas
- [ ] Documentação atualizada
- [ ] Sem credenciais hardcoded
- [ ] Sem `System.out.println`

---

## 🏗 Arquitetura

### Regras ArchUnit
- `domain` **não pode** depender de `application` ou `infrastructure`
- `application` **não pode** depender de `infrastructure`
- `infrastructure` **pode** depender de `domain` e `application`

### Adicionando um novo adapter
1. Crie a interface Port em `application/port/out/`
2. Implemente em `infrastructure/adapter/out/.../`
3. Registre como Bean em `infrastructure/config/`
4. Adicione testes de integração

---

## 🧪 Testes

```bash
./mvnw test -Dtest=ArchitectureTest
./mvnw verify -Pit
./mvnw jacoco:report
```

---

## 📋 Backlog

| # | Item | Status |
|---|------|--------|
| 1 | Rate limit por plano | 🔄 |
| 2 | QR com logo | 📋 |
| 3 | Analytics avançado | 📋 |
| 4 | Webhooks de scan | 📋 |
| 5 | Export CSV/PDF | 📋 |
| 6 | Custom domains | 📋 |

---

## 💬 Comunicação

- **Issues**: Bugs e features
- **Discussions**: Dúvidas e ideias
- **Pull Requests**: Code review

## 📜 Código de Conduta

Seja respeitoso, construtivo e inclusivo.

**Obrigado por contribuir!** 🙏
