# Plano de Correção — 6 Erros AgriRent

## Arquivos a modificar

### Código-fonte
| # | Arquivo | Correção |
|---|---|---|
| 2 | `Locacao.java` | `getCodigoPix()` — CRC dinâmico + tamanho de chave dinâmico |
| 3 | `application.properties` | Porta 3307 → 3306 |
| 3 | `Conexao.java` | Porta 3307 → 3306 |
| 4 | `LocacaoController.java` | Adicionar `usuarioId` nos endpoints aprovar/concluir/cancelar |
| 4 | `LocacaoService.java` | Validar propriedade da máquina antes de aprovar/concluir/cancelar |

### Documentação
| # | Arquivo | Correção |
|---|---|---|
| 1 | `DOCUMENTACAO_CODIGO.md` | "Java 17/23" → "Java 17" |
| 5 | `DOCUMENTACAO_CODIGO.md` | Remover acentos do diagrama Mermaid |
| 6 | `DOCUMENTACAO_CODIGO.md` | Adicionar pacote `filter/` (JwtAuthFilter) na seção de arquitetura |
| 3+4 | `DOCUMENTACAO_CODIGO.md` | Corrigir Seção D sobre porta e adicionar nota sobre validação IDOR |
