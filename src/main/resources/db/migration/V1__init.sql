-- Fundação do schema inicial do PharmaGuard.
-- Esta migration valida a integração Flyway + PostgreSQL na etapa T0.4.

CREATE TABLE foundation_validation (
    id BIGINT PRIMARY KEY,
    marker VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO foundation_validation (id, marker)
VALUES (1, 'T0.4_FLYWAY_INIT');
