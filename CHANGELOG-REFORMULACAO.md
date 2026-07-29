# Changelog - Reformulacao da Interface AgriRent

## Data: 2026-06-26

---

### O que foi feito

#### 1. Correcao de bugs no backend (impediam compilacao)
- **WebController.java**: Removido codigo quebrado nos metodos `meusAlugueis()` (linhas 233-236) e `minhasLocacoes()` (linhas 249-254) — strings soltas e variavel redeclarada
- **TelemetriaRepository.java**: Removidos 2 metodos mortos com sintaxe invalida (`ListarMAquinasDisponiveis` e `listarPorFerramenta` — variavel chamada `1`, SQL incompleto)

#### 2. Migracao para Bootstrap 5 (todas as telas exceto login e cadastro)
- Substituido CSS customizado pesado (~800 linhas) por Bootstrap 5 CDN + ~250 linhas de complementos
- **Navbar**: Bootstrap responsiva com collapse mobile, sem emojis
- **Dashboard**: Stats cards, simulador de diaria, grid de maquinas em destaque
- **Explorar**: Filtro por tipo/busca + filtro em tempo real com JS
- **Detalhes da Maquina**: Layout 2 colunas (info + sidebar precos)
- **Alugueis / Locacoes**: Cards de contratos com status e acoes
- **Anunciar**: Formulario responsivo com grid Bootstrap
- **Servicos**: Cards de servicos adicionais
- **Alertas**: Bootstrap alerts dismissiveis

#### 3. JS enxuto (`agrirent.js`)
- Removido: modo escuro, animacao de contadores, funcao `alternarTema()`
- Mantido: saudacao dinamica, validacao de senha, calculo de total, filtro tempo real, active link

#### 4. Refinamento de login, cadastro e dashboard
- **Login**: Fundo gradiente verde refinado, card centralizado com sombra, inputs com foco verde, credenciais de teste em box verde claro
- **Cadastro**: Mesmo padrao do login, campos de proprietario com collapse, validacao de senha
- **Dashboard**: Stats cards com blocos de cor gradiente, simulador com badge, cards de destaque refinados, secao header com barra verde

---

### Arquivos modificados (15)

| Arquivo | Tipo |
|---------|------|
| `src/main/resources/static/css/estilos.css` | CSS substituido |
| `src/main/resources/static/js/agrirent.js` | JS reescrito |
| `src/main/resources/templates/fragments/head.html` | Bootstrap CDN |
| `src/main/resources/templates/fragments/navbar.html` | Bootstrap sem emojis |
| `src/main/resources/templates/fragments/alerts.html` | Alertas dismissiveis |
| `src/main/resources/templates/login.html` | Refinado |
| `src/main/resources/templates/cadastrar.html` | Refinado |
| `src/main/resources/templates/dashboard.html` | Refinado |
| `src/main/resources/templates/explorar.html` | Bootstrap |
| `src/main/resources/templates/maquina-detalhes.html` | Bootstrap |
| `src/main/resources/templates/meus-alugueis.html` | Bootstrap |
| `src/main/resources/templates/minhas-locacoes.html` | Bootstrap |
| `src/main/resources/templates/anunciar.html` | Bootstrap |
| `src/main/resources/templates/servicos.html` | Bootstrap |
| `src/main/resources/templates/locacao-detalhes.html` | Bootstrap |
| `src/main/java/com/frota/controller/WebController.java` | Bugfix |
| `src/main/java/com/frota/repository/TelemetriaRepository.java` | Bugfix |

---

### Status
- Projeto compila sem erros
- Servidor roda na porta 8082
- Banco MySQL na porta 3337 (`db_agrirent`)

### Melhorias pendentes (nao implementadas)
Aguardando decisao do usuario:
- Modal de confirmacao antes de aprovar/recusar locacao
- Toast notifications em vez de alertas no topo
- Graficos Chart.js no dashboard
- Loading states nos botoes de submit
- Possivel migracao para React (reescrita completa do frontend SPA)

### Como rodar
```bash
cd ~/Desktop/t2a22
# 1. Executar scripts/init-database.sql no MySQL (porta 3337)
# 2. Subir o servidor:
JAVA_HOME="/c/Program Files/Java/jdk-23" ./mvnw.cmd spring-boot:run
# 3. Acessar: http://localhost:8082
```

### Credenciais de teste
| Papel | Email | Senha |
|-------|-------|-------|
| ADMIN | admin@agrirent.com | admin123 |
| PROPRIETARIO | joao@agrirent.com | prop123 |
| PROPRIETARIO | maria@agrirent.com | prop123 |
| CLIENTE | carlos@agrirent.com | cli123 |
