# Mudanças Implementadas - Sistema de Pagamento PIX (AgriRent)

Este documento detalha as atualizações realizadas no sistema AgriRent para suporte a avisos de pagamento via PIX e geração de códigos PIX copia e cola.

---

## 1. Visão Geral das Funcionalidades

Após o cliente solicitar a locação de uma máquina agrícola, a solicitação entra no status **PENDENTE**. Para agilizar e dar transparência ao processo de pagamento antes da aprovação final pelo proprietário:

- **Visão do Cliente (`/meus-alugueis`)**:
  - Exibe o aviso destacado de **"Aguardando Pagamento via PIX"**.
  - Apresenta os dados do proprietário (Nome, E-mail/Contato e Chave PIX).
  - Exibe o valor total a ser pago.
  - Disponibiliza o campo **Código PIX Copia e Cola** com botão interativo para cópia rápida.

- **Visão do Proprietário (`/minhas-locacoes`)**:
  - Exibe o aviso de **"Aguardando Pagamento via PIX do Cliente"**.
  - Mostra a Chave PIX cadastrada do proprietário para conferência.
  - Disponibiliza o código PIX copia e cola referente à locação.
  - Orienta o proprietário a clicar em **"Aprovar Locação"** assim que identificar o recebimento do PIX em sua conta.

---

## 2. Alterações Técnicas

### Model (`Locacao.java`)
- Adicionados os campos de proprietário: `proprietarioNome`, `proprietarioEmail`, `proprietarioTelefone` e `proprietarioChavePix`.
- Método `getChavePixValida()`: retorna a chave PIX ou o e-mail cadastrado como fallback.
- Método `getCodigoPix()`: gera a string formatada no padrão PIX (Copia e Cola) contendo a chave, o nome do proprietário e o valor total formatado.

### Repository (`LocacaoRepository.java`)
- Atualizadas as consultas SQL com `JOIN` nas tabelas `proprietarios` e `usuarios` (proprietário) para carregar os dados do favorecido automaticamente em todas as buscas de locação.

### Views (Thymeleaf HTMLs)
- `minhas-locacoes.html`: Adicionado card de aviso e gerenciamento de pagamento PIX para o proprietário.
- `meus-alugueis.html`: Adicionado card de instrução de pagamento PIX e botão de cópia do código para o cliente.

### Ajustes Gerais de Conexão
- Atualizada a URL de conexão com o banco MySQL (`localhost:3307/db_agrirent`) no `application.properties` e no `Conexao.java`.

---

## 3. Credenciais de Teste

| Perfil | E-mail | Senha |
|--------|--------|-------|
| ADMIN | `admin@agrirent.com` | `admin123` |
| PROPRIETÁRIO | `joao@agrirent.com` | `prop123` |
| CLIENTE | `carlos@agrirent.com` | `cli123` |
