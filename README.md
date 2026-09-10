# 💧 Sistema de Automatização de Cálculos Hidráulicos

> TCC — Curso de Ciência da Computação | Centro Universitário de Formiga - UNIFOR/MG
>
> **Aluno:** Henrique Parreira Gonçalves
> **Orientador:** Prof. Me. Valter Ribeiro Lima Júnior

---

## 📋 Sobre o Projeto

Sistema web voltado à automação de processos em escritórios de engenharia hidráulica, permitindo o cadastro de clientes e empreendimentos, a consulta a tabelas normativas digitalizadas e a realização de cálculos técnicos por meio de formulários estruturados.

O projeto surgiu da necessidade real identificada na rotina de escritórios de engenharia que atuam no desenvolvimento de projetos de água, esgoto e drenagem pluvial — os quais lidam diariamente com planilhas complexas, documentos físicos e cálculos manuais suscetíveis a erros e retrabalho.

---

## 🏗️ Arquitetura

```
Frontend (Vue.js 3)  ⇄  Backend (Spring Boot 4)  ⇄  Banco de Dados (PostgreSQL)
```

---

## 🛠️ Tecnologias

### Backend
- **Java 21**
- **Spring Boot 4**
- **Spring Security** com autenticação via **JWT**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL 16**
- **Gradle**
- **Lombok**

### Frontend
- **Vue.js 3** (Composition API)
- **Vue Router**
- **Pinia**
- **Axios**

---

## 👥 Perfis de Usuário

| Perfil | Permissões |
|---|---|
| **ADMIN** | Gerencia usuários, acessa tudo |
| **ENGENHEIRO** | Realiza e consulta cálculos, acessa clientes e empreendimentos |

> Usuários são convidados por e-mail pelo Administrador; o próprio convidado define a senha. Não há cadastro aberto.

---

## 📦 Módulos do Sistema

### 🔐 Autenticação
- Login com geração de token JWT (expiração configurável, padrão 30 minutos)
- Controle de acesso por perfil via `@PreAuthorize`
- Recuperação de senha por código enviado ao e-mail (Resend)

### 👤 Gestão de Usuários *(Admin)*
- Convidar usuários por e-mail (Admin e Engenheiro) — o convidado define a própria senha
- Reenviar convite pendente
- Listar todos os usuários
- Ativar / desativar usuários
- Cada usuário troca a própria senha

### 🏢 Clientes
- Cadastrar, listar (com busca), buscar por ID, editar e excluir clientes do escritório

### 🏗️ Empreendimentos
- Vinculados a um cliente
- Campos: nome, tipo (Casa/Prédio/Galpão), número de pavimentos, endereço da obra, concessionária de água e esgoto
- Listagem por cliente (com busca), busca por ID, edição e exclusão

### 📋 Tabelas Normativas — Prumadas
Consulta digital das tabelas de prumadas de cozinha e ARS (substituição dos documentos físicos).

O engenheiro informa:
- Tipo (Cozinha ou ARS)
- Número de pavimentos
- Diâmetro do desconector (50mm ou 75mm)
- Condição da sanca (Sem sanca / Até 1,2m / Entre 1,2m e 1,8m / Acima de 1,8m)

O sistema retorna a descrição completa de como dividir as prumadas conforme a norma.

### 🧮 Cálculos Hidráulicos
Um CRUD por empreendimento para cada cálculo, com memorial de cálculo em PDF (exceto Caixa de Gordura):

| Cálculo | Norma | Observação |
|---|---|---|
| Ramal predial e hidrômetro | NBR 5626 | Um por empreendimento |
| Vazão predial (reservatórios) | NBR 5626 | Um por empreendimento — coeficientes K1/K2, reserva dividida 60/40 |
| Tanque séptico | NBR 7229 (mantida pela NBR 17076) | |
| Caixa de gordura e sabão | Fórmula V = 2×N + 20 | Até dois por empreendimento |
| Piscina — recirculação | NBR 10339 | Vários por empreendimento; trechos de tubulação e conexões para a perda de carga |

### 💰 Orçamentos *(Admin)*
- Orçamento comercial por cliente: quantidade, valor unitário e total, validade, status (rascunho/enviado/aprovado/recusado)
- Ao aprovar, gera automaticamente o empreendimento correspondente
- Geração do orçamento em PDF
- Não visível para o perfil Engenheiro

