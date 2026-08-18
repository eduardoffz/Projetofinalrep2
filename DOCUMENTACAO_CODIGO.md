# Documentação Técnica do Sistema — AgriRent

Documentação geral do código-fonte e arquitetura do sistema **AgriRent**, plataforma para locação e gestão de máquinas e equipamentos agrícolas.

---

## 1. Arquitetura e Estrutura do Projeto

O sistema foi desenvolvido utilizando **Java 17** com a infraestrutura do **Spring Boot 3.2.5**. A renderização visual é realizada server-side via **Thymeleaf**, integrada a uma API REST e camada de persistência nativa via **JDBC (PreparedStatement e HikariCP)** com banco **MySQL**.

### Organização de Pacotes (`src/main/java/com/frota/`):

* **`model/`**: Entidades do sistema e objetos de transferência (`Locacao`, `Maquina`, `Usuario`, `Proprietario`, `ServicoAdicional`, `Telemetria`, `Indisponibilidade`, DTOs de login e requisições).
* **`repository/`**: Camada de persistência SQL nativa via `Connection` / `PreparedStatement` (`Conexao.java`, `LocacaoRepository`, `MaquinaRepository`, `UsuarioRepository`, `ProprietarioRepository`, `ServicoAdicionalRepository`, `TelemetriaRepository`, `IndisponibilidadeRepository`).
* **`service/`**: Regras de negócio, cálculo de diárias/caução/serviços, autenticação e emissão de tokens (`LocacaoService`, `AuthService`, `ProprietarioService`, `TokenService`, `TelemetriaService`).
* **`controller/`**: Controladores MVC Thymeleaf (`WebController.java`) e endpoints REST API (`LocacaoController`, `MaquinaController`, `AuthController`, etc.).
* **`filter/` & `config/`**: Filtros de autenticação JWT e segurança do Spring Security (`JwtAuthFilter`, `SecurityConfig`).

---

## 2. Principais Módulos e Funcionalidades

### 👤 Perfil do Cliente e Histórico de Locações
* **Rota Web:** `/clientes/{id}` em `WebController.java`.
* **Funcionalidade:** Permite aos proprietários e administradores visualizar o perfil detalhado de um cliente.
* **Informações Exibidas:**
  * Dados cadastrais (Nome, E-mail, Telefone).
  * Atalho direto para iniciar conversa no **WhatsApp** com o número do cliente (`https://wa.me/55...`).
  * Indicadores de uso: **Total de Solicitações**, **Locações Concluídas** e **Taxa de Conclusão (%)**.
  * Tabela completa com o histórico de locações realizadas pelo cliente (máquina, período de uso, status, valor total e observações).

### 📊 Dashboard e Métricas Financeiras
* **Rota Web:** `/` em `WebController.java`.
* **Funcionalidade:** Painel principal adaptado ao perfil do usuário logado (Cliente, Proprietário ou Admin).
* **Métricas do Proprietário:**
  * Total de máquinas cadastradas.
  * Solicitações pendentes e locações ativas.
  * **Faturamento Acumulado (R$):** Soma automática de todos os aluguéis com status `ATIVA` ou `CONCLUIDA` pertencentes ao proprietário (`LocacaoRepository.calcularFaturamentoProprietario`).

### 💸 Pagamento via PIX Copia e Cola
* **Modelo:** `Locacao.java` (`getCodigoPix()`).
* **Funcionalidade:** Geração dinâmica do payload padrão EMV BR Code (definido pelo Banco Central do Brasil) incluindo cálculo de checksum **CRC-16/CCITT-FALSE**.
* **Visualização:** Nas telas `meus-alugueis.html` e `minhas-locacoes.html`, quando a locação está no status `PENDENTE`, são exibidos a chave PIX do proprietário, as instruções e um botão para cópia instantânea do código PIX.

### 🔍 Filtros Dinâmicos de Status
* **Telas:** `minhas-locacoes.html` e `meus-alugueis.html`.
* **Funcionalidade:** Botões de abas em JavaScript (*Todos*, *Pendentes*, *Ativas*, *Concluídas*, *Canceladas*) que alternam a visibilidade dos cards de locação instantaneamente no navegador.

### 🚜 Gestão de Frota, Serviços e Telemetria
* **Cadastro de Máquinas:** Proprietários registram equipamentos informando marca, modelo, ano, horas de uso, preço da diária e caução.
* **Serviços Adicionais:** Inclusão de seguros, frete/entrega e operador qualificado durante a solicitação do aluguel.
* **Telemetria:** Registro e acompanhamento de horas de uso, temperatura do motor, consumo de combustível, RPM e pressão de óleo com alertas visuais para parâmetros fora do padrão.

---

## 3. Banco de Dados e Conexão

* **Script Inicial:** Localizado em `scripts/init-database.sql`, contendo os comandos `CREATE TABLE` e dados de teste.
* **Conexão:** Gerenciada pela classe `Conexao.java` e configurada em `application.properties` apontando para a base MySQL local (`jdbc:mysql://localhost:3306/db_agrirent`).
