# Contribuindo com QR Pro API

Obrigado pelo interesse em contribuir!

## Como contribuir

### 1. Reportando bugs

- Verifique se o bug ja foi reportado nas Issues
- Inclua passos para reproduzir, comportamento esperado vs atual, e logs se houver
- Use o label bug

### 2. Sugerindo features

- Abra uma Issue com o label enhancement
- Descreva o problema que a feature resolve e como voce imagina a solucao

### 3. Enviando codigo

1. Fork o repositorio
2. Clone seu fork: git clone https://github.com/SEU_USER/qrcodepro-api.git
3. Crie uma branch: git checkout -b feature/minha-feature
4. Faca as alteracoes seguindo os padroes abaixo
5. Teste: ./mvnw test (todos os 33+ testes devem passar)
6. Commit: git commit -m "feat: descricao clara"
7. Push: git push origin feature/minha-feature
8. Abra um Pull Request

## Padroes de codigo

### Commits (Conventional Commits)

- feat: nova feature
- fix: correcao de bug
- docs: documentacao
- style: formatacao (sem mudanca de codigo)
- refactor: refatoracao
- test: adicionar/corrigir testes
- chore: build, dependencias, etc.

### Arquitetura

- Domain nao depende de nenhuma outra camada
- Application depende apenas de Domain
- Infrastructure depende de Domain e Application
- Use Ports & Adapters (interfaces no domain/application, implementacoes no infrastructure)
- Testes unitarios com Mockito, integracao com banco real

### Checklist antes de commitar

- [ ] ./mvnw test passa (33+ testes)
- [ ] ./mvnw verify passa (inclui ArchUnit)
- [ ] Codigo segue o padrao do projeto
- [ ] Novos endpoints documentados no OpenAPI
- [ ] Migrations Flyway criadas se necessario

## Ambiente de desenvolvimento

```bash
# 1. Clone
git clone https://github.com/obobuque/qrcodepro-api.git
cd qrcodepro-api

# 2. Dependencias
docker-compose up -d

# 3. Configure
cp .env.example .env
# Edite .env

# 4. Rode
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 5. Teste
./mvnw test
```

## Duvidas?

Abra uma Issue ou entre em contato: gblgabriel111@gmail.com
