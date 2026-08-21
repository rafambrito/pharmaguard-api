package com.pharmaguard.api.supplier.support;

import com.pharmaguard.api.supplier.application.ContatoFornecedorUseCase;
import com.pharmaguard.api.supplier.application.FornecedorUseCase;
import com.pharmaguard.api.supplier.application.LeadTimeFornecedorUseCase;
import com.pharmaguard.api.supplier.domain.ContatoFornecedor;
import com.pharmaguard.api.supplier.domain.Fornecedor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SupplierTestSupport {

    private SupplierTestSupport() {
    }

    public static SupplierStore store() {
        return new SupplierStore();
    }

    public static final class SupplierStore {
        public final Map<Long, Fornecedor> fornecedores = new HashMap<>();
        public final Map<Long, List<ContatoFornecedor>> contatosPorFornecedor = new HashMap<>();
        private long nextId = 1L;

        public Long nextId() {
            return nextId++;
        }
    }

    public static final class FornecedorRepositoryAdapter implements FornecedorUseCase.FornecedorRepositoryPort {
        private final SupplierStore store;

        public FornecedorRepositoryAdapter(SupplierStore store) {
            this.store = store;
        }

        @Override
        public Fornecedor salvar(Fornecedor fornecedor) {
            if (fornecedor.getId() == null) {
                fornecedor.setId(store.nextId());
            }
            store.fornecedores.put(fornecedor.getId(), fornecedor);
            return fornecedor;
        }

        @Override
        public Fornecedor atualizar(Fornecedor fornecedor) {
            return salvar(fornecedor);
        }

        @Override
        public void deletar(Long id) {
            store.fornecedores.remove(id);
            store.contatosPorFornecedor.remove(id);
        }

        @Override
        public Optional<Fornecedor> buscarPorId(Long id) {
            return Optional.ofNullable(store.fornecedores.get(id));
        }

        @Override
        public List<Fornecedor> buscarTodos() {
            return new ArrayList<>(store.fornecedores.values());
        }

        @Override
        public Optional<Fornecedor> buscarPorCodigo(String codigo) {
            return store.fornecedores.values().stream()
                    .filter(fornecedor -> fornecedor.getCodigo() != null && fornecedor.getCodigo().equalsIgnoreCase(codigo))
                    .findFirst();
        }

        @Override
        public Optional<Fornecedor> buscarPorDocumento(String documento) {
            return store.fornecedores.values().stream()
                    .filter(fornecedor -> fornecedor.getDocumento() != null && fornecedor.getDocumento().equalsIgnoreCase(documento))
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
    }

    public static final class ContatoFornecedorRepositoryAdapter implements ContatoFornecedorUseCase.ContatoFornecedorRepositoryPort {
        private final SupplierStore store;

        public ContatoFornecedorRepositoryAdapter(SupplierStore store) {
            this.store = store;
        }

        @Override
        public Optional<Fornecedor> buscarFornecedorPorId(Long fornecedorId) {
            return Optional.ofNullable(store.fornecedores.get(fornecedorId));
        }

        @Override
        public Optional<ContatoFornecedor> buscarPorId(Long fornecedorId, Long contatoId) {
            return listarPorFornecedor(fornecedorId).stream()
                    .filter(contato -> contato.getId().equals(contatoId))
                    .findFirst();
        }

        @Override
        public List<ContatoFornecedor> listarPorFornecedor(Long fornecedorId) {
            return new ArrayList<>(store.contatosPorFornecedor.getOrDefault(fornecedorId, List.of()));
        }

        @Override
        public ContatoFornecedor salvar(Long fornecedorId, ContatoFornecedor contato) {
            if (contato.getId() == null) {
                contato.setId(store.nextId());
            }
            contato.setFornecedor(store.fornecedores.get(fornecedorId));
            store.contatosPorFornecedor.computeIfAbsent(fornecedorId, chave -> new ArrayList<>()).add(contato);
            return contato;
        }

        @Override
        public ContatoFornecedor atualizar(Long fornecedorId, ContatoFornecedor contato) {
            List<ContatoFornecedor> contatos = store.contatosPorFornecedor.getOrDefault(fornecedorId, new ArrayList<>());
            contatos.removeIf(item -> item.getId().equals(contato.getId()));
            contatos.add(contato);
            store.contatosPorFornecedor.put(fornecedorId, contatos);
            return contato;
        }

        @Override
        public void deletar(Long fornecedorId, Long contatoId) {
            List<ContatoFornecedor> contatos = store.contatosPorFornecedor.getOrDefault(fornecedorId, new ArrayList<>());
            contatos.removeIf(item -> item.getId().equals(contatoId));
            store.contatosPorFornecedor.put(fornecedorId, contatos);
        }
    }

    public static final class LeadTimeFornecedorRepositoryAdapter implements LeadTimeFornecedorUseCase.LeadTimeFornecedorRepositoryPort {
        private final SupplierStore store;

        public LeadTimeFornecedorRepositoryAdapter(SupplierStore store) {
            this.store = store;
        }

        @Override
        public Optional<Fornecedor> buscarPorId(Long fornecedorId) {
            return Optional.ofNullable(store.fornecedores.get(fornecedorId));
        }

        @Override
        public Fornecedor atualizar(Fornecedor fornecedor) {
            store.fornecedores.put(fornecedor.getId(), fornecedor);
            return fornecedor;
        }
    }
}