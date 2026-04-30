# Prática DevOps

Aplicação web em três camadas (frontend, backend e bd) containerizada com Docker, entregue como trabalho da disciplina de DevOps.

## Sobre

O **GameTests** é uma plataforma de apoio a testes de software no contexto de projetos e sessões de teste: gestão de projetos, estratégias (com upload de imagens), sessões para testadores, usuários administradores e testadores, e autenticação via JWT.

## Tecnologias

- **Frontend:** aplicação **Next.js 15** (React 19) gerada como **export estático** (`output: 'export'`), servida por **Nginx**. O mesmo Nginx atua como **reverse proxy**: encaminha `/api/*` para a API Spring Boot e `/uploads/*` para os arquivos servidos pela API (imagens de estratégias, etc.).
- **Backend:** **Spring Boot 3.5**, **Java 21**, **Spring Data JPA** (Hibernate) e **OAuth2 Resource Server** (JWT). O build usa **Maven Wrapper** dentro de um **Dockerfile multi-stage**; a imagem final leva só o JAR e a **JRE 21**.
- **Banco:** **MySQL 8.0**, com imagem derivada de um **Dockerfile** em `db/` (base `mysql:8.0`). O volume **`mysql-data`** persiste os dados; o volume **`uploads`** guarda arquivos enviados pela API.
- **Orquestração:** **Docker Compose v2**, arquivo **`compose.yml`** na raiz do repositório.

## Arquitetura

A decisão principal foi **expor apenas uma porta no host**: a do serviço **`frontend`** (**`localhost:3000`** → porta **80** do Nginx dentro do contêiner). **Backend** e **MySQL** ficam só na rede Docker privada **`app-net`** e **não** publicam portas para a máquina hospedeira.

Fluxo típico:

1. O navegador abre **`http://localhost:3000`** e recebe HTML/JS/CSS estáticos do Nginx.
2. O JavaScript chama a API com URLs **relativas** sob **`/api/...`**. O Nginx repassa internamente para **`http://backend:8080/...`** (o prefixo `/api` é removido no proxy).
3. Recursos em **`/uploads/...`** usam o mesmo host no navegador; o Nginx encaminha para **`http://backend:8080/uploads/...`**.
4. A API conecta ao banco em **`jdbc:mysql://db:3306/...`** — **`db`** é o nome do serviço no Compose, resolvido pelo DNS interno do Docker. O schema MySQL usado continua sendo **`AA2`** (definido por `MYSQL_DATABASE`).

Esse desenho reduz a superfície de ataque, alinha página e API sob o mesmo *origin* no navegador e permite URLs relativas no cliente em produção.

A **ordem de subida** do banco é protegida por **healthcheck** (`mysqladmin ping`); o serviço **`backend`** só inicia após o MySQL ficar saudável (`depends_on` com `condition: service_healthy`).

## Estrutura do repositório

```text
project1-devops/
├── compose.yml
├── README.md
├── db/
│   └── Dockerfile
├── frontend/
│   ├── Dockerfile
│   ├── nginx/
│   │   └── default.conf   → proxy /api e /uploads para o serviço backend
│   └── src/...
└── backend/
    ├── Dockerfile
    ├── pom.xml
    └── src/...
```

## Pré-requisitos

Apenas **Docker Desktop** (ou **Docker Engine** + **Compose v2**). Não é necessário instalar Node, Java, Maven nem MySQL na máquina para **rodar a stack containerizada**.

Para desenvolvimento **fora** do Docker, você pode usar Node e Java localmente conforme as versões do projeto.

## Como rodar

Na raiz do repositório:

```bash
docker compose up --build
```

Esse comando constrói as imagens dos serviços **`db`**, **`backend`** e **`frontend`**, cria a rede **`app-net`** e os volumes, e sobe os três contêineres. Quando os logs estabilizarem (mensagem de *Started* do Spring Boot), abra **`http://localhost:3000`**.

- Parar contêineres: `docker compose down`
- Parar e **apagar volumes**: `docker compose down -v`

## Verificando que está funcionando

`docker compose ps` deve mostrar três serviços. O **`frontend`** deve exibir **`0.0.0.0:3000->80/tcp`**. **`backend`** e **`db`** não devem listar mapeamento de porta para o host — apenas portas internas.

Confirmação rápida:

- Chamada **direta** ao backend no host costuma **falhar** se a porta 8080 não estiver publicada: `curl http://localhost:8080` tende a **Connection refused**.
- A API via **proxy**: por exemplo requisição **`POST`** em **`http://localhost:3000/api/login`** com corpo JSON de login, ou outros endpoints sob **`/api/...`**.
