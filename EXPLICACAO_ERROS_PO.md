# 📄 Documento Técnico para o Product Owner (PO): Análise de Erros Cruciais

**Sistema:** AgriRent — Plataforma de Locação de Máquinas Agrícolas  
**Autor:** Especialista em Engenharia e Arquitetura de Software  
**Destinatário:** Product Owner (PO) e Equipe de Desenvolvimento  
**Data:** 7 de Agosto de 2026  

---

## 📋 Contexto Executivo

Atendendo à solicitação do Product Owner (PO), este documento apresenta a análise técnica detalhada de **3 erros cruciais** identificados na arquitetura, segurança e execução da plataforma **AgriRent**. 

Estes problemas impactam diretamente a **disponibilidade do sistema em diferentes ambientes**, a **segurança dos recursos** e a **integridade transacional do fluxo principal de locação de máquinas agrícolas**.

---

# 🔴 ERRO 1: Gerenciamento Paralelo e Manual de Conexões JDBC (`Conexao.java`)

### 1. Qual é o erro?
A aplicação tenta conectar ao banco de dados utilizando configurações de rede e credenciais **fixas no código-fonte (*hardcoded*)** em uma classe estática manual, ignorando a configuração centralizada do Spring Boot. 

**Sintomas na prática:**
* Em ambientes onde o MySQL roda na porta padrão `3306` ou exige senha, a aplicação lança exceções do tipo:  
  `java.sql.SQLException: Communications link failure` ou `Access denied for user 'root'@'localhost'`.
* A aplicação cria **dois pools de conexões HikariCP simultâneos**, gerando vazamento de memória e conexões presas no banco de dados.

---

### 2. Por que ele ocorre/ocorreu?
O ecossistema Spring Boot gerencia o ciclo de vida do banco através de seu próprio `DataSource` configurado no `application.properties`. No entanto, na camada de repositórios do sistema:

1. Foi criada a classe estática `com.frota.repository.Conexao` com os dados fixos:
   * **URL:** `jdbc:mysql://localhost:3307/db_agrirent`
   * **Usuário:** `root`
   * **Senha:** `""`
2. Todas as classes de repositório (`LocacaoRepository`, `MaquinaRepository`, `UsuarioRepository`, etc.) chamam diretamente `Conexao.getConnection()`.
3. Com isso, as variáveis de ambiente do Spring Boot e do `application.properties` são completamente ignoradas durante a execução das consultas SQL da aplicação.

---

### 3. Como resolvê-lo se baseando no sistema?

#### **A. Separação por Camadas (Persistência / Spring Data)**
1. **Remover** a classe estática `Conexao.java`.
2. **Injetar o `DataSource` ou `JdbcTemplate`** gerenciado pelo Spring Boot em todos os repositórios através de Injeção de Dependência por Construtor.

#### **B. Refatoração de Código (Exemplo em `LocacaoRepository.java`)**

**Antes (Errado):**
```java
// Conexão manual e estática
try (Connection c = Conexao.getConnection(); 
     PreparedStatement p = c.prepareStatement(sql)) {
    // ...
}
```

**Depois (Correto / Refatorado):**
```java
@Repository
public class LocacaoRepository {

    private final JdbcTemplate jdbcTemplate;

    // Injeção de Dependência pelo Spring
    public LocacaoRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Locacao> listarTodos() {
        String sql = BASE_SELECT + "ORDER BY l.created_at DESC";
        return jdbcTemplate.query(sql, this::mapearRow);
    }
}
```

#### **C. Centralização de Configuração no `application.properties`**
```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3307/db_agrirent?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASS:}
```

---

# 🔴 ERRO 2: Concorrência e *Overbooking* na Reserva de Máquinas

### 1. Qual é o erro?
Dois clientes diferentes conseguem reservar a **mesma máquina agrícola para o mesmo período de datas**, gerando choque de agenda (*Overbooking* / Dupla Locação).

