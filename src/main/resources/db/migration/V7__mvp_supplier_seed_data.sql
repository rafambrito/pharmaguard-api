-- Fornecedores iniciais para testes do MVP. Executado automaticamente pelo Flyway na inicializacao.

INSERT INTO fornecedores (id, nome, codigo, documento, observacao, lead_time_dias, status, data_criacao)
VALUES
    (9001, 'Distribuidora Vida Farma', 'MVP-FORN-001', '11222333000144', 'Fornecedor principal para medicamentos de alto giro no MVP.', 5, 'ATIVO', CURRENT_TIMESTAMP),
    (9002, 'Saude Total Medicamentos', 'MVP-FORN-002', '22333444000155', 'Fornecedor para antibioticos e medicamentos de uso continuo.', 9, 'ATIVO', CURRENT_TIMESTAMP),
    (9003, 'Insumos Hospitalares Brasil', 'MVP-FORN-003', '33444555000166', 'Fornecedor para itens injetaveis e atendimento de urgencia.', 14, 'ATIVO', CURRENT_TIMESTAMP)
ON CONFLICT (codigo) DO UPDATE SET
    nome = EXCLUDED.nome,
    documento = EXCLUDED.documento,
    observacao = EXCLUDED.observacao,
    lead_time_dias = EXCLUDED.lead_time_dias,
    status = EXCLUDED.status,
    data_ultima_alteracao = CURRENT_TIMESTAMP;

SELECT setval(pg_get_serial_sequence('fornecedores', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9001) FROM fornecedores));