package com.pharmaguard.api.supplier.adapters.out.repository;

import com.pharmaguard.api.supplier.application.ContatoFornecedorUseCase;
import com.pharmaguard.api.supplier.application.FornecedorUseCase;
import com.pharmaguard.api.supplier.application.LeadTimeFornecedorUseCase;
import com.pharmaguard.api.supplier.domain.ContatoFornecedor;
import com.pharmaguard.api.supplier.domain.Fornecedor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemorySupplierRepositoryAdapter implements
        FornecedorUseCase.FornecedorRepositoryPort,
        ContatoFornecedorUseCase.ContatoFornecedorRepositoryPort,
        LeadTimeFornecedorUseCase.LeadTimeFornecedorRepositoryPort {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, Fornecedor> fornecedores = new ConcurrentHashMap<>();
    private final Map<Long, ContatoFornecedor> contatos = new ConcurrentHashMap<>();

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        if (fornecedor.getId() == null) {
            fornecedor.setId(nextId());
        }
        fornecedores.put(fornecedor.getId(), fornecedor);
        return fornecedor;
    }

    @Override
    public Fornecedor atualizar(Fornecedor fornecedor) {
        fornecedores.put(fornecedor.getId(), fornecedor);
        return fornecedor;
    }

    @Override
    public void deletar(Long id) {
        fornecedores.remove(id);
        contatos.entrySet().removeIf(entry -> {
            ContatoFornecedor contato = entry.getValue();
            return contato.getFornecedor() != null && id.equals(contato.getFornecedor().getId());
        });
    }

    @Override
    public Optional<Fornecedor> buscarPorId(Long id) {
        return Optional.ofNullable(fornecedores.get(id));
    }

    @Override
    public List<Fornecedor> buscarTodos() {
        return new ArrayList<>(fornecedores.values());
    }

    @Override
    public Optional<Fornecedor> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return fornecedores.values().stream()
                .filter(fornecedor -> codigo.equalsIgnoreCase(fornecedor.getCodigo()))
                .findFirst();
    }

    @Override
    public Optional<Fornecedor> buscarPorDocumento(String documento) {
        if (documento == null) {
            return Optional.empty();
        }
        return fornecedores.values().stream()
                .filter(fornecedor -> documento.equals(fornecedor.getDocumento()))
                .findFirst();
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return buscarPorCodigo(codigo).isPresent();
    }

    @Override
    public boolean existePorDocumento(String documento) {
        return buscarPorDocumento(documento).isPresent();
    }

    @Override
    public Optional<Fornecedor> buscarFornecedorPorId(Long fornecedorId) {
        return buscarPorId(fornecedorId);
    }

    @Override
    public Optional<ContatoFornecedor> buscarPorId(Long fornecedorId, Long contatoId) {
        ContatoFornecedor contato = contatos.get(contatoId);
        if (contato == null || contato.getFornecedor() == null) {
            return Optional.empty();
        }
        if (!fornecedorId.equals(contato.getFornecedor().getId())) {
            return Optional.empty();
        }
        return Optional.of(contato);
    }

    @Override
    public List<ContatoFornecedor> listarPorFornecedor(Long fornecedorId) {
        return contatos.values().stream()
                .filter(contato -> contato.getFornecedor() != null)
                .filter(contato -> fornecedorId.equals(contato.getFornecedor().getId()))
                .toList();
    }

    @Override
    public ContatoFornecedor salvar(Long fornecedorId, ContatoFornecedor contato) {
        if (contato.getId() == null) {
            contato.setId(nextId());
        }
        contato.setFornecedor(
                buscarPorId(fornecedorId).orElseThrow(() -> new IllegalArgumentException("fornecedor nao encontrado")));
        contatos.put(contato.getId(), contato);
        return contato;
    }

    @Override
    public ContatoFornecedor atualizar(Long fornecedorId, ContatoFornecedor contato) {
        contato.setFornecedor(
                buscarPorId(fornecedorId).orElseThrow(() -> new IllegalArgumentException("fornecedor nao encontrado")));
        contatos.put(contato.getId(), contato);
        return contato;
    }

    @Override
    public void deletar(Long fornecedorId, Long contatoId) {
        buscarPorId(fornecedorId, contatoId).ifPresent(contato -> contatos.remove(contato.getId()));
    }

    private Long nextId() {
        return sequence.getAndIncrement();
    }
}