**Sintomas na prática:**
* O produtor rural A e o produtor rural B solicitam o mesmo trator para a mesma semana. O sistema aceita ambas as solicitações, criando contratos concorrentes e gerando falha operacional e prejuízo financeiro.

---

### 2. Por que ele ocorre/ocorreu?
Na camada de serviços de negócio (`com.frota.service.LocacaoService`), o fluxo de criação da locação é executado em duas etapas sem isolamento transacional:

```java
// Etapa 1: Consulta conflitos
if (locacaoRepo.existeConflitoDatas(maq.getId(), dto.getDataInicio(), dto.getDataFim()))
    throw new RuntimeException("Ja existe uma locacao neste periodo");

// Etapa 2: Cria a nova locação
Locacao criada = locacaoRepo.criar(loc);
```

1. **Condição de Corrida (*Race Condition*):** Não existe a anotação `@Transactional` no método e nem bloqueio de linha no MySQL (`SELECT ... FOR UPDATE`).
2. Se duas requisições chegarem ao mesmo tempo (no mesmo milissegundo):
   * Ambas as requisições executam a consulta da Etapa 1 ao mesmo tempo.
   * Ambas recebem a resposta de que o período está livre (`false`).
   * Ambas avançam para a Etapa 2 e gravam a reserva no banco de dados.
3. Além disso, a máquina só tem seu atributo `disponivel` alterado para `false` no momento da aprovação do pagamento, deixando a janela aberta para múltiplas solicitações pendentes.

---

### 3. Como resolvê-lo se baseando no sistema?

#### **A. Adição de Isolamento Transacional e Trava Pessimista (Pessimistic Lock)**
1. Adicionar a anotação `@Transactional` no método `criarLocacao` em `LocacaoService.java`.
2. Implementar a busca da máquina utilizando a cláusula `FOR UPDATE` no SQL. Isso faz com que o MySQL "trave" a linha da máquina para outras transações até que a primeira termine.

#### **B. Refatoração no `LocacaoService.java`**

```java
@Service
public class LocacaoService {

    private final LocacaoRepository locacaoRepo;
    private final MaquinaRepository maquinaRepo;

    public LocacaoService(LocacaoRepository lr, MaquinaRepository mr, ...) {
        this.locacaoRepo = lr;
        this.maquinaRepo = mr;
    }

    @Transactional // Garante atomicidade da transação
    public Locacao criarLocacao(LocacaoRequest dto, Long clienteId) {
        // Busca a máquina aplicando trava pessimista no banco (SELECT ... FOR UPDATE)
        Maquina maq = maquinaRepo.buscarPorIdComLock(dto.getMaquinaId())
                .orElseThrow(() -> new RuntimeException("Máquina não encontrada"));

        // Com a máquina travada, valida a existência de conflitos com segurança
        if (locacaoRepo.existeConflitoDatas(maq.getId(), dto.getDataInicio(), dto.getDataFim())) {
            throw new RuntimeException("Já existe uma locação no período solicitado");
        }

        // Realiza o registro seguro
        return locacaoRepo.criar(loc);
    }
}
```

---

# 🔴 ERRO 3: Falha de Autorização e Validação de Propriedade na Aprovação de Locações (IDOR)

### 1. Qual é o erro?
No fluxo principal de locação (**Solicitar Locação ➡️ Efetuar PIX ➡️ Aprovar Locação ➡️ Concluir Locação**), qualquer usuário autenticado no sistema (inclusive um cliente comum) consegue **aprovar, cancelar ou concluir locações de terceiros**.

**Sintoma na prática:**
Basta qualquer usuário enviar uma requisição HTTP POST para `/api/locacoes/{id}/aprovar` ou `/api/locacoes/{id}/cancelar` alterando o `{id}`, e a locação de outro proprietário tem seu status alterado no sistema sem a devida confirmação de quem é o proprietário real da máquina.

---

### 2. Por que ele ocorre/ocorreu?
Nos arquivos `LocacaoController.java` e `LocacaoService.java`:

