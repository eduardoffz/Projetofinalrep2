-- ============================================================
-- AGRIRENT - Plataforma de Aluguel de Maquinas Agricolas
-- Script de criacao do banco de dados
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_agrirent;
USE db_agrirent;

-- -----------------------------------------------------------
-- Tabela de usuarios do sistema
-- Roles: CLIENTE, PROPRIETARIO, ADMIN
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
-- Tabela de proprietarios (anunciantes)
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
-- Tabela de maquinas agricolas disponiveis para aluguel
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
-- Tabela de locacoes (contratos de aluguel)
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
-- Tabela de servicos adicionais (seguro, entrega, limpeza)
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
-- Tabela de vinculo locacao x servicos
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
-- Tabela de telemetria (dados dos sensores durante locacao)
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
-- Tabela de indisponibilidades (bloqueios de calendario)
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
-- DADOS DE EXEMPLO
-- ============================================================

-- Usuario admin (senha: admin123)
INSERT INTO usuarios (nome, email, senha, role) VALUES
('Administrador', 'admin@agrirent.com', '$2a$10$QidVv0Xh5dcEJ1vxVAo7Zuu.5jZqtDQGa/Ogw4RtZc5lAgPzcqadC', 'ADMIN');

-- Proprietarios (senha: prop123)
INSERT INTO usuarios (nome, email, senha, role, telefone) VALUES
('Joao Proprietario', 'joao@agrirent.com', '$2a$10$gx6OoLatb.55obf6ZMHOOOhYW.UvDq4blxo3V.xFIn1YPnusGR53q', 'PROPRIETARIO', '(67) 99999-1111'),
('Maria Proprietaria', 'maria@agrirent.com', '$2a$10$gx6OoLatb.55obf6ZMHOOOhYW.UvDq4blxo3V.xFIn1YPnusGR53q', 'PROPRIETARIO', '(44) 99999-2222');

-- Cliente de teste (senha: cli123)
INSERT INTO usuarios (nome, email, senha, role, telefone) VALUES
('Carlos Cliente', 'carlos@agrirent.com', '$2a$10$13K.gWJ.UpkVWg2M1D.7pO.Qb25IR9RKRI3slX/YROHa597B.wuK6', 'CLIENTE', '(11) 98888-3333');

-- Perfis de proprietarios
INSERT INTO proprietarios (usuario_id, documento, chave_pix, endereco) VALUES
(2, '111.222.333-44', 'joao@agrirent.com', 'Fazenda Boa Esperanca, Rodovia BR-163, km 45 - MS'),
(3, '222.333.444-55', 'maria@agrirent.com', 'Sitio Recanto Verde, Estrada Municipal 12 - PR');

-- Maquinas para aluguel
INSERT INTO maquinas (proprietario_id, nome, modelo, fabricante, ano_fabricacao, tipo, horas_uso_totais, preco_diaria, caucao, localizacao, disponivel, descricao) VALUES
(1, 'Trator Alpha', 'TLX-3000', 'JohnDeere', 2022, 'TRATOR', 1250.5, 450.00, 2000.00, 'Fazenda Boa Esperanca - MS', TRUE,
 'Trator agricola potente para preparo de solo, plantio e tratos culturais. Equipado com GPS, piloto automatico e ar condicionado. Ideal para medias e grandes propriedades.'),
(1, 'Pulverizador Gamma', 'PL-200', 'Jacto', 2021, 'PULVERIZADOR', 2100.0, 350.00, 1500.00, 'Fazenda Boa Esperanca - MS', TRUE,
 'Pulverizador de barras com 2000L de capacidade, ideal para aplicacao de defensivos e fertilizantes liquidos. Equipado com pontas de pulverizacao de alta precisao.'),
(2, 'Colheitadeira Beta', 'CH-580', 'NewHolland', 2023, 'COLHEITADEIRA', 890.0, 1200.00, 5000.00, 'Sitio Recanto Verde - PR', TRUE,
 'Colheitadeira de graos com tecnologia de perdas minimas. Capacidade do tanque graneleiro: 6000L. Ideal para soja, milho e trigo. Baixas horas de uso.');

-- Servicos adicionais
INSERT INTO servicos_adicionais (nome, descricao, preco, tipo, ativo) VALUES
('Seguro Contra Danos', 'Cobertura contra danos acidentais durante o periodo de locacao', 50.00, 'SEGURO', TRUE),
('Entrega e Retirada', 'Transporte da maquina ate a propriedade do cliente', 200.00, 'ENTREGA', TRUE),
('Seguro Roubo e Furto', 'Cobertura contra roubo e furto da maquina locada', 35.00, 'SEGURO', TRUE),
('Operador Experiente', 'Operador qualificado para operar a maquina durante a locacao', 180.00, 'MANUTENCAO', TRUE);
