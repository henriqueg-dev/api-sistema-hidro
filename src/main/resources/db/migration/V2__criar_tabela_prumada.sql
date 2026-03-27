CREATE TABLE tb_prumada (
     id BIGSERIAL PRIMARY KEY,
     tipo VARCHAR(20) NOT NULL, -- 'COZINHA' ou 'ARS'
     num_pavimentos VARCHAR(100) NOT NULL,
     desconector VARCHAR(20) NOT NULL,
     condicao_sanca VARCHAR(50),
     descricao TEXT NOT NULL,
     ativo BOOLEAN NOT NULL DEFAULT TRUE
);