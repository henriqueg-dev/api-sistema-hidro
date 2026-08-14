-- PRUMADAS COZINHA
INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_9', '50mm', 'SEM_SANCA',
       'Prever 1 prumada, Ø50mm, interligando todas as pias dos pavimentos. O térreo deve ter prumada independente. Obs.: As prumadas podem ser unificadas no último pavimento para fins de ventilação primária, com aumento do diâmetro para Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_9' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_9', '75mm', 'ENTRE_1_2_E_1_8',
       'Prever 1 prumada individual, Ø75mm, para a pia do 2º pavimento. Prever 1 prumada, Ø75mm, interligando as pias do 3º ao 9º pavimento. O térreo deve ter prumada independente. Obs.: As prumadas podem ser unificadas no último pavimento para fins de ventilação primária.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_9' AND desconector = '75mm' AND condicao_sanca = 'ENTRE_1_2_E_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_9', '50mm', 'ACIMA_1_8',
       'Prever 1 prumada, Ø50mm, interligando as pias do 2º ao 9º pavimento. O térreo deve ter prumada independente. Prever ventilação secundária, Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_9' AND desconector = '50mm' AND condicao_sanca = 'ACIMA_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_16', '50mm', 'SEM_SANCA',
       'Prever 1 prumada individual, Ø50mm, do 2º ao 9º andar, com a pia independente em cada pavimento. Prever 1 prumada, Ø50mm, do 9º ao 16º andar. O térreo deve ter prumada independente. Obs.: As prumadas podem ser unificadas no último pavimento para fins de ventilação primária, com aumento do diâmetro para Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_16' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_16', '75mm', 'ENTRE_1_2_E_1_8',
       'Prever 1 prumada individual, Ø75mm, para o 2º pavimento. Prever 1 prumada, Ø75mm, do 3º pavimento em diante. O térreo deve ter prumada independente. Obs.: As prumadas podem ser unificadas no último pavimento para fins de ventilação primária.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_16' AND desconector = '75mm' AND condicao_sanca = 'ENTRE_1_2_E_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_16', '50mm', 'ACIMA_1_8',
       'Prever 1 prumada individual, Ø50mm, do 2º ao 9º andar, com a pia independente em cada pavimento. Prever 1 prumada, Ø50mm, do 9º ao 16º andar. O térreo deve ter prumada independente. Prever ventilação secundária, Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_16' AND desconector = '50mm' AND condicao_sanca = 'ACIMA_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_18', '50mm', 'SEM_SANCA',
       'Prever 1 prumada individual, Ø50mm, do 2º ao 9º andar, com a pia independente em cada pavimento. Prever 1 prumada, Ø75mm, do 9º ao 18º andar. O térreo deve ter prumada independente. Obs.: As prumadas podem ser unificadas no último pavimento para fins de ventilação primária, com aumento do diâmetro para Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_18' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_18', '75mm', 'ENTRE_1_2_E_1_8',
       'Prever 1 prumada individual, Ø75mm, para o 2º pavimento. Prever 1 prumada, Ø75mm, do 3º pavimento em diante. O térreo deve ter prumada independente. Obs.: As prumadas podem ser unificadas no último pavimento para fins de ventilação primária.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_18' AND desconector = '75mm' AND condicao_sanca = 'ENTRE_1_2_E_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ATE_18', '50mm', 'ACIMA_1_8',
       'Prever 1 prumada individual, Ø50mm, para a pia do 2º pavimento. Prever 1 prumada, Ø75mm, do 3º ao 18º andar. O térreo deve ter prumada independente. Prever ventilação secundária, Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ATE_18' AND desconector = '50mm' AND condicao_sanca = 'ACIMA_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'COZINHA', 'ACIMA_18', '50mm', 'SEM_SANCA',
       'Prever 1 prumada individual, Ø50mm, do 2º ao 9º andar, com a pia independente em cada pavimento. Prever 1 prumada, Ø75mm, do 9º pavimento em diante. A prumada de esgoto, com diâmetro Ø75mm, pode atender até 23 pavimentos, totalizando 69 UHCs. O térreo deve ter prumada independente. Prever ventilação secundária, Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'COZINHA' AND num_pavimentos = 'ACIMA_18' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

