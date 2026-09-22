ALTER TABLE tb_codigo_verificacao DROP CONSTRAINT tb_codigo_verificacao_finalidade_check;

ALTER TABLE tb_codigo_verificacao
    ADD CONSTRAINT tb_codigo_verificacao_finalidade_check
    CHECK (finalidade IN ('RECUPERACAO_SENHA', 'CONVITE', 'ALTERACAO_SENHA'));
