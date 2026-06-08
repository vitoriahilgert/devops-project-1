# Prática DevOps

Aplicação web em três camadas (frontend, backend e bd) containerizada com Docker, entregue como trabalho da disciplina de DevOps.

## Sobre

O **GameTests** é uma plataforma de apoio a testes de software no contexto de projetos e sessões de teste: gestão de projetos, estratégias (com upload de imagens), sessões para testadores, usuários administradores e testadores, e autenticação via JWT.

## Dados para logar na plataforma
### Para entrar como administrador:

**E-mail:** vihilgerttomasel@gmail.com

**Senha:** password

### Para entrar como usuário normal:

**E-mail:** nandaq2003@gmail.com

**Senha:** password

Em relação à funcionalidades: o administrador tem como função criar usuários, estratégias de testes de jogos, projetos de teste. Os testadores criam sessões de teste referentes aos projetos em que estão incluídos.

## Tecnologias

- **Frontend:** aplicação **Next.js** (React) gerada como **export estático** (`output: 'export'`), servida por **Nginx**. O mesmo Nginx atua como **reverse proxy**: encaminha `/api/*` para a API Spring Boot e `/uploads/*` para os arquivos servidos pela API.
- **Backend:** **Spring Boot** com **Java 21**, utilizando **Spring Data JPA** e **OAuth2 Resource Server** (JWT). O build usa **Maven Wrapper**.
- **Banco:** **MySQL**, com imagem derivada de um **Dockerfile** em `db/` (base `mysql:8.0`). O volume **`mysql-data`** persiste os dados; o volume **`uploads`** guarda arquivos enviados pela API.
- **Orquestração:** **Docker Compose v2**, arquivo **`compose.yml`** na raiz do repositório.

## Arquitetura

Apenas a porta do serviço **`frontend`** (**`localhost:3000`** -> porta 80 do Nginx dentro do contêiner) é exposta no host. **Backend** e **MySQL** estão apenas na rede Docker privada **`app-net`** e **não** publicam portas para a máquina hospedeira.

<img width="581" height="200" alt="Screenshot 2026-06-08 at 00 17 06" src="https://github.com/user-attachments/assets/1f0b32d5-d2e4-403b-a03c-14f0dd236d7c" />


Fluxo:

1. O navegador abre **`http://localhost:3000`** e recebe HTML/JS/CSS estáticos do Nginx.
2. O JavaScript chama a API com URLs **relativas** sob **`/api/...`**. O Nginx repassa internamente para **`http://backend:8080/...`**.
3. Recursos em **`/uploads/...`** usam o mesmo host no navegador; o Nginx encaminha para **`http://backend:8080/uploads/...`**.
4. A API conecta ao banco em **`jdbc:mysql://db:3306/...`**. O schema MySQL usado continua sendo **`AA2`** (definido por `MYSQL_DATABASE`).

Esse modelo reduz a superfície de ataque, alinha página e API sob o mesmo *origin* no navegador e permite URLs relativas no cliente em produção.

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

## Como rodar

Na raiz do repositório:

```bash
docker compose up --build
```

Esse comando constrói as imagens dos serviços **`db`**, **`backend`** e **`frontend`**, cria a rede **`app-net`** e os volumes, e sobe os três contêineres. Quando os logs estabilizarem (mensagem de *Started* do Spring Boot), abra **`http://localhost:3000`**.

- Para parar contêineres: `docker compose down`
- Para parar e **apagar volumes**: `docker compose down -v`

## Verificando que está funcionando

`docker compose ps` deve mostrar três serviços. O **`frontend`** deve exibir **`0.0.0.0:3000->80/tcp`**. **`backend`** e **`db`** não devem listar mapeamento de porta para o host — apenas portas internas.

