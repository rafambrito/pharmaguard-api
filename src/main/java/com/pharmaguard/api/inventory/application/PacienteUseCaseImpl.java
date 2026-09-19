package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Paciente;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PacienteUseCaseImpl implements PacienteUseCase {
    private final PacienteRepositoryPort repository;

    public PacienteUseCaseImpl(PacienteRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
    }

    @Override
    public Paciente criar(Paciente paciente) {
        Objects.requireNonNull(paciente, "paciente e obrigatorio");
        if (repository.existePorCpf(paciente.getCpf())) {
            throw new BusinessException("paciente ja cadastrado para o cpf informado");
        }
        paciente.setStatus(Paciente.Status.ATIVO);
        paciente.setDataCriacao(LocalDateTime.now());
        return repository.salvar(paciente);
    }

    @Override
    public Paciente atualizar(Paciente paciente) {
        Objects.requireNonNull(paciente, "paciente e obrigatorio");
        Objects.requireNonNull(paciente.getId(), "id do paciente e obrigatorio");
        Paciente existente = buscarPorId(paciente.getId());
        if (!existente.getCpf().equals(paciente.getCpf()) && repository.existePorCpf(paciente.getCpf())) {
            throw new BusinessException("paciente ja cadastrado para o cpf informado");
        }
        paciente.setStatus(existente.getStatus());
        paciente.setDataCriacao(existente.getDataCriacao());
        paciente.setDataUltimaAlteracao(LocalDateTime.now());
        return repository.salvar(paciente);
    }

    @Override
    public Paciente buscarPorId(Long id) {
        Objects.requireNonNull(id, "id do paciente e obrigatorio");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("paciente nao encontrado"));
    }

    @Override
    public List<Paciente> listar(String cpf, String nome) {
        return repository.listar(normalizarCpf(cpf), normalizarTexto(nome));
    }

    @Override
    public void inativar(Long id) {
        Paciente paciente = buscarPorId(id);
        paciente.inativar();
        repository.salvar(paciente);
    }

    private String normalizarCpf(String cpf) {
        return cpf == null || cpf.isBlank() ? null : cpf.replaceAll("\\D", "");
    }

    private String normalizarTexto(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}