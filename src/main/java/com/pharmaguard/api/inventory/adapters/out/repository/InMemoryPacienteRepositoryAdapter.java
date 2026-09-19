package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.application.PacienteUseCase.PacienteRepositoryPort;
import com.pharmaguard.api.inventory.domain.Paciente;
import java.util.List;
import java.util.Optional;

public class InMemoryPacienteRepositoryAdapter implements PacienteRepositoryPort {
    private final InMemoryInventoryStore store;
    public InMemoryPacienteRepositoryAdapter(InMemoryInventoryStore store) { this.store = store; }
    @Override public Paciente salvar(Paciente paciente) {
        if (paciente.getId() == null) { paciente.setId(store.nextId()); }
        store.pacientes.put(paciente.getId(), paciente); return paciente;
    }
    @Override public Optional<Paciente> buscarPorId(Long id) { return Optional.ofNullable(store.pacientes.get(id)); }
    @Override public boolean existePorCpf(String cpf) { return store.pacientes.values().stream().anyMatch(p -> p.getCpf().equals(cpf)); }
    @Override public List<Paciente> listar(String cpf, String nome) {
        return store.pacientes.values().stream()
                .filter(paciente -> cpf == null || paciente.getCpf().equals(cpf))
                .filter(paciente -> nome == null || paciente.getNome().toLowerCase().contains(nome.toLowerCase()))
                .toList();
    }
}