package com.pharmaguard.api.supplier.adapters.in.dto.response;

import java.time.LocalDateTime;

public record ContatoFornecedorResponse(
        Long id,
        String nome,
        String cargo,
        String telefone,
        String email,
        String canalPrincipal,
        boolean ativo,
        Long fornecedorId,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaAlteracao) {
}