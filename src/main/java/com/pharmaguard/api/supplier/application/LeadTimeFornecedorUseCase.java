package com.pharmaguard.api.supplier.application;

import com.pharmaguard.api.supplier.domain.Fornecedor;
import java.util.Optional;

public interface LeadTimeFornecedorUseCase {

    Fornecedor atualizar(Long fornecedorId, Integer leadTimeDias);

    Optional<Fornecedor> consultar(Long fornecedorId);

    interface LeadTimeFornecedorRepositoryPort {

        Optional<Fornecedor> buscarPorId(Long fornecedorId);

        Fornecedor atualizar(Fornecedor fornecedor);
    }
}