### 📜 Auditoria *(Admin)*
- Linha do tempo de todas as alterações do sistema (Hibernate Envers), com busca
- Histórico detalhado por entidade e registro: quem alterou, quando e o que mudou

### 🤖 Assistente de IA
- Chat técnico (Claude) por usuário, com histórico de conversas
- Pode ser vinculado a um empreendimento — os dados do projeto entram como contexto nas perguntas

**Referências normativas:**
- ABNT NBR 5626 — Instalações prediais de água fria
- ABNT NBR 7229 / NBR 17076 — Tanques sépticos
- ABNT NBR 8160 — Esgoto sanitário
- ABNT NBR 10339 — Piscinas (recirculação, tratamento e higienização)
- ABNT NBR 10844 — Águas pluviais

---

## 🗄️ Modelo de Dados

```text
tb_usuario
  ├── tb_codigo_verificacao   (convite / recuperação de senha)
  └── tb_conversa             (assistente de IA)
        └── tb_mensagem

tb_cliente
  └── tb_empreendimento
        ├── tb_ramal_predial
        ├── tb_vazao_predial
        ├── tb_tanque_septico
        ├── tb_caixa_gordura
        └── tb_piscina
              └── tb_trecho_piscina
                    └── tb_conexao_trecho

tb_orcamento        (ligado a tb_cliente; aprovado gera um tb_empreendimento)
tb_prumada          (tabela normativa, independente)
tb_revisao          (auditoria — Hibernate Envers; toda tabela acima tem uma <tabela>_aud)
```

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos
- Java 21
- PostgreSQL 16
- Node.js (para o frontend)
- Gradle

### Backend

1. Clone o repositório:

   ```bash
   git clone https://github.com/henriqueg-dev/sistema-hidro.git
   ```

1. Crie o banco de dados no PostgreSQL:

   ```sql
   CREATE DATABASE hidro_db;
   ```

1. Defina o profile ativo. Para execução local basta o profile `dev`, que já é o padrão quando `SPRING_PROFILES_ACTIVE` não está definida:

   ```bash
   export SPRING_PROFILES_ACTIVE=dev
   ```

   O profile `dev` já traz os valores locais de banco (`localhost/hidro_db`, `postgres/postgres`) e um segredo JWT de desenvolvimento.

1. Configure as credenciais via variáveis de ambiente (evita segredos em texto plano no repositório). Em produção, todas são obrigatórias:

   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   export DB_URL=jdbc:postgresql://localhost:5432/hidro_db
   export DB_USERNAME=seu_usuario
   export DB_PASSWORD=sua_senha
   export JWT_SECRET=troque-por-uma-chave-aleatoria-de-no-minimo-32-bytes
   export JWT_EXPIRATION=86400000
   export CORS_ALLOWED_ORIGINS=https://app.seu-dominio.com
   ```

   > O `JWT_SECRET` precisa ter ao menos 32 bytes (256 bits) para o algoritmo HMAC-SHA256.

1. Opcional — variáveis para os módulos de Assistente de IA e e-mail transacional. Sem elas a aplicação sobe normalmente; só essas funcionalidades ficam indisponíveis:

   ```bash
   export ANTHROPIC_API_KEY=sua-chave-da-anthropic
   export RESEND_API_KEY=re_sua_chave_do_resend
   export RESEND_FROM=naoresponda@seu-dominio.com.br
   export FRONTEND_URL=https://app.seu-dominio.com
   ```

   > `RESEND_FROM` precisa ser um remetente de um domínio verificado no Resend; sem configurar, cai no sandbox `onboarding@resend.dev`, que só entrega e-mail para o endereço da própria conta Resend.

1. Rode o projeto:

   ```bash
   ./gradlew bootRun
   ```

1. Insira o usuário admin no banco:

   ```sql
   INSERT INTO tb_usuario (nome, email, senha, perfil, ativo, convite_pendente)
   VALUES ('Administrador', 'admin@hidro.com', '$2a$10$HASH_BCRYPT_AQUI', 'ADMIN', true, false);
   ```

---

## 📡 Endpoints Principais

### Autenticação
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Realiza login e retorna o token JWT |
| POST | `/api/auth/esqueci-senha` | Envia por e-mail um código para redefinir a senha |
| POST | `/api/auth/redefinir-senha` | Confirma o código (recuperação ou convite) e define a senha |

### Usuários
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| POST | `/api/usuarios` | Convidar usuário (envia e-mail com código; a senha é definida pelo convidado) | ADMIN |
| GET | `/api/usuarios` | Listar usuários | ADMIN |
| PATCH | `/api/usuarios/{id}/status` | Ativar/desativar | ADMIN |
| POST | `/api/usuarios/{id}/reenviar-convite` | Reenvia o e-mail de convite (convite ainda pendente) | ADMIN |
| PATCH | `/api/usuarios/me/senha` | Troca a própria senha | ADMIN, ENGENHEIRO |

### Clientes
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/clientes` | Criar cliente |
| GET | `/api/clientes?busca=` | Listar clientes (busca opcional) |
| GET | `/api/clientes/{id}` | Buscar por ID |
| PUT | `/api/clientes/{id}` | Atualizar |
| DELETE | `/api/clientes/{id}` | Excluir |

