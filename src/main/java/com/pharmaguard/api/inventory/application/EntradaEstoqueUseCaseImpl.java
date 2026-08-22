package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

public class EntradaEstoqueUseCaseImpl implements EntradaEstoqueUseCase {

    private final EntradaEstoqueRepositoryPort repository;

    public EntradaEstoqueUseCaseImpl(EntradaEstoqueRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public EntradaEstoque registrar(Long medicamentoId, Long loteId, EntradaEstoque entrada) {
        Objects.requireNonNull(medicamentoId, "medicamentoId e obrigatorio");
        Objects.requireNonNull(loteId, "loteId e obrigatorio");
        Objects.requireNonNull(entrada, "entrada e obrigatoria");

        Medicamento medicamento = repository.buscarMedicamentoPorId(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("medicamento nao encontrado"));

        Lote lote = repository.buscarLotePorMedicamentoIdEId(medicamentoId, loteId)
                .orElseThrow(() -> new ResourceNotFoundException("lote nao encontrado para o medicamento informado"));

        entrada.setMedicamento(medicamento);
        entrada.setLote(lote);
        entrada.registrarMomentoEntrada();
        EntradaEstoque entradaSalva = repository.salvar(entrada);

        int saldoAposMovimentacao = repository.creditarSaldoLote(lote.getId(), entradaSalva.getQuantidade());
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setTipo(MovimentacaoEstoque.Tipo.ENTRADA);
        movimentacao.setMedicamento(medicamento);
        movimentacao.setLote(lote);
        movimentacao.setQuantidade(entradaSalva.getQuantidade());
        movimentacao.setSaldoAposMovimentacao(saldoAposMovimentacao);
        movimentacao.setMotivo(entradaSalva.getOrigem().name());
        movimentacao.setUsuarioResponsavelId(entradaSalva.getUsuarioResponsavelId());
        movimentacao.registrarMomentoMovimentacao();
        repository.salvarMovimentacao(movimentacao);

        return entradaSalva;
    }

    @Override
    public EntradaEstoque buscarPorId(Long id) {
        Objects.requireNonNull(id, "id e obrigatorio");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("entrada de estoque nao encontrada"));
    }

    @Override
    public List<EntradaEstoque> listar(Long medicamentoId, Long loteId) {
        if (medicamentoId != null) {
            repository.buscarMedicamentoPorId(medicamentoId)
                    .orElseThrow(() -> new ResourceNotFoundException("medicamento nao encontrado"));
        }
        return repository.listar(medicamentoId, loteId);
    }
}
