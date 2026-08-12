# Plano de Implementação: Melhorias Visuais & Imagens HD das Máquinas (AgriRent)

Este documento apresenta a proposta de evolução visual para a plataforma **AgriRent**, focando na inclusão de imagens em alta definição (HD) para o catálogo de máquinas agrícolas, aprimoramento dos cards de exploração e widgets visuais de saúde do maquinário.

---

## 📸 1. Galeria de Imagens Geradas para as Máquinas

Abaixo estão as imagens geradas especificamente para substituir os ícones genéricos nos cards e na visualização detalhada dos equipamentos:

### 🚜 Trator Alpha (John Deere TLX-3000)
![Trator Alpha (John Deere)](C:\Users\eduar\.gemini\antigravity-ide\brain\661187f9-7d50-4067-a906-20c75c394d3c\trator_agricola_hd_1786547862038.png)

* **Especificações:** 2022 | 1.250 horas de uso | Diária: R$ 450,00 | Caução: R$ 2.000,00
* **Aplicação no Sistema:** Exibição principal nos destaques do Dashboard e catálogo de Tratores.

---

### 🌾 Colheitadeira Beta (New Holland CH-580)
![Colheitadeira Beta (New Holland)](C:\Users\eduar\.gemini\antigravity-ide\brain\661187f9-7d50-4067-a906-20c75c394d3c\colheitadeira_graos_hd_1786547877691.png)

* **Especificações:** 2023 | 890 horas de uso | Diária: R$ 1.200,00 | Caução: R$ 5.000,00
* **Aplicação no Sistema:** Card em destaque para colheita de grãos (milho, soja, trigo).

---

### 💦 Pulverizador Gamma (Jacto PL-200)
![Pulverizador Gamma (Jacto)](C:\Users\eduar\.gemini\antigravity-ide\brain\661187f9-7d50-4067-a906-20c75c394d3c\pulverizador_barras_hd_1786547892805.png)

* **Especificações:** 2021 | 2.100 horas de uso | Diária: R$ 350,00 | Caução: R$ 1.500,00
* **Aplicação no Sistema:** Card para defensivos e fertilização líquida em lavouras.

---

## 🎨 2. Proposta de Melhorias Visuais nas Telas

### A. Integração de Imagens nos Templates (`explorar.html` e `dashboard.html`)
* **Cards Estilo Marketplace:** Substituir a área em branco/ícone por um header de foto HD com cantos arredondados, sobreposição sutil de gradiente e badge de preço/status fixado no topo da imagem.
* **Fallback Inteligente:** Se a imagem da máquina não for informada no cadastro, o sistema exibirá automaticamente a foto correspondente ao tipo (`TRATOR`, `COLHEITADEIRA`, `PULVERIZADOR`).

### B. Tela de Detalhes da Máquina (`maquina-detalhes.html`)
* **Hero Banner Visual:** Exibição da foto da máquina em largura completa com overlay com o título, localização e valor por dia.
* **Widget de Saúde & Telemetria:** Substituir a tabela seca de telemetria por medidores gráficos (temperatura em manômetro/barra verde-amarela-vermelha e nível de óleo/combustível).

### C. Cadastro / Anúncio de Máquinas (`anunciar.html`)
* **Campo de URL de Imagem / Seletor:** Adicionar campo para inclusão de foto personalizada da máquina e botão de pré-visualização instantânea antes de salvar.

---

## ⚠️ User Review Required

> [!IMPORTANT]
> **Manutenção do Banco de Dados:** O campo `imagem_url` já existe no schema SQL da tabela `maquinas`. As fotos geradas foram salvas em `src/main/resources/static/images/` (`/images/trator_alpha.jpg`, `/images/colheitadeira_beta.jpg`, `/images/pulverizador_gamma.jpg`), permitindo que a aplicação sirva as imagens localmente sem depender de links externos.

---

## 🛠 Proposed Changes

### Componente: Imagens Estáticas & Script SQL

#### [NEW] [/static/images/trator_alpha.jpg](file:///c:/Users/eduar/OneDrive/%C3%81rea%20de%20Trabalho/Projetofinalrep2-versao-clean/src/main/resources/static/images/trator_alpha.jpg)
#### [NEW] [/static/images/colheitadeira_beta.jpg](file:///c:/Users/eduar/OneDrive/%C3%81rea%20de%20Trabalho/Projetofinalrep2-versao-clean/src/main/resources/static/images/colheitadeira_beta.jpg)
#### [NEW] [/static/images/pulverizador_gamma.jpg](file:///c:/Users/eduar/OneDrive/%C3%81rea%20de%20Trabalho/Projetofinalrep2-versao-clean/src/main/resources/static/images/pulverizador_gamma.jpg)

#### [MODIFY] [init-database.sql](file:///c:/Users/eduar/OneDrive/%C3%81rea%20de%20Trabalho/Projetofinalrep2-versao-clean/scripts/init-database.sql)
* Preencher a coluna `imagem_url` dos dados iniciais de exemplo com o caminho das imagens locais (`/images/trator_alpha.jpg`, etc.).

---

### Componente: Views Thymeleaf

#### [MODIFY] [explorar.html](file:///c:/Users/eduar/OneDrive/%C3%81rea%20de%20Trabalho/Projetofinalrep2-versao-clean/src/main/resources/templates/explorar.html)
* Atualizar a marcação do card de máquinas para renderizar a imagem HD com estilo responsivo e badges flutuantes.

#### [MODIFY] [dashboard.html](file:///c:/Users/eduar/OneDrive/%C3%81rea%20de%20Trabalho/Projetofinalrep2-versao-clean/src/main/resources/templates/dashboard.html)
* Renderizar as fotos nos cards da seção "Máquinas em Destaque".

#### [MODIFY] [maquina-detalhes.html](file:///c:/Users/eduar/OneDrive/%C3%81rea%20de%20Trabalho/Projetofinalrep2-versao-clean/src/main/resources/templates/maquina-detalhes.html)
* Incluir o container principal da foto em alta definição e os indicadores visuais de telemetria.

---

## 🧪 Verification Plan

### Manual Verification
1. Abrir `/explorar` e verificar se os tratores, colheitadeiras e pulverizadores aparecem com fotos HD nítidas.
2. Clicar em uma máquina para acessar `/maquinas/{id}` e checar a exibição da foto e dos novos widgets de especificações.