### Empreendimentos
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/empreendimentos` | Criar empreendimento |
| GET | `/api/empreendimentos/cliente/{clienteId}?busca=` | Listar por cliente (busca opcional) |
| GET | `/api/empreendimentos/{id}` | Buscar por ID |
| PUT | `/api/empreendimentos/{id}` | Atualizar |
| DELETE | `/api/empreendimentos/{id}` | Excluir |

### Prumadas
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/prumadas/consultar` | Consultar prumada por filtros |
| GET | `/api/prumadas?tipo=COZINHA` | Listar todas por tipo |

### Cálculos hidráulicos
Mesmo padrão de rotas para os cinco cálculos, trocando só o prefixo:

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/{recurso}` | Criar |
| PUT | `/api/{recurso}/{id}` | Atualizar |
| DELETE | `/api/{recurso}/{id}` | Excluir |
| GET | `/api/{recurso}/empreendimento/{empreendimentoId}` | Listar por empreendimento |
| GET | `/api/{recurso}/{id}/memorial.pdf` | Baixar memorial de cálculo em PDF *(exceto `caixas-gordura`)* |

Prefixos (`{recurso}`): `ramais-prediais`, `tanques-septicos`, `vazoes-prediais`, `caixas-gordura`, `piscinas`. A Piscina também tem `GET /api/piscinas/{id}` (buscar por ID) e `GET /api/piscinas/referencias` (tabelas de apoio da NBR 10339 para a tela).

### Orçamentos
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| POST | `/api/orcamentos` | Criar orçamento | ADMIN |
| GET | `/api/orcamentos` | Listar orçamentos | ADMIN |
| GET | `/api/orcamentos/{id}` | Buscar por ID | ADMIN |
| PUT | `/api/orcamentos/{id}` | Atualizar (aprovar gera o empreendimento) | ADMIN |
| DELETE | `/api/orcamentos/{id}` | Excluir | ADMIN |
| GET | `/api/orcamentos/{id}/orcamento.pdf` | Baixar orçamento em PDF | ADMIN |

### Auditoria
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| GET | `/api/auditoria?busca=` | Linha do tempo de alterações | ADMIN |
| GET | `/api/auditoria/{entidade}/{id}` | Histórico de um registro | ADMIN |

### Assistente de IA
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/assistente/status` | Se a chave da IA está configurada |
| GET | `/api/assistente/conversas` | Listar conversas do usuário autenticado |
| GET | `/api/assistente/conversas/{id}` | Detalhe de uma conversa |
| POST | `/api/assistente/conversas` | Criar conversa (primeira mensagem) |
| POST | `/api/assistente/conversas/{id}/mensagens` | Enviar mensagem numa conversa existente |
| DELETE | `/api/assistente/conversas/{id}` | Excluir conversa |

---

## 📐 Padrões de Desenvolvimento

- Entidades JPA com sufixo `Entity` (ex: `UsuarioEntity`)
- DTOs com sufixo `DTO` (ex: `UsuarioRequestDTO`)
- **Proibido** uso de `@Data` do Lombok — usar `@Getter` e `@Setter`
- **Proibido** uso de `java.util.Date` — usar `java.time`
- Campos booleanos com `@Builder.Default`
- Regras de negócio isoladas na camada `Service`
- Retorno ao frontend sempre via DTOs, nunca entidades
- `@ManyToOne(fetch = FetchType.LAZY)` obrigatório
- Controle de acesso via `@PreAuthorize` no Controller
- Código e nomenclatura em **português**

---

## 📄 Licença

Projeto acadêmico desenvolvido como Trabalho de Conclusão de Curso — UNIFOR/MG, 2026.
