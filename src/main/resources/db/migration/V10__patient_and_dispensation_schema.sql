-- Schema para cadastro de pacientes e dispensacao de medicamentos.

CREATE TABLE pacientes (
    id                    BIGSERIAL PRIMARY KEY,
    nome                  VARCHAR(150) NOT NULL,
    cpf                   VARCHAR(11)  NOT NULL,
    data_nascimento       DATE         NOT NULL,
    cartao_sus            VARCHAR(30),
    telefone              VARCHAR(30),
    email                 VARCHAR(150),
    cidade                VARCHAR(100),
    uf                    VARCHAR(2),
    status                VARCHAR(20)  NOT NULL,
    data_criacao          TIMESTAMP    NOT NULL,
    data_ultima_alteracao TIMESTAMP,
    CONSTRAINT uk_pacientes_cpf UNIQUE (cpf)
);

CREATE TABLE dispensacoes (
    id               BIGSERIAL PRIMARY KEY,
    paciente_id      BIGINT       NOT NULL REFERENCES pacientes(id),
    saida_estoque_id BIGINT       NOT NULL REFERENCES saidas_estoque(id),
    numero_receita   VARCHAR(100),
    crm_prescritor   VARCHAR(50),
    CONSTRAINT uk_dispensacoes_saida_estoque UNIQUE (saida_estoque_id)
);

CREATE INDEX idx_pacientes_nome ON pacientes(nome);
CREATE INDEX idx_dispensacoes_paciente ON dispensacoes(paciente_id);