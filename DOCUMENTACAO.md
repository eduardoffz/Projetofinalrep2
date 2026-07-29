# AgriRent - Documentacao do Projeto

**Sistema de Aluguel de Maquinas Agricolas**

---

## Visao Geral

Plataforma web para conectar proprietarios de maquinas agricolas com agricultores que precisam alugar equipamentos. Construida com Java Spring Boot e Bootstrap 5.

---

## Tecnologias

| Tecnologia | Versao |
|------------|--------|
| Java | 17+ |
| Spring Boot | 3.2.5 |
| Maven | - |
| Bootstrap | 5.3.3 |
| Thymeleaf | - |
| MySQL | - |
| JWT (jjwt) | 0.12.5 |
| BCrypt | - |

---

## Como Rodar

### 1. Banco de Dados
Abra o MySQL Workbench (porta **3337**) e execute:
```sql
-- File > Open SQL Script > scripts/init-database.sql > Execute
```

### 2. Servidor
```bash
cd ~/Desktop/t2a22
JAVA_HOME="/c/Program Files/Java/jdk-23" ./mvnw.cmd spring-boot:run
```

### 3. Acessar
Abra o navegador em: **[http://localhost:8082](http://localhost:8082)**

---

## Credenciais de Teste

| Papel | Email | Senha |
|-------|-------|-------|
| ADMIN | admin@agrirent.com | admin123 |
| PROPRIETARIO | joao@agrirent.com | prop123 |
| PROPRIETARIO | maria@agrirent.com | prop123 |
| CLIENTE | carlos@agrirent.com | cli123 |

---

## Estrutura do Projeto

```
t2a22/
├── pom.xml                          # Dependencias Maven
├── scripts/
│   ├── init-database.sql            # Script de criacao do banco + dados iniciais
│   └── setup.sh                     # Script auxiliar
├── src/main/
│   ├── java/com/frota/
│   │   ├── FrotaApplication.java    # Entry point
│   │   ├── config/
│   │   │   └── SecurityConfig.java  # Seguranca (JWT + BCrypt)
│   │   ├── controller/
│   │   │   ├── AuthController.java          # API REST de autenticacao
│   │   │   ├── LocacaoController.java       # API REST de locacoes
│   │   │   ├── MaquinaController.java       # API REST de maquinas
│   │   │   ├── ProprietarioController.java  # API REST de proprietarios
│   │   │   ├── ServicoAdicionalController.java # API REST de servicos
│   │   │   └── WebController.java           # Rotas MVC (Thymeleaf)
│   │   ├── filter/
│   │   │   └── JwtAuthFilter.java  # Filtro de autenticacao JWT
│   │   ├── model/                  # Classes de modelo (DTOs)
│   │   │   ├── Indisponibilidade.java
│   │   │   ├── Locacao.java
│   │   │   ├── LocacaoRequest.java
│   │   │   ├── LocacaoServico.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── Maquina.java
│   │   │   ├── Proprietario.java
│   │   │   ├── ServicoAdicional.java
│   │   │   ├── Telemetria.java
│   │   │   ├── Usuario.java
│   │   │   └── UsuarioRequest.java
│   │   ├── repository/             # Acesso ao banco (JDBC puro)
│   │   │   ├── Conexao.java        # Conexao com MySQL
│   │   │   ├── IndisponibilidadeRepository.java
│   │   │   ├── LocacaoRepository.java
│   │   │   ├── LocacaoServicoRepository.java
│   │   │   ├── MaquinaRepository.java
│   │   │   ├── ProprietarioRepository.java
│   │   │   ├── ServicoAdicionalRepository.java
│   │   │   ├── TelemetriaRepository.java
│   │   │   └── UsuarioRepository.java
│   │   └── service/                # Logica de negocios
│   │       ├── AuthService.java
│   │       ├── LocacaoService.java
│   │       ├── ProprietarioService.java
│   │       ├── TelemetriaService.java
│   │       └── TokenService.java
│   └── resources/
│       ├── application.properties  # Configuracoes (porta 8082, secret JWT)
│       ├── static/
│       │   ├── css/
│       │   │   └── estilos.css     # Complementos Bootstrap
│       │   └── js/
│       │       └── agrirent.js     # JavaScript do sistema
│       └── templates/              # Paginas HTML (Thymeleaf)
│           ├── fragments/
│           │   ├── alerts.html
│           │   ├── head.html
│           │   └── navbar.html
│           ├── login.html
│           ├── cadastrar.html
│           ├── dashboard.html
│           ├── explorar.html
│           ├── maquina-detalhes.html
│           ├── meus-alugueis.html
│           ├── minhas-locacoes.html
│           ├── anunciar.html
│           ├── servicos.html
│           └── locacao-detalhes.html
├── CHANGELOG-REFORMULACAO.md       # Historico de alteracoes na interface
└── DOCUMENTACAO.md                  # Este arquivo
```

---

## Telas do Sistema

| Tela | URL | Descricao |
|------|-----|-----------|
| Login | `/login` | Autenticacao de usuarios |
| Cadastro | `/cadastrar` | Criacao de conta (cliente ou proprietario) |
| Dashboard | `/` | Visao geral com estatisticas e simulador |
| Explorar | `/explorar` | Busca e filtro de maquinas disponiveis |
| Detalhes | `/maquinas/{id}` | Informacoes da maquina + formulario de locacao |
| Meus Alugueis | `/meus-alugueis` | Locacoes do cliente logado |
| Minhas Locacoes | `/minhas-locacoes` | Locacoes recebidas (proprietario) |
| Anunciar | `/anunciar` | Cadastro de nova maquina |
| Servicos | `/servicos` | Lista de servicos adicionais (admin) |
| Detalhes Locacao | `/locacoes/{id}` | Resumo financeiro da locacao |

---

## Roles de Acesso

| Role | Permissoes |
|------|------------|
| **CLIENTE** | Explorar maquinas, fazer locacoes, ver meus alugueis |
| **PROPRIETARIO** | Tudo do CLIENTE + anunciar maquinas, aprovar/concluir/cancelar locacoes |
| **ADMIN** | Acesso completo a todas as funcionalidades |

---

## API REST

| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/cadastrar` | Cadastro |
| GET | `/api/maquinas` | Listar maquinas |
| GET | `/api/maquinas/disponiveis` | Maquinas disponiveis |
| POST | `/api/maquinas` | Criar maquina |
| GET | `/api/locacoes` | Listar locacoes ativas |
| POST | `/api/locacoes` | Criar locacao |
| POST | `/api/locacoes/{id}/aprovar` | Aprovar locacao |
| POST | `/api/locacoes/{id}/concluir` | Concluir locacao |
| POST | `/api/locacoes/{id}/cancelar` | Cancelar locacao |
| GET | `/api/proprietarios` | Listar proprietarios |
| GET | `/api/servicos` | Listar servicos adicionais |

---

## Banco de Dados

**Nome:** `db_agrirent` (MySQL, porta 3337)

**Tabelas:**
1. **usuarios** — CLIENTE / PROPRIETARIO / ADMIN
2. **proprietarios** — Perfil dos anunciantes (1:1 com usuarios)
3. **maquinas** — Tratores, colheitadeiras, pulverizadores...
4. **locacoes** — Contratos (PENDENTE, ATIVA, CONCLUIDA, CANCELADA, ATRASADA)
5. **servicos_adicionais** — Seguro, entrega, operador...
6. **locacao_servicos** — Vinculo N:N entre locacoes e servicos
7. **telemetria** — Dados de sensores (temperatura, RPM, pressao...)
8. **indisponibilidades** — Bloqueios de calendario

---

## Observacoes

- O frontend usa **Bootstrap 5** via CDN (requer internet para carregar)
- Nao usa JPA/Hibernate — o acesso ao banco e feito com **JDBC puro**
- A porta padrao do servidor e **8082**
- O secret JWT esta configurado em `application.properties` (trocar em producao)
