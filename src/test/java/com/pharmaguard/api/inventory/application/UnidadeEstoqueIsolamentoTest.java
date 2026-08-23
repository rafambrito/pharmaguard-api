package com.pharmaguard.api.inventory.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UnidadeEstoqueIsolamentoTest {

    @Test
    void deveManterSaldoDoMesmoMedicamentoSeparadoPorUnidade() {
        Medicamento medicamento = medicamento();
        IsolatedSaldoRepository repository = new IsolatedSaldoRepository(medicamento);
        SaldoEstoqueUseCase useCase = new SaldoEstoqueUseCaseImpl(repository);

        assertThat(useCase.consultarPorMedicamento(10L, 1L).getQuantidadeDisponivel()).isEqualTo(100);
        assertThat(useCase.consultarPorMedicamento(20L, 1L).getQuantidadeDisponivel()).isEqualTo(25);
    }

    private Medicamento medicamento() {
        Categoria categoria = new Categoria(); categoria.setNome("Categoria"); categoria.setDescricao("Descricao");
        UnidadeMedida medida = new UnidadeMedida(); medida.setNome("Unidade"); medida.setSigla("un");
        Medicamento medicamento = new Medicamento(); medicamento.setId(1L); medicamento.setNome("Medicamento");
        medicamento.setApresentacao("Apresentacao"); medicamento.setCategoria(categoria); medicamento.setUnidadeMedida(medida);
        medicamento.setCriticidade(Medicamento.Criticidade.MEDIA); medicamento.setStatus(Medicamento.Status.ATIVO);
        return medicamento;
    }

    private static class IsolatedSaldoRepository implements SaldoEstoqueUseCase.SaldoEstoqueRepositoryPort {
        private final Medicamento medicamento;
        private IsolatedSaldoRepository(Medicamento medicamento) { this.medicamento = medicamento; }
        @Override public Optional<Medicamento> buscarMedicamentoPorId(Long id) { return Optional.of(medicamento); }
        @Override public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long unidadeId, Long medicamentoId) {
            return List.of(new SaldoLoteEstoque(medicamentoId, 2L, "LOT-1", LocalDate.now().plusDays(30),
                    unidadeId.equals(10L) ? 100 : 25));
        }
        @Override public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long medicamentoId) { return List.of(); }
        @Override public List<SaldoLoteEstoque> listarTodosOsSaldos() { return List.of(); }
        @Override public int consultarQuantidadeReservada(Long medicamentoId) { return 0; }
    }
}