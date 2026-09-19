package com.pharmaguard.api.inventory.adapters.in.dto.response;

import com.pharmaguard.api.inventory.domain.Paciente;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PacienteResponse(Long id, String nome, String cpf, LocalDate dataNascimento, String cartaoSus,
        String telefone, String email, String cidade, String uf, Paciente.Status status,
        LocalDateTime dataCriacao, LocalDateTime dataUltimaAlteracao) {
}