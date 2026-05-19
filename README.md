# 💧 Sistema de Automatização de Cálculos Hidráulicos

> TCC — Curso de Ciência da Computação | Centro Universitário de Formiga - UNIFOR/MG
>
> **Aluno:** Henrique Parreira Gonçalves
> **Orientador:** Prof. Me. Valter Ribeiro Lima Júnior

---

## 📋 Sobre o Projeto

Sistema web voltado à automação de processos em escritórios de engenharia hidráulica, permitindo o cadastro de empresas e empreendimentos, a consulta a tabelas normativas digitalizadas e a realização de cálculos técnicos por meio de formulários estruturados.

O projeto surgiu da necessidade real identificada na rotina de escritórios de engenharia que atuam no desenvolvimento de projetos de água, esgoto e drenagem pluvial — os quais lidam diariamente com planilhas complexas, documentos físicos e cálculos manuais suscetíveis a erros e retrabalho.

---

## 🏗️ Arquitetura

```
Frontend (Vue.js 3)  ⇄  Backend (Spring Boot 3)  ⇄  Banco de Dados (PostgreSQL)
```

---

## 🛠️ Tecnologias

### Backend
- **Java 21**
- **Spring Boot 3**
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
| **ENGENHEIRO** | Realiza e consulta cálculos, acessa empresas e empreendimentos |

> Usuários são criados manualmente pelo Administrador. Não há cadastro aberto.

---

## 📦 Módulos do Sistema

### 🔐 Autenticação
- Login com geração de token JWT
- Controle de acesso por perfil via `@PreAuthorize`
- Token com expiração de 24 horas

### 👤 Gestão de Usuários *(Admin)*
- Criar usuários (Admin e Engenheiro)
- Listar todos os usuários
- Ativar / desativar usuários

### 🏢 Empresas
- Cadastrar empresas (clientes do escritório)
- Listar e consultar empresas

### 🏗️ Empreendimentos
- Vinculados a uma empresa
- Campos: nome, tipo (Casa/Prédio/Galpão), número de pavimentos, endereço da obra, concessionária de água e esgoto
- Listagem por empresa

### 📋 Tabelas Normativas — Prumadas
Consulta digital das tabelas de prumadas de cozinha e ARS (substituição dos documentos físicos).

O engenheiro informa:
- Tipo (Cozinha ou ARS)
- Número de pavimentos
- Diâmetro do desconector (50mm ou 75mm)
- Condição da sanca (Sem sanca / Até 1,2m / Entre 1,2m e 1,8m / Acima de 1,8m)

O sistema retorna a descrição completa de como dividir as prumadas conforme a norma.

**Referências normativas:**
- ABNT NBR 8160 — Esgoto sanitário
- ABNT NBR 5626 — Instalações prediais de água fria
- ABNT NBR 10844 — Águas pluviais

### 🧮 Cálculos Hidráulicos *(em desenvolvimento)*
Formulários estruturados para os principais cálculos do escritório, com histórico salvo por empreendimento.

---

## 🗄️ Modelo de Dados

```
tb_usuario
tb_empresa
  └── tb_empreendimento
        └── tb_calculo (histórico)
tb_prumada (tabelas normativas)
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
git clone https://github.com/seu-usuario/sistema-hidro.git
```

2. Crie o banco de dados no PostgreSQL:
```sql
CREATE DATABASE hidro_db;
```

3. Configure o `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hidro_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
jwt.secret=sua-chave-secreta
jwt.expiration=86400000
```

4. Rode o projeto:
```bash
./gradlew bootRun
```

5. Insira o usuário admin no banco:
```sql
INSERT INTO tb_usuario (nome, email, senha, perfil, ativo)
VALUES ('Administrador', 'admin@hidro.com', '$2a$10$HASH_BCRYPT_AQUI', 'ADMIN', true);
```

---

## 📡 Endpoints Principais

### Autenticação
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Realiza login e retorna o token JWT |

### Usuários
| Método | Rota | Descrição | Perfil |
|---|---|---|---|
| POST | `/api/usuarios` | Criar usuário | ADMIN |
| GET | `/api/usuarios` | Listar usuários | ADMIN |
| PATCH | `/api/usuarios/{id}/status` | Ativar/desativar | ADMIN |

### Empresas
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/empresas` | Criar empresa |
| GET | `/api/empresas` | Listar empresas |
| GET | `/api/empresas/{id}` | Buscar por ID |

### Empreendimentos
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/empreendimentos` | Criar empreendimento |
| GET | `/api/empreendimentos/empresa/{id}` | Listar por empresa |

### Prumadas
| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/prumadas/consultar` | Consultar prumada por filtros |
| GET | `/api/prumadas?tipo=COZINHA` | Listar todas por tipo |

---

## 📐 Padrões de Desenvolvimento

- Classes Model com sufixo `Model` (ex: `UsuarioModel`)
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
