# 💊 PharmaGuard

> **Gestão inteligente de estoque farmacêutico para reduzir desperdícios, prevenir rupturas e apoiar decisões na saúde pública.**

![Status](https://img.shields.io/badge/Status-MVP%20Entregue-success?style=for-the-badge)
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
│   ├── adpaters
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── inventory
│   ├── adpaters
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── supplier
│   ├── adpaters
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── analytics
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── intelligence
│   ├── adpaters
│   │   └── out/ollama
│   ├── application
│   ├── domain
│   └── infrastructure
│
├── scheduler
│   ├── application
│   └── infrastructure
│
├── reports
│   ├── adpaters
│   ├── application
│   └── infrastructure
│
└── shared
```

### Princípio de dependência

```text
ADAPTERS
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

O motor produz informações como:

- 📈 consumo médio;
- 📐 desvio padrão;
- 🛡️ estoque de segurança;
- 📦 estoque mínimo e máximo;
- 🚨 risco de ruptura;
- ⏳ risco de vencimento;
- 🔄 recomendação de reposição;
- 💰 estoque em risco de perda;
- 🏥 criticidade do medicamento.

### 🤖 Diagnóstico assistido por IA (Ollama)

Além do motor estatístico determinístico, o módulo **`intelligence`** interpreta os indicadores calculados e gera um diagnóstico textual em linguagem natural, sem substituir as regras de negócio:

```text
Indicadores do motor estatístico
                │
                ▼
       Prompt estruturado
                │
                ▼
     GeradorInsightPort (hexagonal)
                │
                ▼
   Ollama (LLM local, modelo gemma3:4b)
                │
                ▼
     Explicação em linguagem natural
                │
                ▼
              PharmaGuard UI
```

- Execução local via **Ollama**, sem custo por token e sem envio de dados a serviços externos.
- Modelo configurável por variável de ambiente (`OLLAMA_MODEL`, padrão `gemma3:4b`).
- Chamada ao modelo protegida pelos padrões de resiliência do próprio projeto: **retry**, **circuit breaker**, **timeout** e **bulkhead**, evitando que uma falha ou lentidão do Ollama impacte o restante da API.
- Quando a IA está desabilitada (`intelligence.enabled=false`) ou indisponível, um `GeradorInsightPort` de fallback determinístico assume a geração do resumo, garantindo que a `pharmaguard-ui` sempre receba uma resposta.
- Health check dedicado (`OllamaHealthIndicator`) reporta a disponibilidade do modelo no endpoint de *actuator health*.

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
<img src="https://img.shields.io/badge/TypeScript-5-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" />
<img src="https://img.shields.io/badge/Vite-5-646CFF?style=for-the-badge&logo=vite&logoColor=white" alt="Vite" />
<img src="https://img.shields.io/badge/Pinia-State-FFD859?style=for-the-badge&logo=pinia&logoColor=black" alt="Pinia" />
</p>

> MVP entregue em [`pharmaguard-ui`](../pharmaguard-ui), consumindo esta API via REST/JWT. Consulte o README daquele projeto para detalhes de módulos e telas.

## Inteligência Artificial

<p>
<img src="https://img.shields.io/badge/Ollama-Local_LLM-000000?style=for-the-badge&logo=ollama&logoColor=white" alt="Ollama" />
<img src="https://img.shields.io/badge/Modelo-gemma3:4b-4B32C3?style=for-the-badge" alt="gemma3:4b" />
</p>

> Motor de IA local via [Ollama](https://ollama.com), sem dependência de provedores externos pagos. Modelo configurável por variável de ambiente (`OLLAMA_MODEL`, padrão `gemma3:4b`).

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
- Diagnóstico assistido por IA local (Ollama) com fallback determinístico

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

O `docker-compose.yml` já sobe o Ollama (`OLLAMA_BASE_URL`, `OLLAMA_MODEL=llama3.2:1b`) para o diagnóstico assistido por IA. Para rodar o modelo padrão da aplicação localmente:

```bash
ollama pull gemma3:4b
ollama serve
```

Caso o Ollama não esteja disponível, defina `intelligence.enabled=false` (ou a variável correspondente) para que o fallback determinístico assuma a geração dos diagnósticos.

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
│   │   │   ├── intelligence/
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

### 📡 Integrações com sistemas externos (demonstradas via mock na `pharmaguard-ui`)

O módulo **Integrações** da `pharmaguard-ui` já apresenta, de forma mockada, o próximo passo natural de evolução da API: expor os dados de estoque e receber dados regulatórios de sistemas federais. As integrações candidatas para implementação real são:

- **BNAFAR** — transmissão automatizada de posição de estoque, entradas, saídas e perdas de medicamentos para a Base Nacional de Dados da Assistência Farmacêutica, via web services (REST/SOAP) ou barramento do e-SUS, eliminando a digitação manual de relatórios federais.
- **CATMAT / TUSS** — consumo dos catálogos nacionais (CATMAT do Governo Federal e TUSS), padronizando a codificação de medicamentos e insumos conforme a RENAME e evitando duplicidade de cadastros.
- **RNDS (Rede Nacional de Dados em Saúde)** — conectividade com a plataforma do Ministério da Saúde para interoperabilidade de prontuários, vinculando a dispensação efetuada no PharmaGuard ao histórico de saúde do cidadão via CPF ou Cartão Nacional de Saúde (CNS).
- **ANVISA — Registros e Lotes** — consulta automatizada ao banco de dados da ANVISA para validação de registros de medicamentos, alertas de recolhimento preventivo (recall) e verificação de prazos de validade regulatórios na entrada das notas fiscais.
- **SNGPC/ANVISA** — escrituração de medicamentos controlados junto ao Sistema Nacional de Gerenciamento de Produtos Controlados.

### 🧾 Outras evoluções mockadas na `pharmaguard-ui`

- **Entrada por Nota Fiscal** — leitura de código de barras da DANFE e importação de XML/PDF da nota fiscal para gerar entradas de estoque automaticamente, reduzindo o lançamento manual de itens, NCM e unidade de medida.
- **Pedido de Compra** — fluxo de criação de pedidos de compra a fornecedores a partir de sugestões de reposição do motor de inteligência.

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
