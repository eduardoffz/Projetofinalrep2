-- ============================================================
-- AGRIRENT - Plataforma de Aluguel de Maquinas Agricolas
-- Script de criacao do banco de dados e Carga de Dados Inicial
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_agrirent;
USE db_agrirent;

-- -----------------------------------------------------------
-- 1. Tabela de usuarios do sistema
-- Roles: CLIENTE, PROPRIETARIO, ADMIN
-- Senhas padrão:
--   admin123  -> $2a$10$QidVv0Xh5dcEJ1vxVAo7Zuu.5jZqtDQGa/Ogw4RtZc5lAgPzcqadC
--   prop123   -> $2a$10$gx6OoLatb.55obf6ZMHOOOhYW.UvDq4blxo3V.xFIn1YPnusGR53q
--   cli123    -> $2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    telefone VARCHAR(20),
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------------------------------------
-- 2. Tabela de proprietarios (anunciantes)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS proprietarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    documento VARCHAR(20) NOT NULL,
    chave_pix VARCHAR(100),
    endereco VARCHAR(300),
    rating DOUBLE DEFAULT 5.0,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------
-- 3. Tabela de maquinas agricolas disponiveis para aluguel
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS maquinas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proprietario_id BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    fabricante VARCHAR(100) NOT NULL,
    ano_fabricacao INT,
    tipo VARCHAR(30) NOT NULL,
    horas_uso_totais DOUBLE DEFAULT 0,
    preco_diaria DECIMAL(10,2) NOT NULL DEFAULT 0,
    caucao DECIMAL(10,2) DEFAULT 0,
    localizacao VARCHAR(200),
    disponivel BOOLEAN DEFAULT TRUE,
    descricao TEXT,
    imagem_url VARCHAR(500),
    ultima_telemetria DATETIME,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (proprietario_id) REFERENCES proprietarios(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------
-- 4. Tabela de servicos adicionais (seguro, entrega, limpeza, operador)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS servicos_adicionais (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(300),
    preco DECIMAL(10,2) DEFAULT 0,
    tipo VARCHAR(30) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

-- -----------------------------------------------------------
-- 5. Tabela de locacoes (contratos de aluguel)
-- Status: PENDENTE, ATIVA, CONCLUIDA, CANCELADA, ATRASADA
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS locacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    maquina_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    data_devolucao DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    valor_diaria DECIMAL(10,2) NOT NULL,
    valor_caucao DECIMAL(10,2) DEFAULT 0,
    valor_total DECIMAL(10,2) NOT NULL,
    observacoes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (maquina_id) REFERENCES maquinas(id) ON DELETE CASCADE,
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------
-- 6. Tabela de vinculo locacao x servicos
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS locacao_servicos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    locacao_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    preco_cobrado DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (locacao_id) REFERENCES locacoes(id) ON DELETE CASCADE,
    FOREIGN KEY (servico_id) REFERENCES servicos_adicionais(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------
-- 7. Tabela de telemetria (dados dos sensores durante locacao)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS telemetria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    maquina_id BIGINT NOT NULL,
    locacao_id BIGINT,
    horas_uso DOUBLE NOT NULL,
    temperatura_motor DOUBLE NOT NULL,
    consumo_combustivel DOUBLE NOT NULL,
    rpm_motor DOUBLE NOT NULL,
    pressao_oleo DOUBLE NOT NULL,
    data_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (maquina_id) REFERENCES maquinas(id) ON DELETE CASCADE,
    FOREIGN KEY (locacao_id) REFERENCES locacoes(id) ON DELETE SET NULL
);

-- -----------------------------------------------------------
-- 8. Tabela de indisponibilidades (bloqueios de calendario)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS indisponibilidades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    maquina_id BIGINT NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    motivo VARCHAR(200),
    FOREIGN KEY (maquina_id) REFERENCES maquinas(id) ON DELETE CASCADE
);

-- ============================================================
-- DADOS INICIAIS (INSERTS)
-- ============================================================

-- -----------------------------------------------------------
-- A. USUARIOS
-- -----------------------------------------------------------
-- Administrador (senha: admin123)
INSERT INTO usuarios (id, nome, email, senha, role, telefone) VALUES
(1, 'Administrador Geral', 'admin@agrirent.com', '$2a$10$QidVv0Xh5dcEJ1vxVAo7Zuu.5jZqtDQGa/Ogw4RtZc5lAgPzcqadC', 'ADMIN', '(11) 97777-0001');

-- Proprietários (senha: prop123)
INSERT INTO usuarios (id, nome, email, senha, role, telefone) VALUES
(2, 'João Silva (Fazenda Boa Esperança)', 'joao@agrirent.com', '$2a$10$gx6OoLatb.55obf6ZMHOOOhYW.UvDq4blxo3V.xFIn1YPnusGR53q', 'PROPRIETARIO', '(67) 99999-1111'),
(3, 'Maria Fernandes (Recanto Verde)', 'maria@agrirent.com', '$2a$10$gx6OoLatb.55obf6ZMHOOOhYW.UvDq4blxo3V.xFIn1YPnusGR53q', 'PROPRIETARIO', '(44) 99999-2222'),
(4, 'Fernando Guimarães (Agro Santa Rita)', 'fernando@agrirent.com', '$2a$10$gx6OoLatb.55obf6ZMHOOOhYW.UvDq4blxo3V.xFIn1YPnusGR53q', 'PROPRIETARIO', '(65) 99876-4444'),
(5, 'Agropecuária Silva & Filhos', 'contato@silvaagro.com.br', '$2a$10$gx6OoLatb.55obf6ZMHOOOhYW.UvDq4blxo3V.xFIn1YPnusGR53q', 'PROPRIETARIO', '(66) 99654-5555'),
(6, 'Beatriz Alcantara (Fazenda Guarani)', 'beatriz@fazendaguarani.com', '$2a$10$gx6OoLatb.55obf6ZMHOOOhYW.UvDq4blxo3V.xFIn1YPnusGR53q', 'PROPRIETARIO', '(62) 99123-6666');

-- Clientes / Produtores Rurais (senha: cli123)
INSERT INTO usuarios (id, nome, email, senha, role, telefone) VALUES
(7, 'Carlos Cliente', 'carlos@agrirent.com', '$2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6', 'CLIENTE', '(11) 98888-3333'),
(8, 'Ana Paula Souza', 'ana.souza@gmail.com', '$2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6', 'CLIENTE', '(19) 98765-1234'),
(9, 'Roberto Mendes (AgroVale)', 'roberto.mendes@agrovale.com.br', '$2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6', 'CLIENTE', '(45) 99888-2345'),
(10, 'Juliana Lima (Sítio Horizonte)', 'juliana.lima@sitiohorizonte.com', '$2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6', 'CLIENTE', '(34) 99111-3456'),
(11, 'Marcos Oliveira (Fazenda Paraíso)', 'marcos.oliveira@fazendaparaiso.com', '$2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6', 'CLIENTE', '(64) 99222-4567'),
(12, 'Camila Rocha (Agro Rocha)', 'camila.rocha@agrorocha.com.br', '$2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6', 'CLIENTE', '(51) 98444-5678'),
(13, 'Rafael Santos (Campo Aberto)', 'rafael.santos@campoaberto.com', '$2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6', 'CLIENTE', '(43) 99333-6789');

-- -----------------------------------------------------------
-- B. PROPRIETARIOS
-- -----------------------------------------------------------
INSERT INTO proprietarios (id, usuario_id, documento, chave_pix, endereco, rating) VALUES
(1, 2, '111.222.333-44', 'joao@agrirent.com', 'Fazenda Boa Esperança, Rodovia BR-163, km 45 - Dourados/MS', 4.9),
(2, 3, '222.333.444-55', 'maria@agrirent.com', 'Sítio Recanto Verde, Estrada Municipal 12 - Maringá/PR', 4.8),
(3, 4, '333.444.555-66', '65998764444', 'Fazenda Santa Rita, MT-242, km 12 - Sorriso/MT', 5.0),
(4, 5, '12.345.678/0001-90', 'financeiro@silvaagro.com.br', 'Agropecuária Silva, Av. dos Imigrantes, 1500 - Rondonópolis/MT', 4.7),
(5, 6, '555.666.777-88', 'beatriz@fazendaguarani.com', 'Fazenda Guarani, GO-060, km 80 - Rio Verde/GO', 4.9);

-- -----------------------------------------------------------
-- C. SERVICOS ADICIONAIS
-- -----------------------------------------------------------
INSERT INTO servicos_adicionais (id, nome, descricao, preco, tipo, ativo) VALUES
(1, 'Seguro Contra Danos', 'Cobertura completa contra danos mecânicos acidentais durante o período de locação.', 50.00, 'SEGURO', TRUE),
(2, 'Entrega e Retirada no Local', 'Transporte seguro e pontual da máquina até a propriedade do cliente via caminhão prancha.', 200.00, 'ENTREGA', TRUE),
(3, 'Seguro Roubo e Furto', 'Cobertura securitária total contra roubo, furto qualificado e intempéries climáticas.', 35.00, 'SEGURO', TRUE),
(4, 'Operador Qualificado em Campo', 'Operador experiente e certificado pelo SENAR para operação de alta performance da máquina.', 180.00, 'MANUTENCAO', TRUE),
(5, 'Manutenção Preventiva & Calibração RTK', 'Calibração fina dos sistemas de GPS, piloto automático e revisão completa de filtros.', 90.00, 'MANUTENCAO', TRUE),
(6, 'Tanque Cheio na Entrega (Diesel S10)', 'Máquina entregue com tanque 100% abastecido com Diesel S10 filtrado de alta qualidade.', 350.00, 'ENTREGA', TRUE),
(7, 'Lavagem Técnica & Descontaminação Pós-Uso', 'Higienização completa, remoção de resíduos e descontaminação de defensivos após o uso.', 120.00, 'MANUTENCAO', TRUE),
(8, 'Assistência Técnica 24h Especializada', 'Plantão mecânico e técnico 24 horas por dia com atendimento prioritário no talhão.', 75.00, 'SEGURO', TRUE);

-- -----------------------------------------------------------
-- D. MAQUINAS AGRICOLAS
-- -----------------------------------------------------------
INSERT INTO maquinas (id, proprietario_id, nome, modelo, fabricante, ano_fabricacao, tipo, horas_uso_totais, preco_diaria, caucao, localizacao, disponivel, descricao) VALUES
(1, 1, 'Trator Alpha TLX-3000', 'TLX-3000', 'John Deere', 2022, 'TRATOR', 1250.5, 450.00, 2000.00, 'Dourados - MS', TRUE,
 'Trator agrícola potente para preparo de solo, plantio e tratos culturais. Equipado com GPS integrado, piloto automático StarFire e cabine pressurizada com ar-condicionado. Ideal para médias e grandes propriedades.'),

(2, 1, 'Pulverizador Gamma PL-200', 'PL-200', 'Jacto', 2021, 'PULVERIZADOR', 2100.0, 350.00, 1500.00, 'Dourados - MS', TRUE,
 'Pulverizador de barras com 2000L de capacidade, ideal para aplicação homogênea de defensivos e fertilizantes líquidos. Equipado com pontas cerâmicas anti-deriva e corte de seção automático.'),

(3, 2, 'Colheitadeira Beta CH-580', 'CH-580', 'New Holland', 2023, 'COLHEITADEIRA', 890.0, 1200.00, 5000.00, 'Maringá - PR', TRUE,
 'Colheitadeira de grãos com tecnologia de perdas mínimas. Capacidade do tanque graneleiro: 6000L. Ideal para soja, milho e trigo. Cabine com comandos eletro-hidráulicos e baixa taxa de horas trabalhadas.'),

(4, 3, 'Trator John Deere 8R 370', '8R 370', 'John Deere', 2023, 'TRATOR', 620.0, 850.00, 4000.00, 'Sorriso - MT', TRUE,
 'Trator de alta potência com 370 cv nominais, tração dianteira mecânica (TDM) suspensa e transmissão e23 inteligente. Perfeito para gradagens pesadas, subsolagem profunda e plantio de alta velocidade.'),

(5, 3, 'Colheitadeira Case IH Axial-Flow 8250', 'Axial-Flow 8250', 'Case IH', 2022, 'COLHEITADEIRA', 1150.0, 1600.00, 6000.00, 'Sorriso - MT', TRUE,
 'Colheitadeira axial topo de linha com sistema de automação AFS Harvest Command, plataforma drapper de 45 pés e tanque graneleiro de 14.400 litros. Máximo rendimento operacional por hectare.'),

(6, 3, 'Plantadeira Stara Estrela 32 Linhas', 'Estrela 32L', 'Stara', 2023, 'PLANTADEIRA', 430.0, 700.00, 3000.00, 'Sorriso - MT', TRUE,
 'Plantadeira articulada e dobrável de 32 linhas com dosagem variável de sementes e adubo. Sistema de transporte rápido em menos de 1 minuto acionado diretamente da cabine.'),

(7, 4, 'Pulverizador Jacto Uniport 3030', 'Uniport 3030', 'Jacto', 2022, 'PULVERIZADOR', 1420.0, 950.00, 3500.00, 'Rondonópolis - MT', TRUE,
 'Pulverizador autopropelido com reservatório de 3000 litros e barras de pulverização de 36 metros em fibra de carbono. Controle bico a bico, recirculação de calda e direção em todas as 4 rodas.'),

(8, 4, 'Trator Massey Ferguson MF 4707', 'MF 4707', 'Massey Ferguson', 2021, 'TRATOR', 1840.0, 320.00, 1200.00, 'Rondonópolis - MT', TRUE,
 'Trator utilitário compacto de 75 cv, tração 4x4, extremamente econômico e ágil. Excelente para tratos culturais, manobras em galpões, pulverizações pontuais e transporte interno.'),

(9, 4, 'Caminhão Graneleiro Scania R450 6x4', 'R450 6x4', 'Scania', 2022, 'CAMINHAO', 3200.0, 800.00, 3500.00, 'Rondonópolis - MT', TRUE,
 'Cavalo mecânico 6x4 acoplado a conjunto bitrem graneleiro. Potência de 450 cv com capacidade de carga líquida de 38 toneladas para escoamento rápido de safras da lavoura ao armazém.'),

(10, 2, 'Retroescavadeira Caterpillar 416F2', '416F2', 'Caterpillar', 2020, 'RETROESCAVADEIRA', 2950.0, 480.00, 2000.00, 'Maringá - PR', TRUE,
 'Retroescavadeira tração 4x4 com caçamba dianteira multiuso e braço extensível traseiro. Essencial para abertura de curvas de nível, terraceamento, drenagem e conservação de estradas rurais.'),

(11, 5, 'Trator Valtra BH 194 HiTech', 'BH 194 HiTech', 'Valtra', 2022, 'TRATOR', 1310.0, 520.00, 2200.00, 'Rio Verde - GO', TRUE,
 'Trator pesado de 195 cv equipado com transmissão HiTech com PowerShift. Sinônimo de força bruta, durabilidade e alta tração em terrenos ondulados ou solos argilosos pesados.'),

(12, 5, 'Plantadeira John Deere DB40', 'DB40 24 Linhas', 'John Deere', 2021, 'PLANTADEIRA', 780.0, 650.00, 2800.00, 'Rio Verde - GO', TRUE,
 'Plantadeira a vácuo com dosadores eletrônicos MaxEmerge 5e de alta precisão linha a linha. Corte de seções automático RowCommand para eliminação de sobreposição e economia de sementes.'),

(13, 5, 'Pulverizador Stara Imperador 3.0', 'Imperador 3.0', 'Stara', 2023, 'PULVERIZADOR', 510.0, 1100.00, 4500.00, 'Rio Verde - GO', TRUE,
 'Inovador autopropelido 2 em 1: realiza tanto a pulverização de defensivos com barras centrais quanto a distribuição a lanço de fertilizantes e sementes com taxa variável em uma única máquina.'),

(14, 1, 'Colheitadeira New Holland CR 7.90', 'CR 7.90', 'New Holland', 2022, 'COLHEITADEIRA', 1380.0, 1450.00, 5500.00, 'Dourados - MS', TRUE,
 'Colheitadeira de duplo rotor axial Twin Rotor com sistema Dynamic Feed Roll. Preservação excepcional de integridade de grãos e baixíssimo índice de impurezas no tanque graneleiro.'),

(15, 2, 'Caminhão Prancha Volvo FH 540', 'FH 540 Prancha', 'Volvo', 2021, 'CAMINHAO', 2800.0, 900.00, 4000.00, 'Maringá - PR', TRUE,
 'Caminhão prancha rebaixada 3 eixos equipado com guincho hidráulico de 20 toneladas e rampas eletro-hidráulicas para transporte seguro de maquinários agrícolas entre propriedades.'),

(16, 3, 'Retroescavadeira JCB 3CX', '3CX Eco', 'JCB', 2021, 'RETROESCAVADEIRA', 2150.0, 460.00, 1800.00, 'Sorriso - MT', TRUE,
 'Retroescavadeira ágil e econômica com motor JCB EcoMAX. Equipada com caçamba 6 em 1, cabine panorâmica climatizada e sistema de amortecimento de impactos SmoothRide.');

-- -----------------------------------------------------------
-- E. LOCACOES (HISTORICO COMPLETO: PENDENTE, ATIVA, CONCLUIDA, CANCELADA, ATRASADA)
-- -----------------------------------------------------------
INSERT INTO locacoes (id, maquina_id, cliente_id, data_inicio, data_fim, data_devolucao, status, valor_diaria, valor_caucao, valor_total, observacoes, created_at, updated_at) VALUES
-- Locações Concluídas (Histórico com sucesso)
(1, 1, 7, '2026-06-01', '2026-06-10', '2026-06-10', 'CONCLUIDA', 450.00, 2000.00, 4750.00, 'Preparo de solo e gradagem para safra de inverno na Fazenda Alvorada.', '2026-05-28 10:15:00', '2026-06-10 18:00:00'),
(2, 2, 7, '2026-07-05', '2026-07-09', '2026-07-09', 'CONCLUIDA', 350.00, 1500.00, 1950.00, 'Aplicação pontual de fungicida e dessecação de talhão 4.', '2026-07-01 14:20:00', '2026-07-09 17:30:00'),
(3, 3, 8, '2026-07-15', '2026-07-25', '2026-07-25', 'CONCLUIDA', 1200.00, 5000.00, 13935.00, 'Colheita de milho safrinha. Excelente rendimento operacional e perdas mínimas.', '2026-07-10 09:00:00', '2026-07-25 19:00:00'),
(4, 4, 9, '2026-08-01', '2026-08-12', '2026-08-12', 'CONCLUIDA', 850.00, 4000.00, 10690.00, 'Subsolagem pesada e quebra de camada compactada em 350 hectares.', '2026-07-28 11:30:00', '2026-08-12 18:45:00'),

-- Locações Ativas (Em andamento no momento)
(5, 5, 10, '2026-08-10', '2026-08-25', NULL, 'ATIVA', 1600.00, 6000.00, 26255.00, 'Colheita de soja precoce sob pivô central com operador dedicado da plataforma.', '2026-08-05 16:40:00', '2026-08-10 08:00:00'),
(6, 7, 11, '2026-08-12', '2026-08-22', NULL, 'ATIVA', 950.00, 3500.00, 10775.00, 'Pulverização pré-emergente de soja com barra de 36m.', '2026-08-08 08:30:00', '2026-08-12 07:30:00'),
(7, 1, 12, '2026-08-15', '2026-08-28', NULL, 'ATIVA', 450.00, 2000.00, 6530.00, 'Reforma de pastagem e distribuição de calcário com distribuidor acoplado.', '2026-08-11 13:00:00', '2026-08-15 08:15:00'),

-- Locações Pendentes (Aguardando aprovação / pagamento PIX do proprietário)
(8, 6, 7, '2026-08-20', '2026-08-30', NULL, 'PENDENTE', 700.00, 3000.00, 7990.00, 'Plantio de milho 1ª safra. Aguardando conferência do sinal via PIX.', '2026-08-18 10:00:00', '2026-08-18 10:00:00'),
(9, 11, 13, '2026-08-22', '2026-08-31', NULL, 'PENDENTE', 520.00, 2200.00, 5450.00, 'Aração e preparo profundo de solo em área nova.', '2026-08-18 11:15:00', '2026-08-18 11:15:00'),
(10, 10, 8, '2026-08-25', '2026-09-02', NULL, 'PENDENTE', 480.00, 2000.00, 4520.00, 'Abertura de curvas de nível e contenção de águas pluviais.', '2026-08-18 13:45:00', '2026-08-18 13:45:00'),

-- Locações Canceladas (Desistências registradas)
(11, 12, 9, '2026-05-10', '2026-05-18', NULL, 'CANCELADA', 650.00, 2800.00, 6050.00, 'Cancelado pelo cliente por estiagem prolongada e reagendamento de janela de plantio.', '2026-05-02 09:20:00', '2026-05-08 14:10:00'),
(12, 8, 10, '2026-06-15', '2026-06-20', NULL, 'CANCELADA', 320.00, 1200.00, 2120.00, 'Cancelado: cliente utilizou equipamento próprio de outra fazenda.', '2026-06-10 15:00:00', '2026-06-12 11:00:00'),

-- Locação Atrasada (Alerta no sistema para devolução)
(13, 14, 11, '2026-07-20', '2026-08-05', NULL, 'ATRASADA', 1450.00, 5500.00, 24955.00, 'Operação estendida em campo para finalização de safra; aguardando formalização de aditivo.', '2026-07-16 10:40:00', '2026-08-06 09:00:00');

-- -----------------------------------------------------------
-- F. SERVICOS VINCULADOS AS LOCACOES
-- -----------------------------------------------------------
INSERT INTO locacao_servicos (locacao_id, servico_id, preco_cobrado) VALUES
-- Locacao 1
(1, 1, 50.00),  -- Seguro Danos
(1, 2, 200.00), -- Entrega

-- Locacao 2
(2, 2, 200.00), -- Entrega

-- Locacao 3
(3, 1, 50.00),  -- Seguro Danos
(3, 3, 35.00),  -- Seguro Roubo
(3, 4, 180.00), -- Operador
(3, 6, 350.00), -- Tanque Cheio
(3, 7, 120.00), -- Lavagem

-- Locacao 4
(4, 1, 50.00),  -- Seguro Danos
(4, 5, 90.00),  -- Manutencao/RTK
(4, 6, 350.00), -- Tanque Cheio

-- Locacao 5
(5, 1, 50.00),  -- Seguro Danos
(5, 4, 180.00), -- Operador
(5, 6, 350.00), -- Tanque Cheio
(5, 8, 75.00),  -- Assistencia 24h

-- Locacao 6
(6, 2, 200.00), -- Entrega
(6, 3, 35.00),  -- Seguro Roubo
(6, 5, 90.00),  -- Manutencao/RTK

-- Locacao 7
(7, 1, 50.00),  -- Seguro Danos
(7, 4, 180.00), -- Operador

-- Locacao 8
(8, 2, 200.00), -- Entrega
(8, 5, 90.00),  -- Manutencao/RTK

-- Locacao 9
(9, 1, 50.00),  -- Seguro Danos
(9, 2, 200.00), -- Entrega

-- Locacao 10
(10, 2, 200.00), -- Entrega

-- Locacao 11
(11, 2, 200.00), -- Entrega

-- Locacao 12
(12, 2, 200.00), -- Entrega

-- Locacao 13
(13, 1, 50.00),  -- Seguro Danos
(13, 4, 180.00), -- Operador
(13, 8, 75.00);  -- Assistencia 24h

-- -----------------------------------------------------------
-- G. TELEMETRIA E MANUTENCAO PREDITIVA
-- Leituras com status Normal, Atencao (Temp. Alta) e Alerta Critico
-- -----------------------------------------------------------
INSERT INTO telemetria (maquina_id, locacao_id, horas_uso, temperatura_motor, consumo_combustivel, rpm_motor, pressao_oleo, data_registro) VALUES
-- Trator Alpha (maquina 1 - em operacao)
(1, 1, 1205.0, 84.5, 14.2, 1950, 3.8, '2026-06-03 10:30:00'),
(1, 1, 1220.0, 86.0, 15.0, 2050, 3.7, '2026-06-06 14:15:00'),
(1, 1, 1245.0, 88.2, 14.8, 2000, 3.6, '2026-06-09 16:45:00'),
(1, 7, 1248.0, 85.0, 13.9, 1900, 3.9, '2026-08-16 09:00:00'),
(1, 7, 1250.5, 94.5, 16.5, 2150, 3.2, '2026-08-17 15:20:00'), -- Alerta de Atencao (Temp 94.5 > 90)

-- Pulverizador Gamma (maquina 2)
(2, 2, 2085.0, 81.0, 11.2, 1800, 4.1, '2026-07-06 08:30:00'),
(2, 2, 2095.0, 82.5, 11.8, 1850, 4.0, '2026-07-08 11:00:00'),
(2, NULL, 2100.0, 79.5, 10.5, 1750, 4.2, '2026-08-10 14:00:00'),

-- Colheitadeira Beta CH-580 (maquina 3)
(3, 3, 850.0, 87.0, 28.5, 2100, 3.5, '2026-07-18 11:30:00'),
(3, 3, 875.0, 89.5, 29.8, 2150, 3.4, '2026-07-22 14:45:00'),
(3, 3, 890.0, 93.0, 31.0, 2200, 3.2, '2026-07-24 16:10:00'), -- Alerta de Atencao (Temp 93.0)

-- Trator John Deere 8R 370 (maquina 4)
(4, 4, 580.0, 86.5, 22.0, 1950, 4.2, '2026-08-04 10:00:00'),
(4, 4, 600.0, 88.0, 23.5, 2000, 4.0, '2026-08-08 15:30:00'),
(4, 4, 620.0, 87.2, 21.8, 1920, 4.1, '2026-08-12 17:00:00'),

-- Colheitadeira Case IH Axial-Flow 8250 (maquina 5 - locacao ativa)
(5, 5, 1120.0, 88.5, 34.0, 2150, 3.6, '2026-08-12 10:15:00'),
(5, 5, 1135.0, 96.0, 36.5, 2200, 2.9, '2026-08-14 14:30:00'), -- Alerta de Atencao (Temp 96.0)
(5, 5, 1150.0, 103.5, 38.0, 2250, 1.4, '2026-08-17 16:45:00'), -- Alerta Critico (Temp 103.5 e Pressao 1.4)

-- Pulverizador Jacto Uniport 3030 (maquina 7 - locacao ativa)
(7, 6, 1395.0, 82.0, 18.0, 1900, 4.0, '2026-08-13 08:30:00'),
(7, 6, 1410.0, 84.5, 19.2, 1950, 3.9, '2026-08-15 11:15:00'),
(7, 6, 1420.0, 83.0, 18.5, 1920, 4.1, '2026-08-17 14:00:00'),

-- Retroescavadeira Caterpillar 416F2 (maquina 10)
(10, NULL, 2920.0, 85.0, 9.5, 1800, 3.8, '2026-08-01 09:00:00'),
(10, NULL, 2940.0, 87.5, 10.2, 1850, 3.7, '2026-08-08 13:45:00'),
(10, NULL, 2950.0, 86.0, 9.8, 1820, 3.9, '2026-08-15 15:30:00'),

-- Trator Valtra BH 194 (maquina 11)
(11, NULL, 1290.0, 84.0, 17.5, 1900, 4.0, '2026-08-05 10:00:00'),
(11, NULL, 1310.0, 85.5, 18.0, 1950, 3.9, '2026-08-12 16:20:00'),

-- Colheitadeira New Holland CR 7.90 (maquina 14 - locacao atrasada)
(14, 13, 1340.0, 89.0, 32.0, 2100, 3.5, '2026-07-25 11:00:00'),
(14, 13, 1365.0, 92.5, 33.8, 2150, 3.3, '2026-07-31 15:00:00'), -- Alerta de Atencao (Temp 92.5)
(14, 13, 1380.0, 88.0, 31.5, 2050, 3.6, '2026-08-05 17:30:00');

-- -----------------------------------------------------------
-- H. INDISPONIBILIDADES E MANUTENCAO PROGRAMADA
-- -----------------------------------------------------------
INSERT INTO indisponibilidades (id, maquina_id, data_inicio, data_fim, motivo) VALUES
(1, 1, '2026-09-10', '2026-09-15', 'Revisão periódica de 1500h, troca de filtros e lubrificação do rodado.'),
(2, 3, '2026-09-01', '2026-09-08', 'Manutenção preventiva da plataforma drapper e revisão dos rotores axiais.'),
(3, 5, '2026-09-20', '2026-09-25', 'Calibração dos sensores de rendimento e atualização do software AFS.'),
(4, 7, '2026-09-05', '2026-09-09', 'Substituição preventiva das pontas de pulverização e aferição de vazão da bomba.'),
(5, 10, '2026-10-01', '2026-10-05', 'Revisão do sistema hidráulico, troca de mangueiras e embuchamento das conchas.'),
(6, 12, '2026-09-15', '2026-09-22', 'Aferição dos discos dosadores pneumáticos para a próxima safra.');
