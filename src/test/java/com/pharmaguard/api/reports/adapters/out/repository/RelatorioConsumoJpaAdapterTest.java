package com.pharmaguard.api.reports.adapters.out.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pharmaguard.api.inventory.adapters.out.repository.MovimentacaoEstoqueJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.CategoriaEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.LoteEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MedicamentoEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.MovimentacaoEstoqueEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeMedidaEntity;
import com.pharmaguard.api.inventory.adapters.out.repository.entity.UnidadeSaudeEntity;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.StatusValidade;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.reports.application.FiltroConsumo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class RelatorioConsumoJpaAdapterTest {

    @Test
    void deveListarSaidaComLoteVencido() {
        LocalDate periodoInicio = LocalDate.now().minusDays(30);
        LocalDate periodoFim = LocalDate.now();
        MovimentacaoEstoqueJpaRepository repository = mock(MovimentacaoEstoqueJpaRepository.class);
        MovimentacaoEstoqueEntity entity = criarMovimentacaoComLoteVencido();
        when(repository.findByPeriodoObrigatorio(
                        null,
                        MovimentacaoEstoque.Tipo.SAIDA,
                        periodoInicio.atStartOfDay(),
                        periodoFim.atTime(LocalTime.MAX),
                        null))
                .thenReturn(List.of(entity));

        List<MovimentacaoEstoque> saidas = new RelatorioConsumoJpaAdapter(repository)
                .listarSaidas(new FiltroConsumo(periodoInicio, periodoFim, null, null, null, null, null));

        assertEquals(1, saidas.size());
        assertEquals(StatusValidade.VENCIDO, saidas.getFirst().getLote().getStatusValidade());
    }

    private MovimentacaoEstoqueEntity criarMovimentacaoComLoteVencido() {
        CategoriaEntity categoria = mock(CategoriaEntity.class);
        when(categoria.getId()).thenReturn(1L);
        when(categoria.getNome()).thenReturn("Analgesicos");
        when(categoria.getDescricao()).thenReturn("Medicamentos analgesicos");
        when(categoria.getStatus()).thenReturn(Categoria.Status.ATIVA);

        UnidadeMedidaEntity unidadeMedida = mock(UnidadeMedidaEntity.class);
        when(unidadeMedida.getId()).thenReturn(1L);
        when(unidadeMedida.getNome()).thenReturn("Unidade");
        when(unidadeMedida.getSigla()).thenReturn("UN");
        when(unidadeMedida.getStatus()).thenReturn(UnidadeMedida.Status.ATIVA);

        MedicamentoEntity medicamento = mock(MedicamentoEntity.class);
        when(medicamento.getId()).thenReturn(1L);
        when(medicamento.getNome()).thenReturn("Dipirona");
        when(medicamento.getApresentacao()).thenReturn("500 mg");
        when(medicamento.getDescricao()).thenReturn("Comprimido");
        when(medicamento.getCategoria()).thenReturn(categoria);
        when(medicamento.getUnidadeMedida()).thenReturn(unidadeMedida);
        when(medicamento.getCriticidade()).thenReturn(Medicamento.Criticidade.MEDIA);
        when(medicamento.getStatus()).thenReturn(Medicamento.Status.ATIVO);

        LoteEntity lote = mock(LoteEntity.class);
        when(lote.getId()).thenReturn(1L);
        when(lote.getNumeroLote()).thenReturn("LOT-001");
        when(lote.getDataValidade()).thenReturn(LocalDate.now().minusDays(1));
        when(lote.getQuantidadeInicial()).thenReturn(100);
        when(lote.getMedicamento()).thenReturn(medicamento);

        UnidadeSaudeEntity unidadeSaude = mock(UnidadeSaudeEntity.class);
        when(unidadeSaude.getId()).thenReturn(1L);

        MovimentacaoEstoqueEntity movimentacao = mock(MovimentacaoEstoqueEntity.class);
        when(movimentacao.getId()).thenReturn(1L);
        when(movimentacao.getTipo()).thenReturn(MovimentacaoEstoque.Tipo.SAIDA);
        when(movimentacao.getUnidadeSaude()).thenReturn(unidadeSaude);
        when(movimentacao.getMedicamento()).thenReturn(medicamento);
        when(movimentacao.getLote()).thenReturn(lote);
        when(movimentacao.getQuantidade()).thenReturn(10);
        when(movimentacao.getSaldoAposMovimentacao()).thenReturn(90);
        when(movimentacao.getDataMovimentacao()).thenReturn(LocalDateTime.now().minusDays(10));
        when(movimentacao.getMotivo()).thenReturn("Dispensacao");
        when(movimentacao.getUsuarioResponsavelId()).thenReturn(1L);
        return movimentacao;
    }
}