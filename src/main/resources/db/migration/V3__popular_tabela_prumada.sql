-- PRUMADAS COZINHA
INSERT INTO tb_prumada (tipo, num_pavimentos, desconector, condicao_sanca, descricao) VALUES
    ('COZINHA', 'ATE_9', '50mm', 'SEM_SANCA',
     '1 prumada interligando todas as pias dos pavimentos, Ø50mm. Térreo independente. Obs.: As prumadas podem ser unificadas no último pavimento para ventilação primária aumentando o diâmetro para Ø75mm.'),

    ('COZINHA', 'ATE_9', '75mm', 'ENTRE_1_2_E_1_8',
     '1 prumada individualizando a pia do 2º pavimento, Ø75mm. 1 prumada interligando as pias do 3º ao 9º pavimento, Ø75mm. Térreo independente. Obs.: As prumadas podem ser unificadas no último pavimento para ventilação primária.'),

    ('COZINHA', 'ATE_9', '50mm', 'ACIMA_1_8',
     '1 prumada interligando as pias do 2º ao 9º pavimento, Ø50mm. Térreo independente. Prever ventilação secundária Ø75mm.'),

    ('COZINHA', 'ATE_16', '50mm', 'SEM_SANCA',
     '1 prumada do 2º ao 9º andar, a pia será independente, Ø50mm. 1 prumada do 9º andar até 16º, Ø50mm. Térreo independente. Obs.: As prumadas podem ser unificadas no último pavimento para ventilação primária aumentando o diâmetro para Ø75mm.'),

    ('COZINHA', 'ATE_16', '75mm', 'ENTRE_1_2_E_1_8',
     '1 prumada individualizando o 2º pavimento, Ø75mm. 1 prumada do 3º pavimento em diante, Ø75mm. Térreo independente. Obs.: As prumadas podem ser unificadas no último pavimento para ventilação primária.'),

    ('COZINHA', 'ATE_16', '50mm', 'ACIMA_1_8',
     '1 prumada do 2º ao 9º andar, a pia será independente, Ø50mm. 1 prumada do 9º andar até 16º, Ø50mm. Térreo independente. Prever ventilação secundária Ø75mm.'),

    ('COZINHA', 'ATE_18', '50mm', 'SEM_SANCA',
     '1 prumada do 2º ao 9º andar, a pia será independente, Ø50mm. 1 prumada do 9º andar até 18º, Ø75mm. Térreo independente. Obs.: As prumadas podem ser unificadas no último pavimento para ventilação primária aumentando o diâmetro para Ø75mm.'),

    ('COZINHA', 'ATE_18', '75mm', 'ENTRE_1_2_E_1_8',
     '1 prumada individualizando o 2º pavimento, Ø75mm. 1 prumada do 3º pavimento em diante, Ø75mm. Térreo independente. Obs.: As prumadas podem ser unificadas no último pavimento para ventilação primária.'),

    ('COZINHA', 'ATE_18', '50mm', 'ACIMA_1_8',
     '1 prumada individualizando a pia do 2º pavimento, Ø50mm. 1 prumada do 3º andar até 18º, Ø75mm. Térreo independente. Prever ventilação secundária Ø75mm.'),

    ('COZINHA', 'ACIMA_18', '50mm', 'SEM_SANCA',
     '1 prumada do 2º ao 9º andar, a pia será independente, Ø50mm. 1 prumada do 9º pavimento em diante, Ø75mm. A prumada de esgoto pode atender até 23 pavimentos totalizando 69 UHCs com Ø75mm. Térreo independente. Prever ventilação secundária Ø75mm.'),

