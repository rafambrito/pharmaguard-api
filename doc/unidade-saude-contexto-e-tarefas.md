# Etapa 10 - Unidade de Saúde

## Contexto do módulo

A etapa de Unidade de Saúde tem como objetivo ampliar o modelo de domínio do PharmaGuard para que o estoque, o controle de medicamentos e as operações de entrada/saída sejam vinculados a uma unidade específica de atendimento.

Este ajuste é essencial para que o sistema reflita a realidade operacional de saúde pública e institucional, em que o mesmo medicamento pode existir em múltiplas unidades, cada uma com:

- estoque próprio;
- lotes próprios;
- consumo específico;
- regras e riscos independentes;
- necessidade de segregação por localidade.

A funcionalidade proposta contempla a criação do domínio de Unidade de Saúde e a adaptação dos módulos já existentes para que o conceito de unidade seja tratado como parte integrante do contexto de negócio.

---

## Objetivo da etapa

Garantir que o sistema passe a operar com o conceito de "Unidade de Saúde" como eixo organizacional principal para:

- cadastro e manutenção da unidade;
- vinculação do estoque ao local;
- controle de entradas e saídas por unidade;
- consultas e relatórios segmentados por unidade;
- consistência das regras de negócio entre medicamentos, lotes e local de operação.

A etapa está alinhada com a visão do roadmap do projeto, que define a necessidade de crescimento do domínio após a base inicial de auth, medicamentos e fornecedores.

---

## Contexto arquitetural do projeto

O projeto segue uma estrutura baseada em Clean Architecture / Hexagonal Architecture, com separação entre:

- domain
- application
- adapters.in
- adapters.out
- shared

A adaptação para Unidade de Saúde deve seguir esse mesmo padrão, preservando a regra de que:

- o domínio define o conceito e a regra de negócio;
- os casos de uso orquestram a operação;
- os endpoints expõem a funcionalidade;
- a infraestrutura implementa persistência e integrações.

A unidade não deve virar responsabilidade apenas de um controller ou de tabela isolada; ela precisa ser tratada como conceito que impacta o modelo de domínio e o comportamento do sistema.

---

## Módulos impactados

### 1) inventory
É o módulo principal afetado, porque é onde estão concentrados:

- Produto
- Lote
- Estoque
- Entrada
- Saída
- Movimentação
- Histórico de consumo

Neste módulo, a unidade deve ser incorporada como atributo de contexto do estoque e dos fluxos de movimentação.

### 2) shared
Pode exigir ajustes em:

- auditoria;
- filtros de contexto;
- validações transversais;
- orientação de logs/observabilidade.

### 3) reports
Os relatórios devem considerar unidade como filtro ou agrupamento obrigatório para produção de indicadores e alertas. Sem esse ajuste, os dados ficam ambíguos ou inconsistentes.

### 4) scheduler / analytics
Se houver futura processamento automático de consumo e alertas, a unidade deve ser tratada como partição lógica do cálculo e da recomendação.

### 5) auth
Em regra, a unidade pode ser integrada posteriormente ao domínio de autorização e gestão de usuários, especialmente para vincular usuário à unidade de atuação. Isso é uma extensão funcional, não parte do escopo cinza da etapa atual.

---

## Entidades e conceitos de domínio

### UnidadeSaude
Entidade central do módulo. Deve representar a unidade responsável pela operação do estoque.

Atributos esperados no domínio:

- id
- identificacao
- nome
- tipo
- endereco
- status
- dataCadastro
- dataAtualizacao

Regras esperadas:

- identificação única;
- nome obrigatório;
- status ativo/inativo;
- endereço com campos mínimos para cadastro;
- validação de integridade antes de persistência.

### Estoque
O estoque deve ser associado à unidade. O vínculo deve refletir que o saldo de um medicamento não é global, mas local.

Ajustes esperados:

- vínculo do estoque a uma UnidadeSaude;
- consistência de relacionamento entre produto, lote e unidade;
- consultas por unidade;
- filtros por local de operação.

### Produto / Lote
O produto e os lotes continuam existindo, mas devem ser entendidos dentro do contexto da unidade.

Ajuste esperado:

- cada lote pode ser controlado por unidade;
- movimentações devem respeitar a unidade do lote e da movimentação;
- a validade e a quantidade continuam sendo atributos do lote, mas o contexto de consumo passa a ser local.

### Entrada / Saída / Movimentacao
Essas entidades precisam ser adaptadas para incorporar a unidade operante.

Ajustes esperados:

