package com.pharmaguard.api.reports.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.reports.application.FiltroConsumo;
import com.pharmaguard.api.reports.application.RelatorioConsumoUseCase;
import com.pharmaguard.api.reports.application.RelatorioConsumoUseCaseImpl;
import com.pharmaguard.api.reports.adapters.in.controller.RelatorioConsumoController;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RelatorioConsumoIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new RelatorioConsumoController(new RelatorioConsumoUseCaseImpl(new FakeRelatorioConsumoRepository())))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveGerarRelatorioDeConsumoPorPeriodo() throws Exception {
        mockMvc.perform(get("/api/v1/relatorios/consumo")
                        .param("periodoInicio", "2026-01-02")
                        .param("periodoFim", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalConsumido", is(60.0)))
                .andExpect(jsonPath("$.mediaDiaria", is(2.0)))
                .andExpect(jsonPath("$.tendencia", is("ESTAVEL")))
                .andExpect(jsonPath("$.itens[0].nomeMedicamento", is("Amoxicilina")))
                .andExpect(jsonPath("$.itens[0].categoriaNome", is("Antibioticos")))
                .andExpect(jsonPath("$.itens[0].unidadeMedidaSigla", is("MG")))
                .andExpect(jsonPath("$.itens[0].quantidadeConsumida", is(60.0)));
    }

    @Test
    void deveRejeitarPeriodoAusenteComMensagemCentralizada() throws Exception {
        mockMvc.perform(get("/api/v1/relatorios/consumo")
                        .param("periodoInicio", "2026-01-02"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("msg.validacao.periodo.obrigatorio")))
                .andExpect(jsonPath("$.detail", is("periodoInicio e periodoFim sao obrigatorios")));
    }

    private static final class FakeRelatorioConsumoRepository implements RelatorioConsumoUseCase.RelatorioConsumoRepositoryPort {

        @Override
        public List<MovimentacaoEstoque> listarSaidas(FiltroConsumo filtro) {
            Categoria categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNome("Antibioticos");
            categoria.setStatus(Categoria.Status.ATIVA);

            UnidadeMedida unidade = new UnidadeMedida();
            unidade.setId(1L);
            unidade.setNome("Miligrama");
            unidade.setSigla("mg");
            unidade.setStatus(UnidadeMedida.Status.ATIVA);

            Medicamento medicamento = new Medicamento();
            medicamento.setId(1L);
            medicamento.setNome("Amoxicilina");
            medicamento.setApresentacao("500mg");
            medicamento.setCategoria(categoria);
            medicamento.setUnidadeMedida(unidade);
            medicamento.setCriticidade(Medicamento.Criticidade.MEDIA);
            medicamento.setStatus(Medicamento.Status.ATIVO);

            Lote lote = new Lote();
            lote.setId(1L);
            lote.setNumeroLote("LOT-001");
            lote.setMedicamento(medicamento);
            lote.setDataValidade(LocalDate.now().plusYears(1));
            lote.setQuantidadeInicial(100);

            MovimentacaoEstoque saida1 = new MovimentacaoEstoque();
            saida1.setId(1L);
            saida1.setTipo(MovimentacaoEstoque.Tipo.SAIDA);
            saida1.setMedicamento(medicamento);
            saida1.setLote(lote);
            saida1.setQuantidade(30);
            saida1.setSaldoAposMovimentacao(70);
            saida1.setDataMovimentacao(LocalDateTime.of(2026, 1, 5, 9, 0));
            saida1.setMotivo("Uso interno");

            MovimentacaoEstoque saida2 = new MovimentacaoEstoque();
            saida2.setId(2L);
            saida2.setTipo(MovimentacaoEstoque.Tipo.SAIDA);
            saida2.setMedicamento(medicamento);
            saida2.setLote(lote);
            saida2.setQuantidade(30);
            saida2.setSaldoAposMovimentacao(40);
            saida2.setDataMovimentacao(LocalDateTime.of(2026, 1, 20, 9, 0));
            saida2.setMotivo("Uso interno");

            return List.of(saida1, saida2);
        }
    }
}
