package com.pharmaguard.api.supplier.application;

import com.pharmaguard.api.supplier.domain.ContatoFornecedor;
import java.util.List;
import java.util.Optional;

public interface ContatoFornecedorUseCase {

    ContatoFornecedor salvar(Long fornecedorId, ContatoFornecedor contato);

    ContatoFornecedor atualizar(Long fornecedorId, ContatoFornecedor contato);

    void deletar(Long fornecedorId, Long contatoId);

    Optional<ContatoFornecedor> buscarPorId(Long fornecedorId, Long contatoId);

    List<ContatoFornecedor> listarPorFornecedor(Long fornecedorId);

    interface ContatoFornecedorRepositoryPort {

        Optional<com.pharmaguard.api.supplier.domain.Fornecedor> buscarFornecedorPorId(Long fornecedorId);

        Optional<ContatoFornecedor> buscarPorId(Long fornecedorId, Long contatoId);

        List<ContatoFornecedor> listarPorFornecedor(Long fornecedorId);

        ContatoFornecedor salvar(Long fornecedorId, ContatoFornecedor contato);

        ContatoFornecedor atualizar(Long fornecedorId, ContatoFornecedor contato);

        void deletar(Long fornecedorId, Long contatoId);
    }
}