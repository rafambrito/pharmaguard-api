# Modulo Medicamentos - (Categoria / Unidade Medida / Medicamento / Validade / Lotes)

## 1. Contexto
- O modulo medicamentos faz parte do monolito modular PharmaGuard.
- A estrutura segue Clean Architecture e Arquitetura Hexagonal (ports and adapters).
- Objetivo da etapa: entregar o CRUD de medicamentos com suporte a categorias, unidades de medida, validade e controle por lotes.
- Escopo desta etapa:
  - API First com contrato OpenAPI
  - CRUD Categoria
  - CRUD Unidade de Medida
  - CRUD Medicamento
  - Controle de validade
  - Controle de lotes
  - Testes do modulo
- Requisitos transversais previstos:
  - Reutilizar `message.properties` para mensagens de validacao e erro.
  - Definir o contrato OpenAPI do modulo antes da implementacao dos endpoints.

## 2. Arquitetura do modulo
### 2.1 Camadas
- domain
  - Entidades e regras de negocio puras (Categoria, UnidadeMedida, Medicamento, Lote e regras de validade).
- application
  - Casos de uso para criar, atualizar, consultar, listar e remover dados do catalogo de medicamentos.
  - Ports de entrada e saida.
- adapters.out
  - Adapters de persistencia JPA, mapeamentos e configuracoes necessarias ao modulo.
- adapters.in
  - Endpoints REST, DTOs, validacoes de entrada e mapeamento request/response.

### 2.2 Fluxo principal (resumo)
- API recebe requisicao.
- Application executa o caso de uso.
- Domain aplica regras de catalogo, validade e lote.
- adapters.out persiste e consulta os dados.
- API retorna resposta padronizada.

## 3. Regras
- Categoria deve possuir identificacao unica por nome.
- Unidade de medida deve possuir identificacao unica por sigla ou codigo definido pela aplicacao.
- Medicamento deve possuir identificacao unica por nome e apresentacao cadastrada.
- Medicamento deve referenciar uma categoria e uma unidade de medida validas.
- Medicamento deve permitir classificacao de criticidade para uso futuro nos modulos de estoque e inteligencia.
- Lote deve estar sempre vinculado a um medicamento.
- Lote deve possuir numero identificador e data de validade obrigatoria.
- Nao deve ser permitido cadastrar validade vencida para lote novo.
- Mensagens de validacao e erro devem ser externalizadas em `message.properties`.

## 4. Tarefas

### T0 - API First (OpenAPI)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.0 | Definir estrategia API First para medicamentos | contrato OpenAPI como fonte de verdade da API | ✅ Concluído |
| T2.0.1 | Criar arquivo de contrato do modulo medicamentos | arquivo yaml com paths e schemas de categoria, unidade de medida, medicamento e lote | ✅ Concluído |
| T2.0.2 | Validar padrao de respostas e erros | contratos de sucesso e erro alinhados ao projeto | ✅ Concluído |

### T1 - Criar Entidade Categoria

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.1 | Criar entidade Categoria no dominio | classe Categoria na camada domain | ✅ Concluído  |
| T2.1.1 | Definir atributos obrigatorios | modelo com nome, descricao e status basico | ✅ Concluído  |
| T2.1.2 | Definir regra de unicidade | regra para evitar categoria duplicada | ✅ Concluído |

### T2 - Criar Entidade Unidade de Medida

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.2 | Criar entidade UnidadeMedida no dominio | classe UnidadeMedida na camada domain | ✅ Concluído |
| T2.2.1 | Definir atributos obrigatorios | modelo com nome, sigla e status basico | ✅ Concluído |
| T2.2.2 | Definir regra de unicidade | regra para evitar unidade de medida duplicada | ✅ Concluído |

### T3 - Criar Entidade Medicamento

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.3 | Criar entidade Medicamento no dominio | classe Medicamento na camada domain | ✅ Concluído |
| T2.3.1 | Definir atributos obrigatorios | modelo com nome, descricao, categoria, unidade de medida e criticidade | ✅ Concluído |
| T2.3.2 | Definir relacoes de catalogo | associacoes consistentes entre medicamento, categoria e unidade de medida | ✅ Concluído |

