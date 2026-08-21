package com.pharmaguard.api.supplier.application;

import com.pharmaguard.api.supplier.domain.Fornecedor;
import java.util.List;
import java.util.Optional;

public interface FornecedorUseCase {

    Fornecedor salvar(Fornecedor fornecedor);

    Fornecedor atualizar(Fornecedor fornecedor);

    void deletar(Long id);

    Optional<Fornecedor> buscarPorId(Long id);

    List<Fornecedor> buscarTodos();

    Optional<Fornecedor> buscarPorCodigo(String codigo);

    Optional<Fornecedor> buscarPorDocumento(String documento);

    interface FornecedorRepositoryPort {

        Fornecedor salvar(Fornecedor fornecedor);

        Fornecedor atualizar(Fornecedor fornecedor);

        void deletar(Long id);

        Optional<Fornecedor> buscarPorId(Long id);

        List<Fornecedor> buscarTodos();

        Optional<Fornecedor> buscarPorCodigo(String codigo);

        Optional<Fornecedor> buscarPorDocumento(String documento);

        boolean existePorCodigo(String codigo);

        boolean existePorDocumento(String documento);
    }
}