```java
// Em LocacaoController.java (Linha 30)
@PostMapping("/{id}/aprovar")
public ResponseEntity<Void> aprovar(@PathVariable Long id) {
    service.aprovarLocacao(id); // NÃO passa nem valida quem é o usuário logado!
    return ResponseEntity.ok().build();
}

// Em LocacaoService.java (Linha 83)
public void aprovarLocacao(Long locacaoId) {
    Locacao loc = locacaoRepo.buscarPorId(locacaoId)...;
    // NÃO valida se a máquina pertence ao proprietário logado!
    locacaoRepo.atualizarStatus(locacaoId, "ATIVA");
}
```

* **Vulnerabilidade IDOR (*Insecure Direct Object Reference*):** O endpoint não valida se o usuário autenticado que está chamando a ação de aprovação é o dono real da máquina envolvida na locação (`locacao.getProprietarioId()`).

---

### 3. Como resolvê-lo se baseando no sistema?

#### **A. Repassar o Usuário Autenticado no Controller**
Capturar o ID do usuário logado através do token JWT no controller:
```java
@PostMapping("/{id}/aprovar")
public ResponseEntity<Void> aprovar(@PathVariable Long id, @RequestAttribute Long usuarioId) {
    service.aprovarLocacao(id, usuarioId);
    return ResponseEntity.ok().build();
}
```

#### **B. Validar a Propriedade da Máquina na Camada de Serviço**
No `LocacaoService.java`, verificar se o usuário logado possui perfil de proprietário e se é dono da máquina:
```java
public void aprovarLocacao(Long locacaoId, Long usuarioIdLogado) {
    Locacao loc = locacaoRepo.buscarPorId(locacaoId)
            .orElseThrow(() -> new RuntimeException("Locação não encontrada"));

    // Busca o perfil de proprietário do usuário logado
    Proprietario prop = proprietarioRepo.buscarPorUsuarioId(usuarioIdLogado)
            .orElseThrow(() -> new RuntimeException("Usuário não é um proprietário cadastrado"));

    // Valida se a máquina da locação pertence a este proprietário
    Maquina maq = maquinaRepo.buscarPorId(loc.getMaquinaId()).get();
    if (!maq.getProprietarioId().equals(prop.getId())) {
        throw new RuntimeException("Acesso negado: Você não é o proprietário desta máquina");
    }

    // Executa a transição de status com segurança
    locacaoRepo.atualizarStatus(locacaoId, "ATIVA");
    maquinaRepo.atualizarDisponibilidade(loc.getMaquinaId(), false);
}
```

---

## 📊 Quadro Comparativo Sintético

| Detalhe / Métrica | Erro 1: Conexão JDBC Hardcoded | Erro 2: Concorrência de Datas | Erro 3: Autorização (IDOR) |
|---|---|---|---|
| **Camada Afetada** | Persistência (`repository`) | Regra de Negócio (`service`) | Controle / Segurança (`controller` / `service`) |
| **Severidade** | 🔴 Alta (Bloqueio de Implantação) | 🔴 Alta (Integridade de Negócio) | 🔴 Alta (Segurança / Quebra de Controle) |
| **Solução Principal** | Usar Spring `JdbcTemplate` & Injeção | Usar `@Transactional` & Trava `FOR UPDATE` | Checagem de ID do Usuário Logado vs Proprietário |
| **Impacto do Ajuste** | Portabilidade entre Ambientes | Fim do *Overbooking* | Garantia de Privacidade e Autorização |

---

## 🎯 Conclusão e Próximos Passos Recomendados

1. **Ação Imediata 1 (Infra/Persistência):** Substituir o uso de `Conexao.java` pela injeção do `JdbcTemplate` do Spring Boot.
2. **Ação Imediata 2 (Negócio/Atomicidade):** Aplicar `@Transactional` e controle de concorrência no `LocacaoService`.
3. **Ação Imediata 3 (Segurança/Autorização):** Injetar a validação de propriedade do usuário logado em todos os endpoints de manipulação de locação.
