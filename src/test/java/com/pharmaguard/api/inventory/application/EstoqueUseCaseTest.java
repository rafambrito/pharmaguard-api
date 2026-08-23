package com.pharmaguard.api.inventory.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.RegraFefo;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EstoqueUseCaseTest {

    @Test
    void deveRegistrarEntradaEAtualizarSaldoEHistorico() {
        Medicamento medicamento = medicamento(1L);
        Lote lote = lote(2L, medicamento, 100, 30);
        FakeEntradaRepository repository = new FakeEntradaRepository(medicamento, lote);
        EntradaEstoqueUseCase useCase = new EntradaEstoqueUseCaseImpl(repository);
        EntradaEstoque entrada = new EntradaEstoque();
        entrada.setQuantidade(20);
        entrada.setOrigem(EntradaEstoque.Origem.FORNECEDOR);
        entrada.setDocumento("NF-1");
        entrada.setUsuarioResponsavelId(5L);

        EntradaEstoque registrada = useCase.registrar(1L, 2L, entrada);

        assertThat(registrada.getId()).isNotNull();
        assertThat(repository.saldo).isEqualTo(120);
        assertThat(repository.movimentacoes).hasSize(1);
        assertThat(repository.movimentacoes.get(0).getTipo()).isEqualTo(MovimentacaoEstoque.Tipo.ENTRADA);
    }

    @Test
    void deveRegistrarSaidaAplicandoFefoERegistrarMovimentacoes() {
        Medicamento medicamento = medicamento(1L);
        FakeSaidaRepository repository = new FakeSaidaRepository(medicamento, List.of(
                new SaldoLoteEstoque(10L, "LOT-OLD", LocalDate.now().plusDays(10), 10),
                new SaldoLoteEstoque(11L, "LOT-NEW", LocalDate.now().plusDays(20), 20)));
        SaidaEstoqueUseCase useCase = new SaidaEstoqueUseCaseImpl(repository);
        SaidaEstoque saida = new SaidaEstoque();
        saida.setQuantidadeTotal(15);
        saida.setMotivo(SaidaEstoque.Motivo.DISPENSACAO);
        saida.setUsuarioResponsavelId(5L);

        SaidaEstoque registrada = useCase.registrar(1L, saida);

        assertThat(registrada.getLotesUtilizados()).extracting(SaidaEstoque.LoteUtilizado::getNumeroLote)
                .containsExactly("LOT-OLD", "LOT-NEW");
        assertThat(registrada.getLotesUtilizados()).extracting(SaidaEstoque.LoteUtilizado::getQuantidadeConsumida)
                .containsExactly(10, 5);
        assertThat(repository.movimentacoes).hasSize(2);
    }

    @Test
    void deveConsultarSaldoEHistoricoComValidacaoDeRecurso() {
        Medicamento medicamento = medicamento(1L);
        FakeSaldoRepository saldoRepository = new FakeSaldoRepository(medicamento,
                List.of(new SaldoLoteEstoque(2L, "LOT-1", LocalDate.now().plusDays(30), 50)));
        SaldoEstoqueUseCase saldoUseCase = new SaldoEstoqueUseCaseImpl(saldoRepository);

        assertThat(saldoUseCase.consultarPorMedicamento(1L).getQuantidadeDisponivel()).isEqualTo(50);
        assertThat(saldoUseCase.consultarLotesPorMedicamento(1L)).hasSize(1);
        assertThatThrownBy(() -> saldoUseCase.consultarPorMedicamento(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveRejeitarIntervaloDeHistoricoInvertido() {
        HistoricoEstoqueUseCase useCase = new HistoricoEstoqueUseCaseImpl(new FakeHistoricoRepository());

        assertThatThrownBy(() -> useCase.listar(null, null, null,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dataInicial");
    }

            @Test
            void deveRejeitarEntradaEmUnidadeInativa() {
            Medicamento medicamento = medicamento(1L);
            FakeEntradaRepository repository = new FakeEntradaRepository(medicamento,
                lote(2L, medicamento, 100, 30));
            repository.unidadeAtiva = false;

            EntradaEstoque entrada = new EntradaEstoque();
            entrada.setQuantidade(10);
            entrada.setOrigem(EntradaEstoque.Origem.FORNECEDOR);

            assertThatThrownBy(() -> new EntradaEstoqueUseCaseImpl(repository)
                .registrar(10L, 1L, 2L, entrada))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unidade de saude");
            }

            @Test
            void deveRejeitarSaidaEmUnidadeInativa() {
            FakeSaidaRepository repository = new FakeSaidaRepository(medicamento(1L),
                List.of(new SaldoLoteEstoque(10L, "LOT-OLD", LocalDate.now().plusDays(10), 10)), false);
            SaidaEstoque saida = new SaidaEstoque();
            saida.setQuantidadeTotal(1);
            saida.setMotivo(SaidaEstoque.Motivo.DISPENSACAO);

            assertThatThrownBy(() -> new SaidaEstoqueUseCaseImpl(repository)
                .registrar(10L, 1L, saida))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unidade de saude");
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

    private Lote lote(Long id, Medicamento medicamento, int quantidade, long dias) {
        return new Lote(id, "LOT-1", LocalDate.now().plusDays(dias), quantidade, medicamento);
    }

    private static class FakeEntradaRepository implements EntradaEstoqueUseCase.EntradaEstoqueRepositoryPort {
        private final Medicamento medicamento;
        private final Lote lote;
        private int saldo;
        private boolean unidadeAtiva = true;
        private final List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();

        private FakeEntradaRepository(Medicamento medicamento, Lote lote) {
            this.medicamento = medicamento;
            this.lote = lote;
            this.saldo = lote.getQuantidadeInicial();
        }

        public EntradaEstoque salvar(EntradaEstoque entrada) { entrada.setId(3L); return entrada; }
        public Optional<EntradaEstoque> buscarPorId(Long id) { return Optional.empty(); }
        public List<EntradaEstoque> listar(Long medicamentoId, Long loteId) { return List.of(); }
        public Optional<Medicamento> buscarMedicamentoPorId(Long id) { return Optional.ofNullable(id.equals(medicamento.getId()) ? medicamento : null); }
        @Override public boolean unidadeAtiva(Long id) { return unidadeAtiva; }
        public Optional<Lote> buscarLotePorMedicamentoIdEId(Long medicamentoId, Long loteId) { return Optional.of(lote); }
        public int creditarSaldoLote(Long loteId, int quantidade) { saldo += quantidade; return saldo; }
        public MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao) { movimentacoes.add(movimentacao); return movimentacao; }
    }

    private static class FakeSaidaRepository implements SaidaEstoqueUseCase.SaidaEstoqueRepositoryPort {
        private final Medicamento medicamento;
        private final List<SaldoLoteEstoque> saldos;
        private final List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();
        private final boolean unidadeAtiva;

        private FakeSaidaRepository(Medicamento medicamento, List<SaldoLoteEstoque> saldos) {
            this(medicamento, saldos, true);
        }

        private FakeSaidaRepository(Medicamento medicamento, List<SaldoLoteEstoque> saldos, boolean unidadeAtiva) {
            this.medicamento = medicamento;
            this.saldos = new ArrayList<>(saldos);
            this.unidadeAtiva = unidadeAtiva;
        }

        public SaidaEstoque salvar(SaidaEstoque saida) { saida.setId(20L); return saida; }
        public Optional<SaidaEstoque> buscarPorId(Long id) { return Optional.empty(); }
        public List<SaidaEstoque> listar(Long medicamentoId) { return List.of(); }
        public Optional<Medicamento> buscarMedicamentoPorId(Long id) { return Optional.of(medicamento); }
        @Override public boolean unidadeAtiva(Long id) { return unidadeAtiva; }
        public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long id) { return saldos; }
        public int baixarSaldoLote(Long loteId, int quantidade) {
            SaldoLoteEstoque saldo = saldos.stream().filter(item -> item.getLoteId().equals(loteId)).findFirst().orElseThrow();
            saldo.setQuantidadeDisponivel(saldo.getQuantidadeDisponivel() - quantidade);
            return saldo.getQuantidadeDisponivel();
        }
        public MovimentacaoEstoque salvarMovimentacao(MovimentacaoEstoque movimentacao) { movimentacoes.add(movimentacao); return movimentacao; }
    }

    private static class FakeSaldoRepository implements SaldoEstoqueUseCase.SaldoEstoqueRepositoryPort {
        private final Medicamento medicamento;
        private final List<SaldoLoteEstoque> saldos;
        private FakeSaldoRepository(Medicamento medicamento, List<SaldoLoteEstoque> saldos) { this.medicamento = medicamento; this.saldos = saldos; }
        public Optional<Medicamento> buscarMedicamentoPorId(Long id) { return Optional.ofNullable(id.equals(medicamento.getId()) ? medicamento : null); }
        public List<SaldoLoteEstoque> listarSaldosPorMedicamento(Long id) { return saldos; }
        public List<SaldoLoteEstoque> listarTodosOsSaldos() { return saldos; }
        public int consultarQuantidadeReservada(Long id) { return 0; }
    }

    private static class FakeHistoricoRepository implements HistoricoEstoqueUseCase.HistoricoEstoqueRepositoryPort {
        public List<MovimentacaoEstoque> listar(Long medicamentoId, Long loteId, MovimentacaoEstoque.Tipo tipo, LocalDate dataInicial, LocalDate dataFinal) { return List.of(); }
        public Optional<Medicamento> buscarMedicamentoPorId(Long id) { return Optional.empty(); }
    }
}
