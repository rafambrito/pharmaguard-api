# Módulo Estoque - Planejamento da Implementação

## 1. Contexto

Este módulo faz parte do MVP do PharmaGuard e está alinhado ao roadmap do projeto, especialmente à Etapa 4 do plano detalhado:

- Controle de estoque
- Entradas
- Saídas
- Movimentações
- Saldo
- Histórico
- Produtos vencendo

O objetivo do módulo é manter o registro real do estoque farmacêutico, controlar movimentações por lote, respeitar a regra FEFO e fornecer dados confiáveis para os módulos posteriores de relatórios, inteligência e alertas.

O módulo é executado dentro da arquitetura modular do monólito, seguindo os princípios do projeto:

- Clean Architecture
- Arquitetura Hexagonal (ports and adapters)
- Dependência em sentido de camadas: adapters.in → Application → Domain → adapters.out
- Domínio isolado de frameworks e infraestrutura

## 2. Visão de negócio

O módulo de estoque deve responder às perguntas essenciais do sistema:

- Quanto temos em estoque?
- O que está disponível para uso?
- Qual lote deve ser utilizado primeiro?
- O que está próximo do vencimento?
- O que está em risco de ruptura?
- Quanto foi consumido no período?

A principal funcionalidade operacional deste módulo é garantir que as entradas e saídas de medicamentos sejam processadas de forma consistente, com rastreabilidade por produto, lote e validade.

## 3. Escopo do módulo

### 3.1 Escopo incluído

- Cadastro e consulta de entradas de estoque
- Cadastro e consulta de saídas de estoque
- Controle por lote e validade
- Registro de movimentações
- Cálculo de saldo atual por medicamento e lote
- Histórico de movimentações por produto
- Identificação de produtos vencidos e próximos do vencimento
- Ordem de consumo por FEFO
- Validações de negócio do módulo
- Testes unitários e de integração do módulo

### 3.2 Escopo excluído

Para manter o foco do MVP e evitar dispersão, este plano não inclui:

- implementação do frontend
- autenticação e autorização em nível de módulo (já tratadas no módulo auth)
- relatórios analíticos avançados
- motor estatístico de reposição e previsão
- scheduler e alertas automáticos
- integrações com sistemas externos
- migração para microsserviços
- refatorações fora do módulo de estoque

> Recomendação: não implementar, nem executar qualquer tarefa fora do escopo descrito acima.

## 4. Dependências do módulo

O módulo de estoque depende diretamente dos módulos já concluídos:

- medicamentos
  - Categoria
  - Unidade de Medida
  - Medicamento
  - Lote
  - Validade
- auth
  - identificação do usuário responsável pela operação
  - auditoria e rastreabilidade operacional
- shared
  - exceptions
  - message.properties
  - padrões de resposta da API

## 5. Arquitetura do módulo

### 5.1 Estrutura proposta

```text
inventory/
├── adapters.in/
│   ├── controller/
│   ├── dto/
│   ├── mapper/
│   └── openapi/
├── application/
│   ├── usecase/
│   ├── port/input/
│   ├── port/output/
│   └── service/
├── domain/
│   ├── entity/
│   ├── valueobject/
│   ├── exception/
│   └── rule/
└── adapters.out/
    ├── persistence/
    ├── jpa/
    ├── repository/
    └── adapter/
```

### 5.2 Fluxo principal

1. A API recebe a requisição de entrada ou saída.
2. O caso de uso do módulo valida regras de negócio.
3. O domínio aplica as regras de estoque, validade e FEFO.
4. O adapter de persistência (adapters.out) grava ou consulta os dados.
5. A API retorna resposta padronizada e consistente.

## 6. Regras de negócio do módulo

### 6.1 Entrada de estoque

- Toda entrada deve estar vinculada a um medicamento e a um lote válido.
- O lote deve possuir número identificador, validade e quantidade inicial.
- A quantidade de entrada deve ser maior que zero.
- A operação deve registrar origem da entrada, como fornecedor ou ajuste interno.
- A entrada deve atualizar o saldo do produto e do lote correspondente.

### 6.2 Saída de estoque

- Toda saída deve estar vinculada a um medicamento.
- A saída deve controlar quantidade liberada por lote.
- O sistema deve priorizar o lote com menor data de validade primeiro.
- Não pode ocorrer saída maior que a quantidade disponível no lote.
- A operação deve registrar motivo da saída, como dispensação, perda ou ajuste.

### 6.3 FEFO

- A regra FEFO deve definir a ordem de consumo por validade.
- Lotes vencidos ou próximos do vencimento devem receber prioridade de atenção.
- A seleção de lote para baixa deve respeitar disponibilidade e validade.
- Caso um lote seja insuficiente, o processo deve continuar para o próximo lote válido.

### 6.4 Movimentação

- Cada operação deve gerar evento de movimentação.
- A movimentação deve manter histórico com:
  - tipo
  - quantidade
  - lote
  - medicamento
  - data
  - usuário responsável
  - motivo

### 6.5 Saldo e histórico

