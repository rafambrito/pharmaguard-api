-- Schema do modulo fornecedores: fornecedores e contatos.

CREATE TABLE fornecedores (
    id                    BIGSERIAL PRIMARY KEY,
    nome                  VARCHAR(200) NOT NULL,
    codigo                VARCHAR(100),
    documento             VARCHAR(30),
    observacao            VARCHAR(500),
    lead_time_dias        INT          NOT NULL,
    status                VARCHAR(20)  NOT NULL,
    data_criacao          TIMESTAMP,
    data_ultima_alteracao TIMESTAMP,
    CONSTRAINT uk_fornecedores_codigo UNIQUE (codigo),
    CONSTRAINT uk_fornecedores_documento UNIQUE (documento)
);

CREATE TABLE fornecedor_contatos (
    id                    BIGSERIAL PRIMARY KEY,
    fornecedor_id         BIGINT       NOT NULL REFERENCES fornecedores(id) ON DELETE CASCADE,
    nome                  VARCHAR(150) NOT NULL,
    cargo                 VARCHAR(100),
    telefone              VARCHAR(30),
    email                 VARCHAR(150),
    canal_principal       VARCHAR(20)  NOT NULL,
    ativo                 BOOLEAN      NOT NULL,
    data_criacao          TIMESTAMP,
    data_ultima_alteracao TIMESTAMP,
    CONSTRAINT uk_fornecedor_contatos_nome_canal UNIQUE (fornecedor_id, nome, canal_principal)
);