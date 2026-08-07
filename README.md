
# 📝 Task Manager

Aplicação Fullstack de um Gerenciador de Tarefas.

O sistema permite criar, listar, atualizar e remover tarefas através de uma API REST desenvolvida com Spring Boot e consumida por uma SPA em Angular.

<img width="1889" height="931" alt="print" src="https://github.com/user-attachments/assets/689f9903-10e4-4ba7-b19f-23be0a4281f0" />

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
  Spring Boot 3.4.2
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
| **Migrações** | Flyway Migrations |
| **Frontend** | Angular 20 (Gerenciador de Pacotes: **pnpm**) |
| **Documentação** | OpenAPI 3 / Swagger UI (Ativo em Dev, desativado em Prod por Segurança) |
| **Testes** | JUnit 5, Mockito, AssertJ, MockMvc |
| **Qualidade** | JaCoCo (**93% de Cobertura**) |
| **Containers** | Docker & Docker Compose |
| **CI/CD** | GitHub Actions & Google Cloud Run |

---

## 📁 Estrutura do Projeto

```
task-manager-fullstack/
├── api/                   # Backend Spring Boot 3.4.2 (Java 21)
│   └── src/main/resources/db/migration/ # Scripts de Migração SQL (Flyway)
├── ui/                    # Frontend Angular 20 (gerenciado via pnpm)
├── docker-compose.local.yml # Docker Compose Desenvolvimento (H2)
├── docker-compose.prod.yml  # Docker Compose Produção (PostgreSQL)
└── README.md              # Documentação
```

---

## 🎯 Padrões & Práticas Utilizadas

- **Service Layer Pattern**
- **Repository Pattern**
- **Database Migrations** (Flyway com `ddl-auto=validate`)
- **Spring Data JPA Auditing** (`@CreatedDate`, `@LastModifiedDate`, `@EntityListeners`)
- **Security Hardening** (Swagger UI habilitado apenas em `local` para navegação interativa e desativado em `prod` para proteção de superfície de ataque)
- **Factory Pattern** (`TaskFactory`)
- **Specification Pattern** (`JpaSpecificationExecutor`)
- **DTO Pattern** (`TaskRequestDTO`, `TaskResponseDTO`, `TaskFilterDTO`, `TaskSummaryDTO`)
- **Builder Pattern** (`@Builder` Lombok)
- **Dependency Injection** (Injeção via Construtor com `@RequiredArgsConstructor`)

---

# ▶️ Executando o Projeto

## Desenvolvimento com Docker (Recomendado)

```bash
docker compose -f docker-compose.local.yml up --build -d
```

## Desenvolvimento Backend Manual (sem Docker)

Para rodar apenas a API REST em Spring Boot (banco H2 em memória):

```bash
cd api
./gradlew bootRun
```
*(No Windows: `.\gradlew.bat bootRun`)*

## Desenvolvimento Frontend Manual (sem Docker)

Para rodar apenas o frontend Angular usando `pnpm`:

```bash
cd ui
pnpm install
pnpm start
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
| `GET` | `/api/v1/tasks/summary` | Retorna o resumo estatístico (total, pendentes e concluídas) |
| `GET` | `/api/v1/tasks` | Lista e filtra tarefas com busca dinâmica e paginação |
| `GET` | `/api/v1/tasks/{id}` | Busca os detalhes de uma tarefa pelo ID |
| `POST` | `/api/v1/tasks` | Cadastra uma nova tarefa |
| `PUT` | `/api/v1/tasks/{id}` | Atualiza título, descrição e status de uma tarefa |
| `PATCH` | `/api/v1/tasks/{id}/toggle` | Alterna o status da tarefa entre concluída e pendente |
| `DELETE` | `/api/v1/tasks/{id}` | Remove uma tarefa |

A documentação completa e interativa está disponível no **Swagger UI** (no perfil `local`). Por razões de **Security Hardening**, a interface é desativada em produção (`prod`) para proteção da superfície de ataque da API.

<img width="1898" height="861" alt="print-swagger" src="https://github.com/user-attachments/assets/218216d2-a73e-4ed3-be62-c2d90168a511" />

---

# 🧪 Testes

### Executar os testes do Backend e relatório JaCoCo

```bash
cd api
./gradlew test jacocoTestReport
```

*Caso o Docker esteja rodando, os testes de integração sobem uma instância real do **PostgreSQL 17** através do **Testcontainers**.*

### Executar os testes unitários do Frontend (Angular)

```bash
cd ui
pnpm test --watch=false
```

O relatório visual de cobertura do backend em HTML é gerado em:
`api/build/reports/jacoco/test/html/index.html`

<img width="1191" height="379" alt="print-jacoco" src="https://github.com/user-attachments/assets/95db2ebb-2c17-4c85-9c6a-109e116b2995" />

---

# ⚙️ Integração Contínua (CI)

O **GitHub Actions** (`.github/workflows/ci.yml`) executa automaticamente a cada `push`:

- Build da aplicação em Java 21
- Suíte de testes unitários
- Testes de integração E2E com Testcontainers
- Relatório de Cobertura JaCoCo (disponível para download na aba Actions)

---

# ☁️ Deploy em Nuvem & Infraestrutura de Produção

A arquitetura de produção foi desenhada seguindo os princípios de **infraestrutura Serverless resiliente e escalável**:

- **Google Cloud Run (Serverless Compute)**: Os containers do Backend (Spring Boot) e Frontend (Angular) são executados no **Cloud Run**, uma plataforma totalmente gerenciada que realiza **autoscaling automático de zero a N instâncias sob demanda**, garantindo alta disponibilidade, zero manutenção de servidores e eficiência máxima.
- **Neon PostgreSQL (Serverless Database)**: A persistência de dados de produção utiliza o **Neon PostgreSQL**, um banco de dados relacional gerenciado na nuvem com conexões seguras por SSL (`sslmode=require`) e gerenciamento de schema controlado via **Flyway Migrations**.

| Serviço | Plataforma | URL de Produção |
|---|---|---|
| **Backend API** | Google Cloud Run | https://task-manager-api-726061632300.us-central1.run.app |
| **Frontend SPA** | Google Cloud Run | https://task-manager-ui-726061632300.us-central1.run.app |
