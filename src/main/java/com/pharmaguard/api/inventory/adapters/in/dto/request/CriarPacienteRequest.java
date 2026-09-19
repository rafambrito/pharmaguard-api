package com.pharmaguard.api.inventory.adapters.in.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CriarPacienteRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 20) String cpf,
        @NotNull @PastOrPresent LocalDate dataNascimento,
        @Size(max = 30) String cartaoSus,
        @Size(max = 30) String telefone,
        @Size(max = 150) String email,
        @Size(max = 100) String cidade,
        @Size(max = 2) String uf) {
}