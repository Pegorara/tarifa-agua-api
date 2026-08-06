-- Dados de exemplo para a API de Tabela Tarifária de Água.
-- Execução manual e opcional — não é carregado automaticamente pela aplicação.
-- Pressupõe que o schema já foi criado (via Hibernate ddl-auto=update ou sql/schema.sql).

-- Categorias exigidas pelo enunciado (mínimo de 4)
INSERT INTO categoria_consumidor (nome) VALUES
                                            ('COMERCIAL'),
                                            ('INDUSTRIAL'),
                                            ('PARTICULAR'),
                                            ('PUBLICO')
    ON CONFLICT (nome) DO NOTHING;

-- Tabela tarifária vigente + faixas de consumo por categoria
DO $$
DECLARE
v_tabela_id BIGINT;
BEGIN
INSERT INTO tabela_tarifaria (nome, data_vigencia, ativo)
VALUES ('Tabela Padrão 2026', '2026-01-01', true)
    RETURNING id INTO v_tabela_id;

-- COMERCIAL
INSERT INTO faixa_consumo (inicio, fim, valor_unitario, categoria_id, tabela_id)
SELECT v.inicio, v.fim, v.valor_unitario, c.id, v_tabela_id
FROM categoria_consumidor c,
     (VALUES (0, 10, 1.20), (11, 20, 2.20), (21, 30, 3.20), (31, 99999, 4.20))
         AS v(inicio, fim, valor_unitario)
WHERE c.nome = 'COMERCIAL';

-- INDUSTRIAL (mesmos valores do exemplo do enunciado nas 2 primeiras faixas)
INSERT INTO faixa_consumo (inicio, fim, valor_unitario, categoria_id, tabela_id)
SELECT v.inicio, v.fim, v.valor_unitario, c.id, v_tabela_id
FROM categoria_consumidor c,
     (VALUES (0, 10, 1.00), (11, 20, 2.00), (21, 30, 3.00), (31, 99999, 4.00))
         AS v(inicio, fim, valor_unitario)
WHERE c.nome = 'INDUSTRIAL';

-- PARTICULAR
INSERT INTO faixa_consumo (inicio, fim, valor_unitario, categoria_id, tabela_id)
SELECT v.inicio, v.fim, v.valor_unitario, c.id, v_tabela_id
FROM categoria_consumidor c,
     (VALUES (0, 10, 0.80), (11, 20, 1.50), (21, 30, 2.50), (31, 99999, 3.50))
         AS v(inicio, fim, valor_unitario)
WHERE c.nome = 'PARTICULAR';

-- PUBLICO
INSERT INTO faixa_consumo (inicio, fim, valor_unitario, categoria_id, tabela_id)
SELECT v.inicio, v.fim, v.valor_unitario, c.id, v_tabela_id
FROM categoria_consumidor c,
     (VALUES (0, 10, 0.90), (11, 20, 1.80), (21, 30, 2.80), (31, 99999, 3.80))
         AS v(inicio, fim, valor_unitario)
WHERE c.nome = 'PUBLICO';
END $$;