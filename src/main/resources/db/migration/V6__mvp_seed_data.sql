-- Dados iniciais para testes do MVP. Executado automaticamente pelo Flyway na inicializacao.

INSERT INTO categorias (id, nome, descricao, status, data_criacao)
VALUES
    (9001, 'Analgesicos', 'Medicamentos para controle de dor e febre.', 'ATIVA', CURRENT_TIMESTAMP),
    (9002, 'Antibioticos', 'Medicamentos antimicrobianos usados em tratamentos infecciosos.', 'ATIVA', CURRENT_TIMESTAMP),
    (9003, 'Cardiovasculares', 'Medicamentos para acompanhamento de hipertensao e doencas cardiovasculares.', 'ATIVA', CURRENT_TIMESTAMP),
    (9004, 'Endocrinos', 'Medicamentos para tratamentos metabolicos e hormonais.', 'ATIVA', CURRENT_TIMESTAMP)
ON CONFLICT (nome) DO UPDATE SET
    descricao = EXCLUDED.descricao,
    status = EXCLUDED.status,
    data_ultima_alteracao = CURRENT_TIMESTAMP;

INSERT INTO unidades_medida (id, nome, sigla, status, data_criacao)
VALUES
    (9001, 'Comprimido', 'CPR', 'ATIVA', CURRENT_TIMESTAMP),
    (9002, 'Frasco', 'FRC', 'ATIVA', CURRENT_TIMESTAMP),
    (9003, 'Ampola', 'AMP', 'ATIVA', CURRENT_TIMESTAMP)
ON CONFLICT (sigla) DO UPDATE SET
    nome = EXCLUDED.nome,
    status = EXCLUDED.status,
    data_ultima_alteracao = CURRENT_TIMESTAMP;

INSERT INTO unidades_saude (id, identificacao, nome, tipo, endereco, status, data_cadastro)
VALUES
    (9001, 'MVP-CENTRAL', 'UBS Central', 'UBS', 'Rua Principal, 100 - Centro', 'ATIVA', CURRENT_TIMESTAMP),
    (9002, 'MVP-NORTE', 'UBS Norte', 'UBS', 'Avenida Norte, 250 - Bairro Norte', 'ATIVA', CURRENT_TIMESTAMP),
    (9003, 'MVP-SUL', 'UPA Sul', 'UPA', 'Rua Sul, 480 - Bairro Sul', 'ATIVA', CURRENT_TIMESTAMP)
ON CONFLICT (identificacao) DO UPDATE SET
    nome = EXCLUDED.nome,
    tipo = EXCLUDED.tipo,
    endereco = EXCLUDED.endereco,
    status = EXCLUDED.status,
    data_atualizacao = CURRENT_TIMESTAMP;

INSERT INTO medicamentos (id, nome, apresentacao, descricao, categoria_id, unidade_medida_id, criticidade, status, data_criacao)
VALUES
    (9001, 'Dipirona Sodica', '500 mg comprimido', 'Analgesico e antitermico de uso adulto.', 9001, 9001, 'ALTA', 'ATIVO', CURRENT_TIMESTAMP),
    (9002, 'Amoxicilina', '500 mg capsula', 'Antibiotico beta-lactamico para uso ambulatorial.', 9002, 9001, 'CRITICA', 'ATIVO', CURRENT_TIMESTAMP),
    (9003, 'Losartana Potassica', '50 mg comprimido', 'Anti-hipertensivo de uso continuo.', 9003, 9001, 'MEDIA', 'ATIVO', CURRENT_TIMESTAMP),
    (9004, 'Insulina NPH', '100 UI/mL frasco', 'Insulina para controle glicemico.', 9004, 9002, 'CRITICA', 'ATIVO', CURRENT_TIMESTAMP),
    (9005, 'Ceftriaxona', '1 g ampola', 'Antibiotico injetavel para atendimento de urgencia.', 9002, 9003, 'ALTA', 'ATIVO', CURRENT_TIMESTAMP)
ON CONFLICT (nome, apresentacao) DO UPDATE SET
    descricao = EXCLUDED.descricao,
    categoria_id = EXCLUDED.categoria_id,
    unidade_medida_id = EXCLUDED.unidade_medida_id,
    criticidade = EXCLUDED.criticidade,
    status = EXCLUDED.status,
    data_ultima_alteracao = CURRENT_TIMESTAMP;

