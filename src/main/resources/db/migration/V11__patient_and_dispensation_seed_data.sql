-- Dados demonstrativos para pacientes e dispensacoes do MVP.

INSERT INTO pacientes (id, nome, cpf, data_nascimento, cartao_sus, telefone, email, cidade, uf, status, data_criacao)
VALUES
    (9101, 'Maria Aparecida Silva', '52998224725', DATE '1978-04-12', '898001234567890', '(11) 98888-1001', 'maria.silva@example.com', 'Sao Paulo', 'SP', 'ATIVO', CURRENT_TIMESTAMP),
    (9102, 'Joao Pedro Santos', '11144477735', DATE '1986-09-21', '898001234567891', '(11) 98888-1002', 'joao.santos@example.com', 'Sao Paulo', 'SP', 'ATIVO', CURRENT_TIMESTAMP),
    (9103, 'Ana Clara Oliveira', '12345678909', DATE '1994-01-30', NULL, '(11) 98888-1003', 'ana.oliveira@example.com', 'Sao Paulo', 'SP', 'ATIVO', CURRENT_TIMESTAMP)
ON CONFLICT (cpf) DO UPDATE SET
    nome = EXCLUDED.nome,
    telefone = EXCLUDED.telefone,
    email = EXCLUDED.email,
    cidade = EXCLUDED.cidade,
    uf = EXCLUDED.uf,
    status = EXCLUDED.status,
    data_ultima_alteracao = CURRENT_TIMESTAMP;

INSERT INTO dispensacoes (id, paciente_id, saida_estoque_id, numero_receita, crm_prescritor)
VALUES
    (9101, 9101, 9001, 'RX-MVP-0001', 'CRM-SP 100001'),
    (9102, 9102, 9003, 'RX-MVP-0002', 'CRM-SP 100002'),
    (9103, 9103, 9005, 'RX-MVP-0003', 'CRM-SP 100003')
ON CONFLICT (saida_estoque_id) DO UPDATE SET
    paciente_id = EXCLUDED.paciente_id,
    numero_receita = EXCLUDED.numero_receita,
    crm_prescritor = EXCLUDED.crm_prescritor;

SELECT setval(pg_get_serial_sequence('pacientes', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9103) FROM pacientes));
SELECT setval(pg_get_serial_sequence('dispensacoes', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9103) FROM dispensacoes));