- O saldo deve ser calculado a partir do histórico de movimentações.
- O sistema deve permitir consulta por medicamento e por lote.
- O histórico deve ser auditável e rastreável.

### 6.6 Vencimento

- O módulo deve permitir identificar lotes vencidos.
- Deve permitir identificar lotes próximos do vencimento.
- Essas regras devem estar preparadas para uso em alertas e relatórios futuros.

## 7. Entidades e conceitos principais

### 7.1 EntradaEstoque

- id
- medicamento
- lote
- quantidade
- dataEntrada
- origem
- documento
- valor unitário ou custo (se necessário ao MVP)
- observação

### 7.2 SaidaEstoque

- id
- medicamento
- lista de lotes utilizados
- quantidade total
- dataSaida
- motivo
- observação
- usuário responsável

### 7.3 MovimentacaoEstoque

- id
- tipo: ENTRADA / SAIDA / AJUSTE
- medicamento
- lote
- quantidade
- saldo após movimentação
- data
- motivo

### 7.4 EstoqueAtual

- medicamento
- quantidadeDisponivel
- quantidadeReservada
- lote(s) ativos
- validade mais próxima

## 8. Divisão de tarefas

### T0 - API First e contrato

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T4.0 | Definir contrato OpenAPI do módulo de estoque | arquivo de contrato do módulo | Planejado |
| T4.0.1 | Definir schemas de entrada, saída e movimentação | DTOs e contracts do módulo | Planejado |
| T4.0.2 | Padronizar respostas e erros | contratos de sucesso e erro | Planejado |

### T1 - Modelagem do domínio

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T4.1 | Criar entidades de domínio do estoque | entidades para entrada, saída e movimentação | Planejado |
| T4.1.1 | Definir regras de quantidade e validade | validações puras no domínio | Planejado |
| T4.1.2 | Definir regra de FEFO | lógica de priorização de lotes | Planejado |
| T4.1.3 | Definir regra de saldo e lote válido | comportamento do domínio para controle de estoque | Planejado |

### T2 - Casos de uso

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T4.2 | Criar casos de uso de entrada | fluxo de cadastro e consulta de entradas | Planejado |
| T4.2.1 | Criar casos de uso de saída | fluxo de baixa e validações | Planejado |
| T4.2.2 | Criar casos de uso de consulta de saldo | consulta por medicamento e lote | Planejado |
| T4.2.3 | Criar casos de uso de histórico | consulta de movimentações | Planejado |

### T3 - Ports e persistência

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T4.3 | Definir portas de saída do módulo | interfaces de repositório | Planejado |
| T4.3.1 | Implementar adapters JPA | repositories e mapeamentos | Planejado |
| T4.3.2 | Mapear entidades de estoque | relações com medicamento, lote e usuário | Planejado |
| T4.3.3 | Persistir movimentações | histórico consistente e auditável | Planejado |

### T4 - API REST

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T4.4 | Expor endpoints de entrada | controller e DTOs de entrada | Planejado |
| T4.4.1 | Expor endpoints de saída | controller e DTOs de saída | Planejado |
| T4.4.2 | Expor endpoints de consulta de saldo | endpoints de leitura do estoque | Planejado |
| T4.4.3 | Expor endpoints de histórico e vencimento | consultas de movimentação e validade | Planejado |

### T5 - Validações e mensagens

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T4.5 | Aplicar validações de entrada | DTOs e Bean Validation | Planejado |
| T4.5.1 | Externalizar mensagens | chaves em message.properties | Planejado |
| T4.5.2 | Integrar exceptions globais | respostas uniformes e compreensíveis | Planejado |

### T6 - Testes

| ID | Tarefa | Entregável | Status |
|---|---|---|---|
| T4.6 | Criar testes unitários do domínio | regras de FEFO, saldo e validade | Planejado |
| T4.6.1 | Criar testes de casos de uso | entrada, saída e histórico | Planejado |
| T4.6.2 | Criar testes de integração da API | cenários de estoque e movimentação | Planejado |
| T4.6.3 | Validar cobertura do módulo | evidência de qualidade | Planejado |

## 9. Critérios de aceite do módulo

O módulo de estoque será considerado concluído quando:

- entradas e saídas forem persistidas corretamente;
- o saldo por medicamento e lote estiver consistente;
- a regra FEFO estiver aplicada em saídas;
- lotes vencidos e próximos do vencimento forem identificados;
- o histórico de movimentações estiver rastreável;
- a API expuser operações relevantes de forma padronizada;
- os testes do módulo executarem com sucesso;
- as mensagens e erros estiverem centralizadas no padrão do projeto.

## 10. Observações finais

Este documento é um plano de implementação e não deve ser interpretado como execução de código. O foco da etapa é definir contexto, regras e divisão de tarefas para que o módulo seja entregue de forma consistente com o restante da arquitetura do PharmaGuard.

A prioridade do módulo é garantir integridade operacional e confiabilidade dos dados de estoque antes de avançar para camadas de inteligência, alertas e relatórios.
