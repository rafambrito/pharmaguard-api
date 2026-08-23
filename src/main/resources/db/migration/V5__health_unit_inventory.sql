CREATE TABLE unidades_saude (
    id BIGINT PRIMARY KEY,
    identificacao VARCHAR(30) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    endereco VARCHAR(300) NOT NULL,
    status VARCHAR(10) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP
);

INSERT INTO unidades_saude (id, identificacao, nome, tipo, endereco, status, data_cadastro)
VALUES (1, 'LEGACY', 'Unidade legada', 'LEGADO', 'Nao informado', 'ATIVA', CURRENT_TIMESTAMP);

ALTER TABLE entradas_estoque ADD COLUMN unidade_saude_id BIGINT REFERENCES unidades_saude(id);
ALTER TABLE saidas_estoque ADD COLUMN unidade_saude_id BIGINT REFERENCES unidades_saude(id);
ALTER TABLE movimentacoes_estoque ADD COLUMN unidade_saude_id BIGINT REFERENCES unidades_saude(id);
UPDATE entradas_estoque SET unidade_saude_id = 1 WHERE unidade_saude_id IS NULL;
UPDATE saidas_estoque SET unidade_saude_id = 1 WHERE unidade_saude_id IS NULL;
UPDATE movimentacoes_estoque SET unidade_saude_id = 1 WHERE unidade_saude_id IS NULL;
ALTER TABLE entradas_estoque ALTER COLUMN unidade_saude_id SET NOT NULL;
ALTER TABLE saidas_estoque ALTER COLUMN unidade_saude_id SET NOT NULL;
ALTER TABLE movimentacoes_estoque ALTER COLUMN unidade_saude_id SET NOT NULL;
ALTER TABLE saldo_lotes_estoque DROP CONSTRAINT saldo_lotes_estoque_pkey;
ALTER TABLE saldo_lotes_estoque ADD COLUMN unidade_saude_id BIGINT REFERENCES unidades_saude(id);
UPDATE saldo_lotes_estoque SET unidade_saude_id = 1 WHERE unidade_saude_id IS NULL;
ALTER TABLE saldo_lotes_estoque ALTER COLUMN unidade_saude_id SET NOT NULL;
ALTER TABLE saldo_lotes_estoque ADD PRIMARY KEY (lote_id, unidade_saude_id);

CREATE INDEX idx_entradas_estoque_unidade ON entradas_estoque(unidade_saude_id);
CREATE INDEX idx_saidas_estoque_unidade ON saidas_estoque(unidade_saude_id);
CREATE INDEX idx_movimentacoes_estoque_unidade ON movimentacoes_estoque(unidade_saude_id);
CREATE INDEX idx_saldo_lotes_estoque_unidade ON saldo_lotes_estoque(unidade_saude_id);