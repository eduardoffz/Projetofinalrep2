# AgriRent - Plataforma de Aluguel de Maquinas Agricolas

Plataforma para conectar proprietarios de maquinas agricolas com agricultores que precisam alugar equipamentos.

## Funcionalidades

- **Explorar Maquinas**: Navegue por maquinas disponiveis para aluguel com filtros por tipo e busca
- **Aluguel**: Solicite locacao com selecao de datas e servicos adicionais
- **Anunciar**: Proprietarios cadastram suas maquinas para aluguel
- **Gestao de Locacoes**: Acompanhe solicitacoes pendentes, ativas e concluidas
- **Servicos Adicionais**: Seguro, entrega, operador e outros servicos
- **Autenticacao**: Login e cadastro com niveis de acesso (CLIENTE, PROPRIETARIO, ADMIN)

## Tecnologias

Java 17, Spring Boot 3.2.5, Maven, Thymeleaf, MySQL, JDBC, JWT, BCrypt

## Estrutura

```
agrirent/
├── pom.xml
├── scripts/init-database.sql
└── src/main/
    ├── java/com/frota/
    │   ├── FrotaApplication.java
    │   ├── config/SecurityConfig.java
    │   ├── controller/      # REST + MVC controllers
    │   ├── filter/JwtAuthFilter.java
    │   ├── model/           # DTOs/Modelos
    │   ├── repository/      # JDBC + Conexao
    │   └── service/         # Auth, Locacao, Proprietario, Token, Telemetria
    └── resources/
        ├── application.properties
        ├── static/css/estilos.css
        └── templates/       # HTML (Thymeleaf)
```

## Como executar

### 1. Criar o banco

Abra o MySQL Workbench:
- **File** > **Open SQL Script**
- Selecione `scripts/init-database.sql`
- Clique em **Execute** (raio)

### 2. Rodar

```bash
mvn spring-boot:run
```

Acesse: http://localhost:8080

## Credenciais de teste

| Papel | Nome | Email | Senha |
|-------|------|-------|-------|
| ADMIN | Administrador Geral | admin@agrirent.com | admin123 |
| PROPRIETARIO | João Silva (Boa Esperança) | joao@agrirent.com | prop123 |
| PROPRIETARIO | Maria Fernandes (Recanto Verde) | maria@agrirent.com | prop123 |
| PROPRIETARIO | Fernando Guimarães (Santa Rita) | fernando@agrirent.com | prop123 |
| PROPRIETARIO | Agropecuária Silva | contato@silvaagro.com.br | prop123 |
| PROPRIETARIO | Beatriz Alcantara (Guarani) | beatriz@fazendaguarani.com | prop123 |
| CLIENTE | Carlos Cliente | carlos@agrirent.com | cli123 |
| CLIENTE | Ana Paula Souza | ana.souza@gmail.com | cli123 |
| CLIENTE | Roberto Mendes (AgroVale) | roberto.mendes@agrovale.com.br | cli123 |
| CLIENTE | Juliana Lima (Sítio Horizonte) | juliana.lima@sitiohorizonte.com | cli123 |
| CLIENTE | Marcos Oliveira (Fazenda Paraíso) | marcos.oliveira@fazendaparaiso.com | cli123 |

## API REST

| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | /api/auth/login | Login |
| POST | /api/auth/cadastrar | Cadastro |
| GET | /api/maquinas | Listar maquinas |
| GET | /api/maquinas/disponiveis | Maquinas disponiveis |
| POST | /api/maquinas | Criar maquina |
| GET | /api/locacoes | Listar locacoes ativas |
| POST | /api/locacoes | Criar locacao |
| POST | /api/locacoes/{id}/aprovar | Aprovar locacao |
| POST | /api/locacoes/{id}/concluir | Concluir locacao |
| POST | /api/locacoes/{id}/cancelar | Cancelar locacao |
| GET | /api/proprietarios | Listar proprietarios |
| GET | /api/servicos | Listar servicos adicionais |