- entrada exige referência à unidade;
- saída exige referência à unidade;
- movimentação deve carregar unidade do evento;
- histórico deve ser persistido com contexto local.

---

## Impacto no modelo de persistência

A camada de infraestrutura deve contemplar:

- migration Flyway para nova tabela de unidade de saúde;
- relacionamento entre estoque e unidade;
- relacionamento entre movimentações e unidade;
- ajustes de constraints e chaves estrangeiras;
- índices para consultas por unidade.

A estrutura deve evitar que o mesmo estoque seja tratado como global sem distinção por local.

---

## Divisão de tarefas sugerida

### T10.1 - Cadastro de Unidade de Saúde

Objetivo:

- criar a entidade UnidadeSaude;
- definir atributos e validações;
- criar repository;
- expor casos de uso de cadastro, consulta, alteração e inativação;
- criar endpoints REST;
- documentar na OpenAPI;
- cobrir com testes.

Entregáveis esperados:

- entidade de domínio;
- persistência; 
- caso de uso;
- API de gerenciamento;
- testes unitários e de integração.

### T10.2 - Vinculação do estoque à unidade

Objetivo:

- ajustar a modelagem do estoque para exigir uma UnidadeSaude;
- garantir que cada saldo pertença a um único local;
- revisar regras de consistência entre produto, lote e estoque.

Entregáveis esperados:

- ajuste de domínio e entidades;
- atualização da persistência;
- regras de consistência;
- testes de integridade.

### T10.3 - Adaptação de entradas e saídas

Objetivo:

- exigir que operações de entrada e saída tenham referência à unidade;
- garantir que a movimentação respeite a unidade correta;
- ajustar a lógica de consulta do histórico local.

Entregáveis esperados:

- modelo de entrada/saída ajustado;
- regras de validação;
- testes para fluxo de movimentação por unidade.

### T10.4 - Ajustes de consultas e relatórios

Objetivo:

- segmentar consultas por unidade;
- ajustar relatórios para refletir a realidade local;
- manter filtros consistentes em estoque, vencimento, risco e consumo.

Entregáveis esperados:

- endpoints e consultas por unidade;
- filtros e agrupamentos;
- testes de integração de consultas.

### T10.5 - Validação de consistência e regras de negócio

Objetivo:

- validar que não haja operação sem unidade;
- garantir que o mesmo medicamento em unidades distintas seja tratado independentemente;
- prevenir inconsistências entre saldo, lotes e movimentações.

Entregáveis esperados:

- testes de regressão;
- validações em use cases;
- documentação de regras de negócio.

### T10.6 - Testes e documentação do módulo

Objetivo:

- validar os casos de uso do módulo;
- testar a integração com o restante do backend;
- atualizar a documentação do projeto e do módulo.

Entregáveis esperados:

- cobertura mínima de testes;
- documentação de API;
- referência do módulo no contexto do projeto.

---

## Regras de implementação esperadas

1. Manter a separação por camadas.
2. Não misturar regras de negócio com controllers.
3. A unidade deve ser tratada como conceito de domínio e não apenas como campo de banco.
4. Ajustes em entrada/saída devem respeitar a regra de segregação por local.
5. Relatórios e consultas devem considerar unidade como critério principal.
6. A modelagem deve permitir escala futura, incluindo transferências entre unidades e estoque por localidade.

---

## Escopo desta etapa

### Incluído

- criação do domínio Unidade de Saúde;
- adaptação da estrutura de estoque para vincular um local;
- ajuste de entradas, saídas e movimentações;
- refinamento de consultas e relatórios por unidade;
- validação de integridade e testes.

### Excluído

- transferência de medicamentos entre unidades;
- regras avançadas de oportunidade de transferência;
- processo de análise estatística complexa entre unidades;
- frontend ou interface de usuário;
- implementação de features fora do módulo de unidade de saúde.

---

## Recomendações de execução

- Não implementar neste momento.
- Não executar tarefas fora do escopo definido.
- Manter foco em modelagem, adaptação e consistência do domínio.
- Usar esta documentação como base para a execução da etapa com controle de escopo.

---

## Resumo executivo

A etapa 10 representa a evolução do sistema de estoque para um modelo orientado por unidade de saúde. O módulo deve ser tratado como extensão natural do domínio já existente, com o objetivo de reduzir ambiguidade operacional e aumentar a consistência do controle farmacêutico por local.

A prioridade principal não é apenas criar a entidade UnidadeSaude, mas adaptar o ecossistema do domínio para que estoque, movimentações e relatórios possam operar de forma correta em um ambiente com múltiplas unidades.