-- PRUMADAS ARS
INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_5', '50mm', 'SEM_SANCA',
       '1 prumada individual do 2º ao 5º pavimento para o tanque ou MLR, Ø50mm. 1 prumada do 2º ao 5º pavimento interligando ralo e MLR ou ralo e tanque, Ø50mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft.Prever 1 prumada individual, Ø50mm, do 2º ao 5º pavimento, destinada ao tanque ou à MLR. Prever 1 prumada, Ø50mm, do 2º ao 5º pavimento, interligando ralo e MLR ou ralo e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_5' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_9', '50mm', 'SEM_SANCA',
       'Prever 1 prumada, Ø50mm, do 2º ao 5º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø50mm, do 6º ao 9º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Obs.: As prumadas devem ser unificadas no último pavimento para fins de ventilação primária, com aumento do diâmetro para Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_9' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_9', '75mm', 'ENTRE_1_2_E_1_8',
       'Prever 1 prumada, Ø75mm, para o 2º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø75mm, do 3º ao 9º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Obs.: As prumadas devem ser unificadas no último pavimento para fins de ventilação primária, mantendo o diâmetro de Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_9' AND desconector = '75mm' AND condicao_sanca = 'ENTRE_1_2_E_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_9', '50mm', 'ACIMA_1_8',
       'Prever 1 prumada, Ø50mm, do 2º ao 5º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø50mm, do 6º ao 9º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Prever ventilação secundária com prumada de Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_9' AND desconector = '50mm' AND condicao_sanca = 'ACIMA_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_16', '50mm', 'SEM_SANCA',
       'Prever 1 prumada, Ø50mm, do 2º ao 5º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø75mm, do 6º ao 16º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Obs.: As prumadas devem ser unificadas no último pavimento para fins de ventilação primária, mantendo o diâmetro de Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_16' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_16', '75mm', 'ENTRE_1_2_E_1_8',
       'Prever 1 prumada, Ø75mm, para o 2º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø100mm, do 3º ao 16º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Obs.: As prumadas devem ser unificadas por meio de junção invertida com joelho de 45º no 3º pavimento, para fins de ventilação primária, mantendo o diâmetro de Ø100mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_16' AND desconector = '75mm' AND condicao_sanca = 'ENTRE_1_2_E_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_16', '50mm', 'ACIMA_1_8',
       'Prever 1 prumada, Ø50mm, do 2º ao 5º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø75mm, do 6º ao 16º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Prever ventilação secundária com prumada de Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_16' AND desconector = '50mm' AND condicao_sanca = 'ACIMA_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_18', '50mm', 'SEM_SANCA',
       'Prever 1 prumada, Ø50mm, para o 2º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø100mm, do 3º ao 18º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Obs.: As prumadas devem ser unificadas por meio de junção invertida com joelho de 45º no 3º pavimento, para fins de ventilação primária, mantendo o diâmetro de Ø100mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_18' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_18', '75mm', 'ENTRE_1_2_E_1_8',
       'Prever 1 prumada, Ø75mm, para o 2º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø100mm, do 3º ao 18º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Obs.: As prumadas devem ser unificadas por meio de junção invertida com joelho de 45º no 3º pavimento, para fins de ventilação primária, mantendo o diâmetro de Ø100mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_18' AND desconector = '75mm' AND condicao_sanca = 'ENTRE_1_2_E_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ATE_18', '50mm', 'ACIMA_1_8',
       'Prever 1 prumada, Ø50mm, para o 2º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø100mm, do 3º ao 18º pavimento, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Prever ventilação secundária com prumada de Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ATE_18' AND desconector = '50mm' AND condicao_sanca = 'ACIMA_1_8'
);

INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao)
SELECT 'ARS', 'ACIMA_18', '50mm', 'SEM_SANCA',
       'Prever 1 prumada individual, Ø50mm, do 2º ao 5º pavimento, interligando ralo, MLR e tanque. Prever 1 prumada, Ø100mm, do 6º pavimento em diante, interligando ralo, MLR e tanque. O térreo deve ter prumada independente. O ponto da MLR deve estar sempre localizado dentro do shaft. Prever ventilação secundária com prumada de Ø75mm.'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_prumada
    WHERE tipo = 'ARS' AND num_pavimentos = 'ACIMA_18' AND desconector = '50mm' AND condicao_sanca = 'SEM_SANCA'
);

-- USUÁRIO ADMIN INICIAL
-- Necessário porque POST /api/usuarios exige ROLE_ADMIN: sem este registro
-- não haveria como criar o primeiro usuário pela API.
-- Login: admin@hidro.com / Senha: admin123 (hash BCrypt, custo 10)
INSERT INTO tb_usuario (nome, email, senha, perfil, ativo, criado_em)
SELECT 'Administrador', 'admin@hidro.com',
       '$2a$10$Ue/9FKKtJ6pXsM4yqBgU5u4MyYOlU9qfNn.ekQ812/rVmkreSKxci',
       'ADMIN', true, now()
WHERE NOT EXISTS (
    SELECT 1 FROM tb_usuario WHERE email = 'admin@hidro.com'
);
