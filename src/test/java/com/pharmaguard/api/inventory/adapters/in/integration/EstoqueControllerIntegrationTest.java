package com.pharmaguard.api.inventory.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.pharmaguard.api.inventory.adapters.in.controller.EstoqueController;
import com.pharmaguard.api.inventory.adapters.in.mapper.InventoryAdapterInMapper;
import com.pharmaguard.api.inventory.application.EntradaEstoqueUseCase;
import com.pharmaguard.api.inventory.application.HistoricoEstoqueUseCase;
import com.pharmaguard.api.inventory.application.SaidaEstoqueUseCase;
import com.pharmaguard.api.inventory.application.SaldoEstoqueUseCase;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.EstoqueAtual;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class EstoqueControllerIntegrationTest {

    private MockMvc mockMvc;
    private EntradaEstoqueUseCase entradaUseCase;
    private SaldoEstoqueUseCase saldoUseCase;

    @BeforeEach
    void setUp() {
        entradaUseCase = org.mockito.Mockito.mock(EntradaEstoqueUseCase.class);
        SaidaEstoqueUseCase saidaUseCase = org.mockito.Mockito.mock(SaidaEstoqueUseCase.class);
        saldoUseCase = org.mockito.Mockito.mock(SaldoEstoqueUseCase.class);
        HistoricoEstoqueUseCase historicoUseCase = org.mockito.Mockito.mock(HistoricoEstoqueUseCase.class);

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new EstoqueController(
                        entradaUseCase,
                        saidaUseCase,
                        saldoUseCase,
                        historicoUseCase,
                        new InventoryAdapterInMapper()))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .setValidator(validator)
                .build();
    }

    @Test
    void deveConsultarSaldoPorMedicamento() throws Exception {
        EstoqueAtual estoque = new EstoqueAtual();
        estoque.setMedicamento(medicamento(1L));
        estoque.setQuantidadeDisponivel(80);
        estoque.setQuantidadeReservada(10);
        estoque.definirLotesAtivos(List.of(new SaldoLoteEstoque(
                1L, 2L, "LOT-001", LocalDate.now().plusDays(30), 80)));
        estoque.recalcularComBaseNosLotes();
        when(saldoUseCase.consultarPorMedicamento(1L, 1L)).thenReturn(estoque);

        mockMvc.perform(get("/api/v1/estoque/saldos/1").param("unidadeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicamentoId", is(1)))
                .andExpect(jsonPath("$.quantidadeDisponivel", is(80)))
                .andExpect(jsonPath("$.lotesAtivos[0].numeroLote", is("LOT-001")));
    }

    @Test
    void deveRegistrarEntradaComPayloadValido() throws Exception {
        EntradaEstoque entrada = new EntradaEstoque();
        entrada.setId(3L);
        entrada.setMedicamento(medicamento(1L));
        entrada.setLote(new com.pharmaguard.api.inventory.domain.Lote());
        entrada.getLote().setId(2L);
        entrada.setQuantidade(20);
        entrada.setOrigem(EntradaEstoque.Origem.FORNECEDOR);
        when(entradaUseCase.registrar(eq(1L), eq(1L), eq(2L), any(EntradaEstoque.class))).thenReturn(entrada);

        mockMvc.perform(post("/api/v1/estoque/entradas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "unidadeId": 1,
                                  "medicamentoId": 1,
                                  "loteId": 2,
                                  "quantidade": 20,
                                  "origem": "FORNECEDOR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.medicamentoId", is(1)))
                .andExpect(jsonPath("$.quantidade", is(20)));
    }

    @Test
    void deveRetornarBadRequestParaEntradaInvalida() throws Exception {
        mockMvc.perform(post("/api/v1/estoque/entradas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medicamentoId": 0,
                                  "loteId": 2,
                                  "quantidade": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Requisicao invalida. Verifique os campos informados.")))
                .andExpect(jsonPath("$.errors").isArray());
    }

    private Medicamento medicamento(Long id) {
        Categoria categoria = new Categoria();
        categoria.setNome("Categoria");
        categoria.setDescricao("Descricao");
        UnidadeMedida unidade = new UnidadeMedida();
        unidade.setNome("Unidade");
        unidade.setSigla("un");
        Medicamento medicamento = new Medicamento();
        medicamento.setId(id);
        medicamento.setNome("Medicamento");
        medicamento.setApresentacao("Apresentacao");
        medicamento.setCategoria(categoria);
        medicamento.setUnidadeMedida(unidade);
        medicamento.setCriticidade(Medicamento.Criticidade.MEDIA);
        medicamento.setStatus(Medicamento.Status.ATIVO);
        return medicamento;
    }
}
