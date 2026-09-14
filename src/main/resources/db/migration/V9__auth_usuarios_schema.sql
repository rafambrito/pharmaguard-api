-- Schema do modulo auth: usuarios.

CREATE TABLE usuarios (
    id                    BIGSERIAL PRIMARY KEY,
    nome                  VARCHAR(100) NOT NULL,
    email                 VARCHAR(150) NOT NULL,
    login                 VARCHAR(50)  NOT NULL,
    tipo                  VARCHAR(50)  NOT NULL,
    perfil                VARCHAR(50)  NOT NULL,
    senha_hash            VARCHAR(255) NOT NULL,
    status                VARCHAR(20)  NOT NULL,
    data_criacao          TIMESTAMP,
    data_ultima_alteracao TIMESTAMP,
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT uk_usuarios_login UNIQUE (login)
);
