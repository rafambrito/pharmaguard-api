-- Schema do modulo estoque: entradas, saidas, saldos e movimentacoes.

CREATE TABLE entradas_estoque (
    id                    BIGSERIAL PRIMARY KEY,
    medicamento_id        BIGINT       NOT NULL REFERENCES medicamentos(id),
    lote_id               BIGINT       NOT NULL REFERENCES lotes(id),
    quantidade            INT          NOT NULL,
    data_entrada          TIMESTAMP    NOT NULL,
    origem                VARCHAR(40)  NOT NULL,
    documento             VARCHAR(120),
    observacao            VARCHAR(500),
    usuario_responsavel_id BIGINT
);

CREATE TABLE saidas_estoque (
    id                    BIGSERIAL PRIMARY KEY,
    medicamento_id        BIGINT       NOT NULL REFERENCES medicamentos(id),
    quantidade_total      INT          NOT NULL,
    data_saida            TIMESTAMP    NOT NULL,
    motivo                VARCHAR(40)  NOT NULL,
    observacao            VARCHAR(500),
    usuario_responsavel_id BIGINT
);

CREATE TABLE saida_lotes_utilizados (
    id                    BIGSERIAL PRIMARY KEY,
    saida_id              BIGINT       NOT NULL REFERENCES saidas_estoque(id) ON DELETE CASCADE,
    lote_id               BIGINT       NOT NULL REFERENCES lotes(id),
    numero_lote           VARCHAR(100) NOT NULL,
    quantidade_consumida  INT          NOT NULL
);

CREATE TABLE saldo_lotes_estoque (
    lote_id                 BIGINT PRIMARY KEY REFERENCES lotes(id),
    quantidade_disponivel   INT       NOT NULL,
    data_ultima_movimentacao TIMESTAMP
);

CREATE TABLE movimentacoes_estoque (
    id                     BIGSERIAL PRIMARY KEY,
    tipo                   VARCHAR(20)  NOT NULL,
    medicamento_id         BIGINT       NOT NULL REFERENCES medicamentos(id),
    lote_id                BIGINT REFERENCES lotes(id),
    quantidade             INT          NOT NULL,
    saldo_apos_movimentacao INT         NOT NULL,
    data_movimentacao      TIMESTAMP    NOT NULL,
    motivo                 VARCHAR(120) NOT NULL,
    usuario_responsavel_id BIGINT
);

CREATE INDEX idx_entradas_estoque_medicamento ON entradas_estoque(medicamento_id);
CREATE INDEX idx_entradas_estoque_lote ON entradas_estoque(lote_id);
CREATE INDEX idx_saidas_estoque_medicamento ON saidas_estoque(medicamento_id);
CREATE INDEX idx_saldo_lotes_estoque_quantidade ON saldo_lotes_estoque(quantidade_disponivel);
CREATE INDEX idx_movimentacoes_estoque_medicamento ON movimentacoes_estoque(medicamento_id);
CREATE INDEX idx_movimentacoes_estoque_lote ON movimentacoes_estoque(lote_id);
CREATE INDEX idx_movimentacoes_estoque_data ON movimentacoes_estoque(data_movimentacao);
