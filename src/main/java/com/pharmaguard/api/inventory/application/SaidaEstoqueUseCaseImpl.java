package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.RegraFefo;
import com.pharmaguard.api.inventory.domain.RegraSaldoEstoque;
import com.pharmaguard.api.inventory.domain.SaldoLoteEstoque;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

public class SaidaEstoqueUseCaseImpl implements SaidaEstoqueUseCase {

    private final SaidaEstoqueRepositoryPort repository;
    private final RegraSaldoEstoque regraSaldoEstoque;
    private final RegraFefo regraFefo;

    public SaidaEstoqueUseCaseImpl(SaidaEstoqueRepositoryPort repository) {
        this(repository, new RegraSaldoEstoque(), new RegraFefo());
    }

    public SaidaEstoqueUseCaseImpl(SaidaEstoqueRepositoryPort repository,
            RegraSaldoEstoque regraSaldoEstoque,
            RegraFefo regraFefo) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
        this.regraSaldoEstoque = Objects.requireNonNull(regraSaldoEstoque, "regraSaldoEstoque e obrigatoria");
        this.regraFefo = Objects.requireNonNull(regraFefo, "regraFefo e obrigatoria");
    }

    @Override
    public SaidaEstoque registrar(Long unidadeId, Long medicamentoId, SaidaEstoque saida) {
        Objects.requireNonNull(medicamentoId, "medicamentoId e obrigatorio");
        validarUnidade(unidadeId);
        Objects.requireNonNull(saida, "saida e obrigatoria");
        validarUnidadeAtiva(unidadeId);

        Medicamento medicamento = repository.buscarMedicamentoPorId(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("medicamento nao encontrado"));

        List<SaldoLoteEstoque> saldosPorLote = repository.listarSaldosPorMedicamento(unidadeId, medicamentoId);
        regraSaldoEstoque.validarSaidaComSaldo(saida.getQuantidadeTotal(), saldosPorLote);

        List<RegraFefo.ItemBaixaFefo> planoBaixa = regraFefo.planejarBaixa(saida.getQuantidadeTotal(), saldosPorLote);
        if (planoBaixa.isEmpty()) {
            throw new BusinessException("nao foi possivel calcular lotes validos para saida");
        }

        saida.setMedicamento(medicamento);
        saida.setUnidadeSaude(new com.pharmaguard.api.inventory.domain.UnidadeSaude(unidadeId));
        saida.registrarMomentoSaida();

        for (RegraFefo.ItemBaixaFefo itemBaixa : planoBaixa) {
            int saldoAposMovimentacao = repository.baixarSaldoLote(unidadeId, itemBaixa.getLoteId(), itemBaixa.getQuantidadeConsumida());
            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
            movimentacao.setTipo(MovimentacaoEstoque.Tipo.SAIDA);
            movimentacao.setMedicamento(medicamento);
            movimentacao.setLote(new com.pharmaguard.api.inventory.domain.Lote());
            movimentacao.setUnidadeSaude(saida.getUnidadeSaude());
            movimentacao.getLote().setId(itemBaixa.getLoteId());
            movimentacao.getLote().setNumeroLote(itemBaixa.getNumeroLote());
            movimentacao.setQuantidade(itemBaixa.getQuantidadeConsumida());
            movimentacao.setSaldoAposMovimentacao(saldoAposMovimentacao);
            movimentacao.setMotivo(saida.getMotivo().name());
            movimentacao.setUsuarioResponsavelId(saida.getUsuarioResponsavelId());
            movimentacao.registrarMomentoMovimentacao();
            repository.salvarMovimentacao(movimentacao);
        }

        List<SaidaEstoque.LoteUtilizado> lotesUtilizados = planoBaixa.stream()
                .map(itemBaixa -> new SaidaEstoque.LoteUtilizado(
                        itemBaixa.getLoteId(),
                        itemBaixa.getNumeroLote(),
                        itemBaixa.getQuantidadeConsumida()))
                .toList();

        saida.definirLotesUtilizados(lotesUtilizados);

        return repository.salvar(saida);
    }

    @Override
    public SaidaEstoque buscarPorId(Long id) {
        Objects.requireNonNull(id, "id e obrigatorio");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("saida de estoque nao encontrada"));
    }

    @Override
    public List<SaidaEstoque> listar(Long medicamentoId) {
        if (medicamentoId != null) {
            repository.buscarMedicamentoPorId(medicamentoId)
                    .orElseThrow(() -> new ResourceNotFoundException("medicamento nao encontrado"));
        }
        return repository.listar(medicamentoId);
    }

    @Override
    public List<SaidaEstoque> listar(Long unidadeId, Long medicamentoId) {
        validarUnidade(unidadeId);
        validarUnidadeAtiva(unidadeId);
        return repository.listar(unidadeId, medicamentoId);
    }

    private void validarUnidadeAtiva(Long unidadeId) {
        if (!repository.unidadeAtiva(unidadeId)) {
            throw new ResourceNotFoundException("unidade de saude nao encontrada ou inativa");
        }
    }

    private void validarUnidade(Long unidadeId) {
        if (unidadeId == null || unidadeId <= 0) {
            throw new IllegalArgumentException("unidadeId deve ser maior que zero");
        }
    }
}