-- PRUMADAS ARS
     ('ARS', 'ATE_5', '50mm', 'SEM_SANCA',
      '1 prumada individual do 2º ao 5º pavimento para o tanque ou MLR, Ø50mm. 1 prumada do 2º ao 5º pavimento interligando ralo e MLR ou ralo e tanque, Ø50mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft.'),

     ('ARS', 'ATE_9', '50mm', 'SEM_SANCA',
      '1 prumada do 2º ao 5º pavimento interligando ralo, MLR e tanque, Ø50mm. 1 prumada do 6º ao 9º pavimento interligando ralo, MLR e tanque, Ø50mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Obs.: As prumadas devem ser unificadas no último pavimento para ventilação primária aumentando o diâmetro para Ø75mm.'),

     ('ARS', 'ATE_9', '75mm', 'ENTRE_1_2_E_1_8',
      '1 prumada para o 2º pavimento interligando ralo, MLR e tanque, Ø75mm. 1 prumada do 3º ao 9º pavimento interligando ralo, MLR e tanque, Ø75mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Obs.: As prumadas devem ser unificadas no último pavimento para ventilação primária mantendo o diâmetro de Ø75mm.'),

     ('ARS', 'ATE_9', '50mm', 'ACIMA_1_8',
      '1 prumada do 2º ao 5º pavimento interligando ralo, MLR e tanque, Ø50mm. 1 prumada do 6º ao 9º pavimento interligando ralo, MLR e tanque, Ø50mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Prever ventilação secundária com prumada de Ø75mm.'),

     ('ARS', 'ATE_16', '50mm', 'SEM_SANCA',
      '1 prumada do 2º ao 5º pavimento interligando ralo, MLR e tanque, Ø50mm. 1 prumada do 6º ao 16º pavimento interligando ralo, MLR e tanque, Ø75mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Obs.: As prumadas devem ser unificadas no último pavimento para ventilação primária mantendo o diâmetro de Ø75mm.'),

     ('ARS', 'ATE_16', '75mm', 'ENTRE_1_2_E_1_8',
      '1 prumada do 2º pavimento interligando ralo, MLR e tanque, Ø75mm. 1 prumada do 3º ao 16º pavimento interligando ralo, MLR e tanque, Ø100mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Obs.: As prumadas devem ser unificadas com junção invertida mais joelho de 45º no 3º pavimento para ventilação primária mantendo o diâmetro de Ø100mm.'),

     ('ARS', 'ATE_16', '50mm', 'ACIMA_1_8',
      '1 prumada do 2º ao 5º pavimento interligando ralo, MLR e tanque, Ø50mm. 1 prumada do 6º ao 16º pavimento interligando ralo, MLR e tanque, Ø75mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Prever ventilação secundária com prumada de Ø75mm.'),

     ('ARS', 'ATE_18', '50mm', 'SEM_SANCA',
      '1 prumada do 2º pavimento interligando ralo, MLR e tanque, Ø50mm. 1 prumada do 3º ao 18º pavimento interligando ralo, MLR e tanque, Ø100mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Obs.: As prumadas devem ser unificadas com junção invertida mais joelho de 45º no 3º pavimento para ventilação primária mantendo o diâmetro de Ø100mm.'),

     ('ARS', 'ATE_18', '75mm', 'ENTRE_1_2_E_1_8',
      '1 prumada do 2º pavimento interligando ralo, MLR e tanque, Ø75mm. 1 prumada do 3º ao 18º pavimento interligando ralo, MLR e tanque, Ø100mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Obs.: As prumadas devem ser unificadas com junção invertida mais joelho de 45º no 3º pavimento para ventilação primária mantendo o diâmetro de Ø100mm.'),

     ('ARS', 'ATE_18', '50mm', 'ACIMA_1_8',
      '1 prumada do 2º pavimento interligando ralo, MLR e tanque, Ø50mm. 1 prumada do 3º ao 18º pavimento interligando ralo, MLR e tanque, Ø100mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Prever ventilação secundária com prumada de Ø75mm.'),

     ('ARS', 'ACIMA_18', '50mm', 'SEM_SANCA',
      '1 prumada do 2º ao 5º pavimento individualizada interligando ralo, MLR e tanque, Ø50mm. 1 prumada do 6º pavimento em diante interligando ralo, MLR e tanque, Ø100mm. Térreo independente. Ponto da MLR deve estar sempre dentro do shaft. Prever ventilação secundária com prumada de Ø75mm.');