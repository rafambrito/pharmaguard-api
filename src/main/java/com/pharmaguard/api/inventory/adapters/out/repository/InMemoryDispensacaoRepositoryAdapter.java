package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.DispensacaoUseCase.DispensacaoRepositoryPort;
import com.pharmaguard.api.inventory.domain.Dispensacao;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class InMemoryDispensacaoRepositoryAdapter implements DispensacaoRepositoryPort {
    private final InMemoryInventoryStore store;
    public InMemoryDispensacaoRepositoryAdapter(InMemoryInventoryStore store) { this.store = store; }
    @Override public Dispensacao salvar(Dispensacao dispensacao) {
        if (dispensacao.getId() == null) dispensacao.setId(store.nextId());
        store.dispensacoes.put(dispensacao.getId(), dispensacao); return dispensacao;
    }
    @Override public Optional<Dispensacao> buscarPorId(Long id) { return Optional.ofNullable(store.dispensacoes.get(id)); }
    @Override public List<Dispensacao> listar(Long unidadeId, Long pacienteId, Long medicamentoId, LocalDate dataInicial, LocalDate dataFinal) {
        return store.dispensacoes.values().stream()
                .filter(item -> unidadeId == null || unidadeId.equals(item.getSaidaEstoque().getUnidadeSaude().getId()))
                .filter(item -> pacienteId == null || pacienteId.equals(item.getPaciente().getId()))
                .filter(item -> medicamentoId == null || medicamentoId.equals(item.getSaidaEstoque().getMedicamento().getId()))
                .filter(item -> dataInicial == null || !item.getSaidaEstoque().getDataSaida().toLocalDate().isBefore(dataInicial))
                .filter(item -> dataFinal == null || !item.getSaidaEstoque().getDataSaida().toLocalDate().isAfter(dataFinal))
                .sorted((left, right) -> right.getSaidaEstoque().getDataSaida().compareTo(left.getSaidaEstoque().getDataSaida())).toList();
    }
}