### T4 - Criar Regras de Validade

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.4 | Criar regras de validade no dominio | objeto ou regras para controle de data de validade | ✅ Concluído |
| T2.4.1 | Validar consistencia da data | regra para impedir datas invalidas ou vencidas no cadastro de lote | ✅ Concluído |
| T2.4.2 | Preparar classificacao basica de vencimento | regra pronta para identificar itens validos e proximos do vencimento | ✅ Concluído |

### T5 - Criar Entidade Lote

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.5 | Criar entidade Lote no dominio | classe Lote na camada domain | ✅ Concluído |
| T2.5.1 | Definir atributos obrigatorios | modelo com numero do lote, validade, quantidade inicial e medicamento vinculado | ✅ Concluído |
| T2.5.2 | Definir regra de identificacao por medicamento | regra para evitar duplicidade indevida de lote por medicamento | ✅ Concluído |

### T6 - Casos de Uso (Application)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.6 | Criar casos de uso de Categoria e Unidade de Medida | servicos de aplicacao para CRUD basico | ✅ Concluído |
| T2.6.1 | Criar casos de uso de Medicamento | servicos de aplicacao para CRUD de medicamento | ✅ Concluído |
| T2.6.2 | Criar casos de uso de Lote e validade | servicos de aplicacao para cadastro e consulta de lotes | ✅ Concluído |

### T7 - Repository / Persistencia

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.7 | Definir portas de repositorio | interfaces de saida no application | ✅ Concluído |
| T2.7.1 | Implementar adapters JPA | repositorios JPA para categoria, unidade de medida, medicamento e lote | ✅ Concluído |
| T2.7.2 | Mapear entidades de persistencia | entidades JPA e relacionamentos consistentes | ✅ Concluído |

### T8 - Endpoints

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.8 | Expor endpoints CRUD de Categoria | controller/resource com operacoes CRUD de categoria | ✅ Concluído |
| T2.8.1 | Expor endpoints CRUD de Unidade de Medida | controller/resource com operacoes CRUD de unidade de medida | ✅ Concluído |
| T2.8.2 | Expor endpoints CRUD de Medicamento e consulta de lotes | endpoints para gestao do catalogo e lotes | ✅ Concluído |

### T9 - Validacoes e Mensagens

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.9 | Aplicar validacoes de entrada | DTOs validados e respostas de erro padrao | ✅ Concluído |
| T2.9.1 | Reutilizar mensagens no arquivo global | chaves do modulo adicionadas em `message.properties` | ✅ Concluído |
| T2.9.2 | Integrar mensagens em validacoes e excecoes | erros com mensagens centralizadas | ✅ Concluído |

### T10 - Testes

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T2.10 | Criar testes unitarios de dominio e application | suite cobrindo regras e casos de uso principais | ✅ Concluído |
| T2.10.1 | Criar testes de integracao da API de medicamentos | cenarios CRUD e consultas de lote validados | ✅ Concluído |
| T2.10.2 | Gerar cobertura minima do modulo | relatorio de cobertura da etapa de medicamentos | ✅ Concluído |

## 5. Criterios de Aceite

| Status | Criterio |
|--------|----------|
| ✅ Concluído | Modulo compilando |
| ✅ Concluído  | Contrato OpenAPI do modulo medicamentos definido |
| X - Pendente | CRUD de categorias funcionando |
| X - Pendente | CRUD de unidades de medida funcionando |
| X - Pendente | CRUD de medicamentos funcionando |
| X - Pendente | Cadastro e consulta de lotes com validade funcionando |
| ✅ Concluído | Mensagens centralizadas em `message.properties` |
| ✅ Concluído | Testes unitarios e de integracao executando |
| ✅ Concluído | Cobertura minima do modulo gerada |
