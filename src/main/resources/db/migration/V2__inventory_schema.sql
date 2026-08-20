-- Schema do modulo inventario: categorias, unidades de medida, medicamentos e lotes.

CREATE TABLE categorias (
    id                    BIGSERIAL PRIMARY KEY,
    nome                  VARCHAR(100) NOT NULL,
    descricao             VARCHAR(500),
    status                VARCHAR(20)  NOT NULL,
    data_criacao          TIMESTAMP,
    data_ultima_alteracao TIMESTAMP,
    CONSTRAINT uk_categorias_nome UNIQUE (nome)
);

CREATE TABLE unidades_medida (
    id                    BIGSERIAL PRIMARY KEY,
    nome                  VARCHAR(100) NOT NULL,
    sigla                 VARCHAR(20)  NOT NULL,
    status                VARCHAR(20)  NOT NULL,
    data_criacao          TIMESTAMP,
    data_ultima_alteracao TIMESTAMP,
    CONSTRAINT uk_unidades_medida_sigla UNIQUE (sigla)
);

CREATE TABLE medicamentos (
    id                    BIGSERIAL PRIMARY KEY,
    nome                  VARCHAR(200) NOT NULL,
    apresentacao          VARCHAR(200) NOT NULL,
    descricao             VARCHAR(500),
    categoria_id          BIGINT       NOT NULL REFERENCES categorias(id),
    unidade_medida_id     BIGINT       NOT NULL REFERENCES unidades_medida(id),
    criticidade           VARCHAR(20)  NOT NULL,
    status                VARCHAR(20)  NOT NULL,
    data_criacao          TIMESTAMP,
    data_ultima_alteracao TIMESTAMP,
    CONSTRAINT uk_medicamentos_nome_apresentacao UNIQUE (nome, apresentacao)
);

CREATE TABLE lotes (
    id                 BIGSERIAL PRIMARY KEY,
    numero_lote        VARCHAR(100) NOT NULL,
    data_validade      DATE         NOT NULL,
    quantidade_inicial INT          NOT NULL,
    medicamento_id     BIGINT       NOT NULL REFERENCES medicamentos(id),
    CONSTRAINT uk_lotes_numero_medicamento UNIQUE (numero_lote, medicamento_id)
);
