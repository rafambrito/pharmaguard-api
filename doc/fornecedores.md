# Modulo Fornecedores - (Fornecedor / Lead Time / Contatos)

## 1. Contexto
- O modulo fornecedores faz parte do monolito modular PharmaGuard.
- A estrutura segue Clean Architecture e Arquitetura Hexagonal (ports and adapters).
- Objetivo da etapa: entregar o CRUD de fornecedores com dados de contato e lead time para apoio aos modulos de estoque e reposicao.
- Escopo desta etapa:
  - API First com contrato OpenAPI
  - CRUD Fornecedor
  - Cadastro de contatos
  - Cadastro de lead time
  - Testes do modulo
- Requisitos transversais previstos:
  - Reutilizar `message.properties` para mensagens de validacao e erro.
  - Definir o contrato OpenAPI do modulo antes da implementacao dos endpoints.

## 2. Arquitetura do modulo
### 2.1 Camadas
- domain
  - Entidades e regras de negocio puras (Fornecedor, ContatoFornecedor e regras de lead time).
- application
  - Casos de uso para criar, atualizar, consultar, listar e remover fornecedores e seus contatos.
  - Ports de entrada e saida.
- adapters.out
  - Adapters de persistencia JPA, mapeamentos e configuracoes necessarias ao modulo.
- adapters.in
  - Endpoints REST, DTOs, validacoes de entrada e mapeamento request/response.

### 2.2 Fluxo principal (resumo)
- API recebe requisicao.
- Application executa o caso de uso.
- Domain aplica regras de fornecedor, contato e lead time.
- adapters.out persiste e consulta os dados.
- API retorna resposta padronizada.

## 3. Regras
- Fornecedor deve possuir identificacao unica por documento ou codigo definido pela aplicacao.
- Fornecedor deve possuir nome obrigatorio e status para controle de disponibilidade.
- Lead time deve ser informado em dias e nao pode ser negativo.
- Fornecedor pode possuir um ou mais contatos vinculados.
- Contato deve possuir informacao minima para comunicacao, como nome e meio de contato principal.
- Nao deve ser permitido cadastrar contatos duplicados para o mesmo fornecedor quando representarem o mesmo canal principal.
- Exclusao de fornecedor deve respeitar regras de integridade definidas pela aplicacao.
- Mensagens de validacao e erro devem ser externalizadas em `message.properties`.

## 4. Tarefas

### T0 - API First (OpenAPI)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.0 | Definir estrategia API First para fornecedores | contrato OpenAPI como fonte de verdade da API | ✅ Concluído |
| T3.0.1 | Criar arquivo de contrato do modulo fornecedores | arquivo yaml com paths e schemas de fornecedor, contato e lead time | ✅ Concluído |
| T3.0.2 | Validar padrao de respostas e erros | contratos de sucesso e erro alinhados ao projeto | ✅ Concluído |

### T1 - Criar Entidade Fornecedor

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.1 | Criar entidade Fornecedor no dominio | classe Fornecedor na camada domain | ✅ Concluído |
| T3.1.1 | Definir atributos obrigatorios | modelo com nome, documento ou codigo, status e lead time base | ✅ Concluído |
| T3.1.2 | Definir regra de unicidade | regra para evitar fornecedor duplicado | ✅ Concluído |

### T2 - Criar Entidade ContatoFornecedor

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.2 | Criar entidade ContatoFornecedor no dominio | classe de contato vinculada ao fornecedor | ✅ Concluído |
| T3.2.1 | Definir atributos obrigatorios | modelo com nome, cargo opcional, telefone, email e canal principal | ✅ Concluído |
| T3.2.2 | Definir relacao com fornecedor | associacao consistente entre fornecedor e contatos | ✅ Concluído |

### T3 - Criar Regras de Lead Time

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.3 | Criar regras de lead time no dominio | objeto ou regras para prazo medio de atendimento | ✅ Concluído |
| T3.3.1 | Validar consistencia do prazo | regra para impedir valores negativos ou invalidos | ✅ Concluído |
| T3.3.2 | Preparar classificacao basica de prazo | regra pronta para diferenciar prazos usuais e elevados | ✅ Concluído |

### T4 - Casos de Uso (Application)

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.4 | Criar casos de uso de Fornecedor | servicos de aplicacao para CRUD de fornecedor | ✅ Concluído |
| T3.4.1 | Criar casos de uso de contatos | servicos para adicionar, atualizar, listar e remover contatos | ✅ Concluído |
| T3.4.2 | Criar casos de uso de lead time | servicos para cadastrar e atualizar o prazo do fornecedor | ✅ Concluído |

### T5 - Repository / Persistencia

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.5 | Definir portas de repositorio | interfaces de saida no application | ✅ Concluído |
| T3.5.1 | Implementar adapters JPA | repositorios JPA para fornecedor e contatos | ✅ Concluído |
| T3.5.2 | Mapear entidades de persistencia | entidades JPA e relacionamentos consistentes | ✅ Concluído |

### T6 - Endpoints

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.6 | Expor endpoints CRUD de fornecedor | controller/resource com operacoes CRUD de fornecedor | ✅ Concluído |
| T3.6.1 | Expor endpoints de contatos | endpoints para gestao de contatos do fornecedor | ✅ Concluído |
| T3.6.2 | Expor endpoint de lead time | endpoint para consulta e atualizacao do prazo do fornecedor | ✅ Concluído |

### T7 - Validacoes e Mensagens

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.7 | Aplicar validacoes de entrada | DTOs validados e respostas de erro padrao | ✅ Concluído |
| T3.7.1 | Reutilizar mensagens no arquivo global | chaves do modulo adicionadas em `message.properties` | ✅ Concluído |
| T3.7.2 | Integrar mensagens em validacoes e excecoes | erros com mensagens centralizadas | ✅ Concluído |

### T8 - Testes

| ID   | Tarefa | Entregavel | Status |
|------|--------|------------|--------|
| T3.8 | Criar testes unitarios de dominio e application | suite cobrindo regras e casos de uso principais | X - Pendente |
| T3.8.1 | Criar testes de integracao da API de fornecedores | cenarios CRUD, contatos e lead time validados | X - Pendente |
| T3.8.2 | Gerar cobertura minima do modulo | relatorio de cobertura da etapa de fornecedores | X - Pendente |
| T3.8 | Criar testes unitarios de dominio e application | suite cobrindo regras e casos de uso principais | ✅ Concluído |
| T3.8.1 | Criar testes de integracao da API de fornecedores | cenarios CRUD, contatos e lead time validados | ✅ Concluído |
| T3.8.2 | Gerar cobertura minima do modulo | relatorio de cobertura da etapa de fornecedores | ✅ Concluído |

## 5. Criterios de Aceite

| Status | Criterio |
|--------|----------|
| ✅ Concluído | Modulo compilando |
| X - Pendente | Contrato OpenAPI do modulo fornecedores definido |
| X - Pendente | CRUD de fornecedores funcionando |
| X - Pendente | Cadastro e manutencao de contatos funcionando |
| X - Pendente | Cadastro e atualizacao de lead time funcionando |
| ✅ Concluído | Mensagens centralizadas em `message.properties` |
| ✅ Concluído| Testes unitarios e de integracao executando |
| ✅ Concluído| Cobertura minima do modulo gerada |
| ✅ Concluído | Mensagens centralizadas em `message.properties` |
| ✅ Concluído | Testes unitarios e de integracao executando |
| ✅ Concluído | Cobertura minima do modulo gerada |
