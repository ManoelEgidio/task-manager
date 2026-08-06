# 📝 Task Manager

Aplicação Fullstack de um Gerenciador de Tarefas.

O sistema permite criar, listar, atualizar e remover tarefas através de uma API REST desenvolvida com Spring Boot e consumida por uma SPA em Angular.

---

## ✨ Funcionalidades

- ✅ Criar tarefas (título obrigatório mín. 3 caracteres e descrição opcional)
- ✅ Listar tarefas
- ✅ Buscar tarefas com filtros dinâmicos (título, descrição, status de conclusão)
- ✅ Atualizar tarefas (título, descrição, status)
- ✅ Alternar status (concluída / pendente)
- ✅ Excluir tarefas
- ✅ Paginação e ordenação
- ✅ Documentação OpenAPI (Swagger)

---

## 🏗️ Arquitetura

```
   Angular 20 (SPA)
          │
      HTTP REST
          │
   Spring Boot 3
          │
     Service Layer
          │
     Repository
          │
┌─────────┴─────────┐
│                   │
H2 (Dev)    PostgreSQL (Testcontainers / Produção)
```

---

## 🚀 Tecnologias

| Camada | Tecnologias |
|---------|-------------|
| **Backend** | Java 21, Spring Boot 3.4.2 |
| **Persistência** | Spring Data JPA, Hibernate |
| **Banco (Desenvolvimento)** | H2 Database (em memória) |
| **Banco (Integração)** | PostgreSQL 17 + Testcontainers |
| **Frontend** | Angular 20 |
| **Documentação** | OpenAPI 3 / Swagger UI |
| **Testes** | JUnit 5, Mockito, AssertJ, MockMvc |
| **Qualidade** | JaCoCo (**93% de Cobertura**) |
| **Containers** | Docker & Docker Compose |
| **CI** | GitHub Actions |

---

## 📁 Estrutura do Projeto

```
task-manager-fullstack/
├── api/                   # Backend Spring Boot 3
├── ui/                    # Frontend Angular 20
├── docker-compose.local.yml # Docker Compose Desenvolvimento (H2)
├── docker-compose.prod.yml  # Docker Compose Produção (PostgreSQL)
└── README.md              # Documentação
```

---

## 🎯 Padrões Utilizados

- **Service Layer Pattern**
- **Repository Pattern**
- **Factory Pattern** (`TaskFactory`)
- **Specification Pattern** (`JpaSpecificationExecutor`)
- **DTO Pattern** (`TaskRequestDTO`, `TaskResponseDTO`, `TaskFilterDTO`)
- **Builder Pattern** (`@Builder` Lombok)
- **Dependency Injection** (Injeção via Construtor com `@RequiredArgsConstructor`)

---

# ▶️ Executando o Projeto

## Desenvolvimento (Local)

```bash
docker compose -f docker-compose.local.yml up --build -d
```

### Serviços Disponíveis

| Serviço | URL |
|----------|-----|
| **API REST** | http://localhost:8080 |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html |
| **Console H2** | http://localhost:8080/h2-console |
| **Frontend Angular** | http://localhost:4200 |

---

## Produção

```bash
docker compose -f docker-compose.prod.yml up --build -d
```

Em ambiente de produção a aplicação utiliza **PostgreSQL** configurado via variáveis de ambiente com suporte a SSL (`?sslmode=require`).

---

# 📚 Endpoints da API

| Método | Endpoint | Descrição |
|---------|----------|-----------|
| `GET` | `/api/v1/tasks` | Lista e filtra tarefas com busca dinâmica e paginação |
| `GET` | `/api/v1/tasks/{id}` | Busca os detalhes de uma tarefa pelo ID |
| `POST` | `/api/v1/tasks` | Cadastra uma nova tarefa |
| `PUT` | `/api/v1/tasks/{id}` | Atualiza título, descrição e status de uma tarefa |
| `PATCH` | `/api/v1/tasks/{id}/toggle` | Alterna o status da tarefa entre concluída e pendente |
| `DELETE` | `/api/v1/tasks/{id}` | Remove uma tarefa |

A documentação completa e interativa está disponível no **Swagger UI**.

---

# 🧪 Testes

### Executar todos os testes e gerar relatório de cobertura JaCoCo

```bash
cd api
./gradlew test jacocoTestReport
```

*Caso o Docker esteja rodando, os testes de integração sobem uma instância real do **PostgreSQL 17** através do **Testcontainers**.*

### Executar os testes via Docker (sem precisar de Java/Gradle instalados no host):

```bash
docker compose -f docker-compose.local.yml run --rm api ./gradlew test jacocoTestReport
```

O relatório visual de cobertura em HTML é gerado em:
`api/build/reports/jacoco/test/html/index.html`

---

# ⚙️ Integração Contínua (CI)

O **GitHub Actions** (`.github/workflows/ci.yml`) executa automaticamente a cada `push`:

- Build da aplicação em Java 21
- Suíte de testes unitários
- Testes de integração E2E com Testcontainers
- Relatório de Cobertura JaCoCo (disponível para download na aba Actions)

---

# ☁️ Deploy em Nuvem

| Aplicação | URL |
|---|---|
| **Backend (Cloud Run)** | `` |
| **Swagger UI (Cloud Run)** | `/swagger-ui/index.html` |
| **Frontend** | `` |