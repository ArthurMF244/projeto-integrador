SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    setor VARCHAR(100) NOT NULL,
    perfil ENUM('Administrador', 'Atendente', 'Solicitante') NOT NULL DEFAULT 'Solicitante',
    status ENUM('Ativo', 'Inativo') NOT NULL DEFAULT 'Ativo',
    nome_usuario VARCHAR(50) NULL UNIQUE,
    senha VARCHAR(100) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chamados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(180) NOT NULL,
    solicitante_id INT NULL,
    solicitante_nome VARCHAR(150) NOT NULL,
    area_solicitante VARCHAR(100) NOT NULL,
    area_responsavel VARCHAR(100) NOT NULL,
    responsavel_id INT NULL,
    responsavel_nome VARCHAR(150) NULL,
    categoria VARCHAR(100) NOT NULL,
    prioridade ENUM('Baixa', 'Média', 'Alta', 'Crítica') NOT NULL DEFAULT 'Média',
    impacto ENUM('Baixo', 'Médio', 'Alto', 'Geral') NOT NULL DEFAULT 'Baixo',
    status ENUM('Novo', 'Em atendimento', 'Aguardando retorno', 'Finalizado') NOT NULL DEFAULT 'Novo',
    descricao TEXT NOT NULL,
    anexo_nome VARCHAR(255) NULL,
    aberto_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finalizado_em DATETIME NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_chamados_solicitante FOREIGN KEY (solicitante_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT fk_chamados_responsavel FOREIGN KEY (responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_chamados_status (status),
    INDEX idx_chamados_prioridade (prioridade),
    INDEX idx_chamados_area_responsavel (area_responsavel),
    INDEX idx_chamados_aberto_em (aberto_em)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chamado_movimentacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    chamado_id INT NOT NULL,
    usuario_id INT NULL,
    status ENUM('Novo', 'Em atendimento', 'Aguardando retorno', 'Finalizado') NOT NULL,
    prioridade ENUM('Baixa', 'Média', 'Alta', 'Crítica') NOT NULL,
    area_responsavel VARCHAR(100) NOT NULL,
    responsavel_id INT NULL,
    responsavel_nome VARCHAR(150) NULL,
    descricao TEXT NULL,
    anexo_nome VARCHAR(255) NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mov_chamado FOREIGN KEY (chamado_id) REFERENCES chamados(id) ON DELETE CASCADE,
    CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT fk_mov_responsavel FOREIGN KEY (responsavel_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_mov_chamado (chamado_id),
    INDEX idx_mov_criado_em (criado_em)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS configuracoes (
    id TINYINT PRIMARY KEY DEFAULT 1,
    nome_sistema VARCHAR(120) NOT NULL DEFAULT 'SI Chamados',
    tema ENUM('', 'dark') NOT NULL DEFAULT '',
    email_suporte VARCHAR(150) NOT NULL DEFAULT 'suporte@exemplo.com',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO configuracoes (id, nome_sistema, tema, email_suporte)
VALUES (1, 'SI Chamados', '', 'suporte@exemplo.com')
ON DUPLICATE KEY UPDATE id = id;

INSERT INTO usuarios (nome, email, setor, perfil, status) VALUES
('Arthur de Marco Faggion', 'arthur@example.com', 'TI - Sistemas', 'Administrador', 'Ativo'),
('Maria Silva', 'maria@example.com', 'Recepção', 'Solicitante', 'Ativo'),
('João Souza', 'joao@example.com', 'TI - Infraestrutura', 'Atendente', 'Ativo')
ON DUPLICATE KEY UPDATE nome = VALUES(nome), setor = VALUES(setor), perfil = VALUES(perfil), status = VALUES(status);

-- Corrige de forma idempotente seeds de versões antigas que foram importados com dupla codificação.
UPDATE usuarios
SET nome = CONVERT(0x4A6FC3A36F20536F757A61 USING utf8mb4)
WHERE email = 'joao@example.com';

UPDATE usuarios
SET setor = CONVERT(0x5265636570C3A7C3A36F USING utf8mb4)
WHERE email = 'maria@example.com';
