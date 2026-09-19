package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.adapters.out.repository.entity.PacienteEntity;
import com.pharmaguard.api.inventory.application.PacienteUseCase.PacienteRepositoryPort;
import com.pharmaguard.api.inventory.domain.Paciente;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
public class PacienteJpaAdapter implements PacienteRepositoryPort {
    private final PacienteJpaRepository jpa;
    public PacienteJpaAdapter(PacienteJpaRepository jpa) { this.jpa = jpa; }
    @Override public Paciente salvar(Paciente paciente) { return toDomain(jpa.save(toEntity(paciente))); }
    @Override public Optional<Paciente> buscarPorId(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public boolean existePorCpf(String cpf) { return jpa.existsByCpf(cpf); }
    @Override public List<Paciente> listar(String cpf, String nome) {
        List<PacienteEntity> pacientes = cpf != null ? jpa.findAllByCpf(cpf)
                : nome != null ? jpa.findAllByNomeContainingIgnoreCaseOrderByNome(nome) : jpa.findAll();
        return pacientes.stream().map(this::toDomain).toList();
    }
    private PacienteEntity toEntity(Paciente paciente) {
        PacienteEntity entity = new PacienteEntity();
        entity.setId(paciente.getId()); entity.setNome(paciente.getNome()); entity.setCpf(paciente.getCpf());
        entity.setDataNascimento(paciente.getDataNascimento()); entity.setCartaoSus(paciente.getCartaoSus());
        entity.setTelefone(paciente.getTelefone()); entity.setEmail(paciente.getEmail()); entity.setCidade(paciente.getCidade());
        entity.setUf(paciente.getUf()); entity.setStatus(paciente.getStatus()); entity.setDataCriacao(paciente.getDataCriacao());
        entity.setDataUltimaAlteracao(paciente.getDataUltimaAlteracao()); return entity;
    }
    private Paciente toDomain(PacienteEntity entity) {
        Paciente paciente = new Paciente();
        paciente.setId(entity.getId()); paciente.setNome(entity.getNome()); paciente.setCpf(entity.getCpf());
        paciente.setDataNascimento(entity.getDataNascimento()); paciente.setCartaoSus(entity.getCartaoSus());
        paciente.setTelefone(entity.getTelefone()); paciente.setEmail(entity.getEmail()); paciente.setCidade(entity.getCidade());
        paciente.setUf(entity.getUf()); paciente.setStatus(entity.getStatus()); paciente.setDataCriacao(entity.getDataCriacao());
        paciente.setDataUltimaAlteracao(entity.getDataUltimaAlteracao()); return paciente;
    }
}