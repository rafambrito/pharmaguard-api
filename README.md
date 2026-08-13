# 💊 PharmaGuard

> **Gestão inteligente de estoque farmacêutico para reduzir desperdícios, prevenir rupturas e apoiar decisões na saúde pública.**

![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)
![License](https://img.shields.io/badge/License-A_Definir-lightgrey?style=for-the-badge)

---

## 🎯 Visão geral

O **PharmaGuard** é uma plataforma para gestão inteligente de estoques de medicamentos e insumos farmacêuticos.

A solução combina **controle operacional de estoque**, **gestão por lote e validade**, **estratégia FEFO (First Expire, First Out)** e um **motor de inteligência de estoque** baseado em análise estatística do consumo.

O objetivo é atuar sobre dois problemas críticos da gestão de medicamentos:

- 📉 **Desperdício**, especialmente por vencimento de lotes;
- 🚨 **Falta de medicamentos**, causada por decisões de reposição desconectadas do consumo real.

Em vez de apenas registrar entradas e saídas, o PharmaGuard busca transformar o histórico do estoque em **informação para tomada de decisão**.

---

## 💡 Proposta de valor

```text
                    PHARMAGUARD
                         │
          ┌──────────────┴──────────────┐
          │                             │
     DISPONIBILIDADE                DESPERDÍCIO
          │                             │
    Evitar rupturas              Evitar vencimentos
          │                             │
          └──────────────┬──────────────┘
                         │
                🧠 INTELIGÊNCIA
                         │
          ┌──────────────┼──────────────┐
          │              │              │
       Consumo        Validade        Reposição
          │              │              │
          └──────────────┼──────────────┘
                         │
                  Decisão baseada
                       em dados
```

### O sistema deve responder

> **Quanto temos?**  
> **Quanto estamos consumindo?**  
> **O que está próximo de vencer?**  
> **O que corre risco de faltar?**  
> **Quando devemos repor?**  
> **Quanto devemos comprar?**

---

# 🏗️ Arquitetura

O MVP será construído como um **monólito modular**, organizado segundo princípios de **Clean Architecture** e **Arquitetura Hexagonal**.

```text
                         ┌───────────────────────┐
                         │      Frontend         │
                         │       Vue 3           │
                         └───────────┬───────────┘
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │       REST API        │
                         │     Spring Boot       │
                         └───────────┬───────────┘
                                     │
                  ┌──────────────────┼──────────────────┐
                  │                  │                  │
                  ▼                  ▼                  ▼
             ┌─────────┐       ┌───────────┐      ┌───────────┐
             │  Auth   │       │ Inventory │      │ Supplier  │
             └─────────┘       └───────────┘      └───────────┘
                  │                  │                  │
                  └──────────────────┼──────────────────┘
                                     │
                                     ▼
                            ┌─────────────────┐
                            │   Analytics     │
                            │ 🧠 Intelligence  │
                            └────────┬────────┘
                                     │
                     ┌───────────────┴───────────────┐
                     │                               │
                     ▼                               ▼
              ┌─────────────┐                 ┌─────────────┐
              │  Scheduler  │                 │   Reports   │
              └─────────────┘                 └─────────────┘
                                     │
                                     ▼
                              ┌─────────────┐
                              │ PostgreSQL  │
                              └─────────────┘
```

## 🧩 Organização modular

```text
com.pharmaguard.api
│
├── auth
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── inventory
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── supplier
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── analytics
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── scheduler
│   ├── application
│   └── infrastructure
│
├── reports
│   ├── api
│   ├── application
│   └── infrastructure
│
└── shared
```

### Princípio de dependência

```text
API
 │
 ▼
Application
 │
 ▼
Domain
 ▲
 │
Infrastructure
```

O domínio não depende de frameworks ou detalhes de infraestrutura.

As integrações externas são realizadas por meio de **ports e adapters**, preservando o isolamento das regras de negócio.

---

# 🧠 Inteligência de estoque

O principal diferencial do PharmaGuard está no **Inventory Intelligence Engine**.

```text
                    HISTÓRICO DE MOVIMENTAÇÕES
                                │
                                ▼
                       ┌─────────────────┐
                       │ Motor Consumo   │
                       └────────┬────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │ Motor Estatístico     │
                    │                       │
                    │ • Média ajustada      │
                    │ • Desvio padrão       │
                    │ • Consumo diário      │
                    └───────────┬───────────┘
                                │
             ┌──────────────────┼──────────────────┐
             ▼                  ▼                  ▼
      ┌────────────┐     ┌────────────┐     ┌─────────────┐
      │ Validade   │     │ Disponib.  │     │ Reposição   │
      │ / FEFO     │     │ de estoque │     │             │
      └─────┬──────┘     └──────┬─────┘     └──────┬──────┘
            │                   │                   │
            └───────────────────┼───────────────────┘
                                ▼
                       ┌─────────────────┐
                       │ Recomendações   │
                       │ e Alertas       │
                       └─────────────────┘
```

### 📊 Indicadores

O motor poderá produzir informações como:

- 📈 consumo médio;
- 📐 desvio padrão;
- 🛡️ estoque de segurança;
- 📦 estoque mínimo e máximo;
- 🚨 risco de ruptura;
- ⏳ risco de vencimento;
- 🔄 recomendação de reposição;
- 💰 estoque em risco de perda;
- 🏥 criticidade do medicamento.

---

# 📦 Controle por lote e validade

O estoque do PharmaGuard é controlado por **produto e lote**.

```text
Produto
   │
   ├── Lote A ── Validade ── Quantidade
   │
   ├── Lote B ── Validade ── Quantidade
   │
   └── Lote C ── Validade ── Quantidade
```

Para dispensação, será utilizada a estratégia:

### **FEFO — First Expire, First Out**

```text
Solicitação de saída
        │
        ▼
Buscar lotes válidos
        │
        ▼
Ordenar por validade
        │
        ▼
Selecionar lote que vence primeiro
        │
        ▼
Baixar quantidade
        │
        ▼
Ainda falta?
   │          │
  SIM        NÃO
   │          │
   ▼          ▼
Próximo     Finalizar
lote
```

O objetivo é reduzir a probabilidade de perdas por vencimento e garantir que os lotes com menor prazo de validade sejam priorizados.

---

# 🚨 Sistema de alertas

O PharmaGuard contará com uma camada de alertas para transformar indicadores em ações.

```text
                  ANALYTICS
                     │
                     ▼
              ┌─────────────┐
              │ Alert Engine│
              └──────┬──────┘
                     │
        ┌────────────┼────────────┐
        ▼            ▼            ▼
     Validade     Estoque      Reposição
        │            │            │
        ▼            ▼            ▼
     🔴 Crítico   🔴 Ruptura   🟠 Alta
     🟠 Alto      🟠 Baixo     🟡 Média
     🟡 Médio
```

Os alertas poderão considerar:

- quantidade disponível;
- consumo histórico;
- criticidade do medicamento;
- velocidade de consumo;
- lead time do fornecedor;
- risco de ruptura;
- potencial de desperdício.

---

# 🛠️ Stack / Tecnologias

## Backend

<p>
<img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
<img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
<img src="https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security" />
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
</p>

## Banco de dados

<p>
<img src="https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
<img src="https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway" />
</p>

## Frontend

<p>
<img src="https://img.shields.io/badge/Vue.js-3-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white" alt="Vue 3" />
<img src="https://img.shields.io/badge/Bootstrap-5-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap" />
</p>

> O frontend será desenvolvido após a conclusão do MVP do backend.

## Qualidade e observabilidade

<p>
<img src="https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5" />
<img src="https://img.shields.io/badge/Mockito-Framework-78A641?style=for-the-badge&logo=mockito&logoColor=white" alt="Mockito" />
<img src="https://img.shields.io/badge/OpenAPI-3.0-6BA539?style=for-the-badge&logo=openapiinitiative&logoColor=white" alt="OpenAPI" />
<img src="https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</p>

---

# 🔐 Segurança

A autenticação e autorização serão implementadas utilizando **Spring Security** e **JWT**.

O acesso às funcionalidades será controlado de acordo com os perfis definidos pela aplicação.

Exemplos de perfis:

```text
ADMINISTRADOR
MÉDICO
ENFERMEIRO
FARMACÊUTICO
GESTOR
```

A arquitetura prevê separação entre:

- autenticação;
- autorização;
- regras de negócio;
- auditoria.

---

# 📋 Principais funcionalidades

### 👤 Autenticação e usuários

- Cadastro de usuários
- Perfis e permissões
- Login
- Autenticação JWT
- Controle de acesso

### 💊 Produtos

- Cadastro de medicamentos e insumos
- Categorias
- Unidades de medida
- Controle de medicamentos críticos

### 🏭 Fornecedores

- Cadastro de fornecedores
- Lead time
- Relacionamento produto/fornecedor

### 📥 Entradas

- Registro de entradas
- Fornecedor
- Nota/documento
- Lotes
- Validade
- Quantidade
- Valor

### 📤 Saídas

- Registro de dispensação
- Controle por lote
- FEFO
- Histórico de movimentações

### 📦 Estoque

- Saldo atual
- Estoque mínimo
- Estoque máximo
- Estoque de segurança
- Lotes disponíveis
- Produtos vencidos
- Produtos próximos do vencimento

### 🧠 Inteligência

- Análise de consumo
- Média ajustada
- Desvio padrão
- Análise de validade
- Risco de ruptura
- Risco de desperdício
- Recomendações de reposição

### 📊 Relatórios

- Estoque atual
- Consumo
- Produtos críticos
- Produtos próximos do vencimento
- Perdas
- Recomendações de compra
- Indicadores de estoque

---

# ⏰ Scheduler

O processamento automático será separado da camada de API.

```text
                    Scheduler
                       │
                       ▼
              Atualização diária
                       │
                       ▼
             Inventory Intelligence
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
       Consumo      Validade      Estoque
          │            │            │
          └────────────┼────────────┘
                       ▼
                 Recomendações
                       │
                       ▼
                    Alertas
```

O Scheduler será responsável por disparar os processos periódicos, enquanto as regras de negócio permanecerão nos módulos de aplicação e domínio.

---

# 🧪 Qualidade

O projeto seguirá uma estratégia de testes baseada na pirâmide de testes:

```text
                 ▲
                /                / E2E              /───────             / Integração             /───────────────           /    Unitários               /─────────────────────```

Prioridades:

- testes unitários para regras de domínio;
- testes dos casos de uso;
- testes de integração para persistência;
- testes dos endpoints;
- testes do motor estatístico;
- testes das regras FEFO;
- testes dos cálculos de estoque.

---

# 📚 Documentação da API

> 🚧 **A preencher após a implementação da API.**

### Swagger / OpenAPI

**Link:** `<!-- inserir URL -->`

### Documentação

**Link:** `<!-- inserir URL -->`

---

# 🔎 Testes

> 🚧 **A preencher após a implementação.**

## Execução local

```bash
# inserir comando
```

## Testes automatizados

```bash
# inserir comando
```

## Cobertura

**Relatório:** `<!-- inserir link ou caminho -->`

---

# 🚀 Execução local

## Pré-requisitos

- Java 21
- Maven
- Docker
- Docker Compose
- PostgreSQL

## Subindo a infraestrutura

```bash
docker compose up -d
```

## Executando a aplicação

```bash
./mvnw spring-boot:run
```

A documentação e os endpoints disponíveis serão adicionados após a implementação da API.

---

# 📁 Estrutura do projeto

```text
pharmaguard-api/
│
├── src/
│   ├── main/
│   │   ├── java/com/pharmaguard/api/
│   │   │   ├── auth/
│   │   │   ├── inventory/
│   │   │   ├── supplier/
│   │   │   ├── analytics/
│   │   │   ├── scheduler/
│   │   │   ├── reports/
│   │   │   └── shared/
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       └── application.yml
│   │
│   └── test/
│
├── docker-compose.yml
├── pom.xml
├── README.md
└── .gitignore
```

---

# 🗺️ Roadmap

O desenvolvimento seguirá uma abordagem incremental, priorizando primeiro a fundação e o **MVP do backend**.

```text
Fundação
   │
   ▼
Auth
   │
   ▼
Cadastros
   │
   ▼
Estoque + Lotes + FEFO
   │
   ▼
Motor de Inteligência
   │
   ▼
Scheduler + Alertas
   │
   ▼
Relatórios
   │
   ▼
                 ┌──────────────┐
                 │     MVP      │
                 └──────┬───────┘
                        │
                        ▼
                    Frontend
                      Vue 3
```

---

# 🔮 Evolução futura

A arquitetura foi planejada para permitir evolução sem antecipar complexidade desnecessária.

Possíveis evoluções:

- 🧩 Microfrontends;
- ☁️ implantação em cloud;
- 🔄 microsserviços;
- 📡 integração com sistemas externos;
- 📱 aplicação mobile;
- 🤖 modelos preditivos;
- 🏥 integração entre unidades de saúde;
- 📦 redistribuição inteligente entre unidades;
- 🔔 notificações por e-mail e outros canais;
- 📈 dashboards avançados.

A evolução para microsserviços não faz parte do MVP. A prioridade é entregar um **monólito modular coeso, testável e bem estruturado**.

---

# 📄 Licença

> A definir.

---

# 👨‍💻 Autor

**Rafael Mendonça Brito**

Projeto desenvolvido como parte da formação de Pós-Graduação / Tech Challenge.

---

<p align="center">
  💊 <strong>PharmaGuard</strong><br>
  <em>Protegendo estoques. Evitando desperdícios. Garantindo disponibilidade.</em>
</p>