INSERT INTO lotes (id, numero_lote, data_validade, quantidade_inicial, medicamento_id)
VALUES
    (9001, 'MVP-DIP-001', CURRENT_DATE + INTERVAL '180 days', 1000, 9001),
    (9002, 'MVP-DIP-002', CURRENT_DATE + INTERVAL '45 days', 300, 9001),
    (9003, 'MVP-AMX-001', CURRENT_DATE + INTERVAL '150 days', 800, 9002),
    (9004, 'MVP-AMX-002', CURRENT_DATE + INTERVAL '25 days', 250, 9002),
    (9005, 'MVP-LOS-001', CURRENT_DATE + INTERVAL '240 days', 1200, 9003),
    (9006, 'MVP-INS-001', CURRENT_DATE + INTERVAL '18 days', 180, 9004),
    (9007, 'MVP-CEF-001', CURRENT_DATE + INTERVAL '365 days', 120, 9005)
ON CONFLICT (numero_lote, medicamento_id) DO UPDATE SET
    data_validade = EXCLUDED.data_validade,
    quantidade_inicial = EXCLUDED.quantidade_inicial;

INSERT INTO entradas_estoque (id, medicamento_id, lote_id, unidade_saude_id, quantidade, data_entrada, origem, documento, observacao)
VALUES
    (9001, 9001, 9001, 9001, 420, CURRENT_TIMESTAMP - INTERVAL '20 days', 'FORNECEDOR', 'MVP-NF-0001', 'Carga inicial MVP'),
    (9002, 9001, 9002, 9002, 80, CURRENT_TIMESTAMP - INTERVAL '18 days', 'FORNECEDOR', 'MVP-NF-0002', 'Lote proximo ao vencimento para testes'),
    (9003, 9002, 9003, 9001, 160, CURRENT_TIMESTAMP - INTERVAL '17 days', 'FORNECEDOR', 'MVP-NF-0003', 'Carga inicial MVP'),
    (9004, 9002, 9004, 9002, 45, CURRENT_TIMESTAMP - INTERVAL '16 days', 'FORNECEDOR', 'MVP-NF-0004', 'Lote de atencao para testes'),
    (9005, 9003, 9005, 9001, 760, CURRENT_TIMESTAMP - INTERVAL '15 days', 'FORNECEDOR', 'MVP-NF-0005', 'Carga inicial MVP'),
    (9006, 9004, 9006, 9003, 55, CURRENT_TIMESTAMP - INTERVAL '14 days', 'FORNECEDOR', 'MVP-NF-0006', 'Estoque critico para dashboard'),
    (9007, 9005, 9007, 9003, 90, CURRENT_TIMESTAMP - INTERVAL '13 days', 'FORNECEDOR', 'MVP-NF-0007', 'Carga inicial MVP')
ON CONFLICT (id) DO UPDATE SET
    quantidade = EXCLUDED.quantidade,
    data_entrada = EXCLUDED.data_entrada,
    observacao = EXCLUDED.observacao;

