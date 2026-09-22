# Criar um escritório novo (manual)

Não existe mais cadastro público. Todo escritório novo é criado assim, na mão.

## 1. Clonar o banco de tenant

Escolha um `hidro_tenant_N` já existente como template (schema + prumadas já aplicados) e
clone-o. Com o Postgres sem ninguém conectado nos dois bancos:

```sql
CREATE DATABASE hidro_tenant_<novo_id> TEMPLATE hidro_tenant_<id_template>;
```

Se o `CREATE DATABASE ... TEMPLATE` reclamar de conexões ativas no template, derrube-as
primeiro (`SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'hidro_tenant_<id_template>';`)
e rode de novo.

⚠️ A clonagem traz os usuários do template junto (`tb_usuario`). Se o escritório novo não é
uma cópia de teste, limpe/ajuste `tb_usuario` no banco clonado antes de liberar acesso.

## 2. Inserir as linhas no catálogo (`hidro_db`)

`conta_id` tem que bater com o `<novo_id>` usado no nome do banco no passo 1.

```sql
BEGIN;

INSERT INTO tb_conta (id, nome_escritorio, criado_em)
VALUES (<novo_id>, '<Nome do Escritório>', now());

SELECT setval('tb_conta_id_seq', (SELECT MAX(id) FROM tb_conta), true);

-- status ATIVA libera acesso na hora, sem passar pelo fluxo de pagamento.
-- Pra forçar o fluxo de PIX normal, use status PENDENTE e plano NULL em vez disso.
INSERT INTO tb_assinatura (conta_id, plano, status, expira_em, criado_em, atualizado_em)
VALUES (<novo_id>, 'ESCRITORIO', 'ATIVA', now() + interval '1 month', now(), now());

-- um INSERT por e-mail que deve conseguir logar nesse escritório
-- (tem que já existir em tb_usuario do banco clonado, ou ser criado lá antes).
INSERT INTO tb_usuario_indice (email, conta_id) VALUES
  ('email@exemplo.com', <novo_id>);

COMMIT;
```

`setval` evita colisão: sem ele, o próximo `INSERT` sem `id` explícito pode tentar reusar
um id já ocupado, porque a sequence não sabe que você inseriu na mão.

## 3. Reiniciar o backend

`TenantBootstrapRunner` religa o `DataSource` de toda conta em `tb_conta` no boot — sem
reiniciar, a conta nova fica invisível pro roteador mesmo já estando no catálogo.

## 4. Testar

Login com um dos e-mails inseridos no passo 2. Se der 402 em tudo, é sinal de que a
assinatura não ficou `ATIVA` (confira `tb_assinatura`) ou que o restart do passo 3 não
rodou.

## Erros comuns (já vividos)

- **Esquecer o `tb_assinatura`**: `AssinaturaFiltro` trata "sem assinatura" como `EXPIRADA`
  e bloqueia tudo com 402, sem mensagem específica.
- **Editar uma migration já aplicada** (`db/catalogo/V1__schema_inicial.sql` ou
  `db/tenant/V2__seed_prumadas.sql`): Flyway grava o checksum de cada migration aplicada;
  mudar o arquivo depois quebra o boot com "Migration checksum mismatch". Se o banco não
  tem nada de valor, é mais rápido dropar e deixar o Flyway recriar do zero do que reparar.
