package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteUseCase {
    Paciente criar(Paciente paciente);
    Paciente atualizar(Paciente paciente);
    Paciente buscarPorId(Long id);
    List<Paciente> listar(String cpf, String nome);
    void inativar(Long id);

    interface PacienteRepositoryPort {
        Paciente salvar(Paciente paciente);
        Optional<Paciente> buscarPorId(Long id);
        List<Paciente> listar(String cpf, String nome);
        boolean existePorCpf(String cpf);
    }
}