INSERT INTO saldo_lotes_estoque (lote_id, unidade_saude_id, quantidade_disponivel, data_ultima_movimentacao)
VALUES
    (9001, 9001, 260, CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (9002, 9002, 35, CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (9003, 9001, 70, CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (9004, 9002, 18, CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (9005, 9001, 640, CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (9006, 9003, 12, CURRENT_TIMESTAMP - INTERVAL '1 day'),
    (9007, 9003, 52, CURRENT_TIMESTAMP - INTERVAL '1 day')
ON CONFLICT (lote_id, unidade_saude_id) DO UPDATE SET
    quantidade_disponivel = EXCLUDED.quantidade_disponivel,
    data_ultima_movimentacao = EXCLUDED.data_ultima_movimentacao;

INSERT INTO saidas_estoque (id, medicamento_id, unidade_saude_id, quantidade_total, data_saida, motivo, observacao)
VALUES
    (9001, 9001, 9001, 90, CURRENT_TIMESTAMP - INTERVAL '12 days', 'DISPENSACAO', 'Consumo historico MVP'),
    (9002, 9001, 9002, 45, CURRENT_TIMESTAMP - INTERVAL '6 days', 'DISPENSACAO', 'Consumo historico MVP'),
    (9003, 9002, 9001, 60, CURRENT_TIMESTAMP - INTERVAL '10 days', 'DISPENSACAO', 'Consumo historico MVP'),
    (9004, 9002, 9002, 35, CURRENT_TIMESTAMP - INTERVAL '4 days', 'DISPENSACAO', 'Consumo historico MVP'),
    (9005, 9003, 9001, 120, CURRENT_TIMESTAMP - INTERVAL '8 days', 'DISPENSACAO', 'Consumo historico MVP'),
    (9006, 9004, 9003, 42, CURRENT_TIMESTAMP - INTERVAL '3 days', 'DISPENSACAO', 'Consumo historico MVP'),
    (9007, 9005, 9003, 18, CURRENT_TIMESTAMP - INTERVAL '2 days', 'DISPENSACAO', 'Consumo historico MVP')
ON CONFLICT (id) DO UPDATE SET
    quantidade_total = EXCLUDED.quantidade_total,
    data_saida = EXCLUDED.data_saida,
    observacao = EXCLUDED.observacao;

INSERT INTO saida_lotes_utilizados (id, saida_id, lote_id, numero_lote, quantidade_consumida)
VALUES
    (9001, 9001, 9001, 'MVP-DIP-001', 90),
    (9002, 9002, 9002, 'MVP-DIP-002', 45),
    (9003, 9003, 9003, 'MVP-AMX-001', 60),
    (9004, 9004, 9004, 'MVP-AMX-002', 35),
    (9005, 9005, 9005, 'MVP-LOS-001', 120),
    (9006, 9006, 9006, 'MVP-INS-001', 42),
    (9007, 9007, 9007, 'MVP-CEF-001', 18)
ON CONFLICT (id) DO UPDATE SET
    quantidade_consumida = EXCLUDED.quantidade_consumida;

INSERT INTO movimentacoes_estoque (id, tipo, medicamento_id, lote_id, unidade_saude_id, quantidade, saldo_apos_movimentacao, data_movimentacao, motivo)
VALUES
    (9001, 'ENTRADA', 9001, 9001, 9001, 420, 420, CURRENT_TIMESTAMP - INTERVAL '20 days', 'Carga inicial MVP'),
    (9002, 'ENTRADA', 9001, 9002, 9002, 80, 80, CURRENT_TIMESTAMP - INTERVAL '18 days', 'Carga inicial MVP'),
    (9003, 'ENTRADA', 9002, 9003, 9001, 160, 160, CURRENT_TIMESTAMP - INTERVAL '17 days', 'Carga inicial MVP'),
    (9004, 'ENTRADA', 9002, 9004, 9002, 45, 45, CURRENT_TIMESTAMP - INTERVAL '16 days', 'Carga inicial MVP'),
    (9005, 'ENTRADA', 9003, 9005, 9001, 760, 760, CURRENT_TIMESTAMP - INTERVAL '15 days', 'Carga inicial MVP'),
    (9006, 'ENTRADA', 9004, 9006, 9003, 55, 55, CURRENT_TIMESTAMP - INTERVAL '14 days', 'Carga inicial MVP'),
    (9007, 'ENTRADA', 9005, 9007, 9003, 90, 90, CURRENT_TIMESTAMP - INTERVAL '13 days', 'Carga inicial MVP'),
    (9008, 'SAIDA', 9001, 9001, 9001, 90, 330, CURRENT_TIMESTAMP - INTERVAL '12 days', 'Dispensacao MVP'),
    (9009, 'SAIDA', 9001, 9002, 9002, 45, 35, CURRENT_TIMESTAMP - INTERVAL '6 days', 'Dispensacao MVP'),
    (9010, 'SAIDA', 9002, 9003, 9001, 60, 100, CURRENT_TIMESTAMP - INTERVAL '10 days', 'Dispensacao MVP'),
    (9011, 'SAIDA', 9002, 9004, 9002, 35, 10, CURRENT_TIMESTAMP - INTERVAL '4 days', 'Dispensacao MVP'),
    (9012, 'SAIDA', 9003, 9005, 9001, 120, 640, CURRENT_TIMESTAMP - INTERVAL '8 days', 'Dispensacao MVP'),
    (9013, 'SAIDA', 9004, 9006, 9003, 42, 13, CURRENT_TIMESTAMP - INTERVAL '3 days', 'Dispensacao MVP'),
    (9014, 'SAIDA', 9005, 9007, 9003, 18, 72, CURRENT_TIMESTAMP - INTERVAL '2 days', 'Dispensacao MVP')
ON CONFLICT (id) DO UPDATE SET
    quantidade = EXCLUDED.quantidade,
    saldo_apos_movimentacao = EXCLUDED.saldo_apos_movimentacao,
    data_movimentacao = EXCLUDED.data_movimentacao,
    motivo = EXCLUDED.motivo;

SELECT setval(pg_get_serial_sequence('categorias', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9001) FROM categorias));
SELECT setval(pg_get_serial_sequence('unidades_medida', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9001) FROM unidades_medida));
SELECT setval(pg_get_serial_sequence('medicamentos', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9001) FROM medicamentos));
SELECT setval(pg_get_serial_sequence('lotes', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9001) FROM lotes));
SELECT setval(pg_get_serial_sequence('entradas_estoque', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9001) FROM entradas_estoque));
SELECT setval(pg_get_serial_sequence('saidas_estoque', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9001) FROM saidas_estoque));
SELECT setval(pg_get_serial_sequence('saida_lotes_utilizados', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9001) FROM saida_lotes_utilizados));
SELECT setval(pg_get_serial_sequence('movimentacoes_estoque', 'id'), (SELECT GREATEST(COALESCE(MAX(id), 1), 9014) FROM movimentacoes_estoque));