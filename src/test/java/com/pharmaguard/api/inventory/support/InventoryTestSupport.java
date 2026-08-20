package com.pharmaguard.api.inventory.support;

import com.pharmaguard.api.inventory.application.CategoriaUseCase;
import com.pharmaguard.api.inventory.application.LoteUseCase;
import com.pharmaguard.api.inventory.application.MedicamentoUseCase;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCase;
import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InventoryTestSupport {

    private InventoryTestSupport() {
    }

    public static InventoryStore store() {
        return new InventoryStore();
    }

    public static final class InventoryStore {
        public final Map<Long, Categoria> categorias = new HashMap<>();
        public final Map<Long, UnidadeMedida> unidadesMedida = new HashMap<>();
        public final Map<Long, Medicamento> medicamentos = new HashMap<>();
        public final Map<Long, Lote> lotes = new HashMap<>();
        private long nextId = 1L;

        public Long nextId() {
            return nextId++;
        }
    }

    public static final class CategoriaRepositoryAdapter implements CategoriaUseCase.CategoriaRepositoryPort {
        private final InventoryStore store;

        public CategoriaRepositoryAdapter(InventoryStore store) {
            this.store = store;
        }

        @Override
        public Categoria salvar(Categoria categoria) {
            return save(categoria);
        }

        @Override
        public Categoria atualizar(Categoria categoria) {
            return save(categoria);
        }

        @Override
        public void remover(Long id) {
            store.categorias.remove(id);
        }

        @Override
        public Optional<Categoria> buscarPorId(Long id) {
            return Optional.ofNullable(store.categorias.get(id));
        }

        @Override
        public List<Categoria> listarTodos() {
            return new ArrayList<>(store.categorias.values());
        }

        @Override
        public boolean existePorNome(String nome) {
            return store.categorias.values().stream().anyMatch(categoria -> categoria.getNome().equalsIgnoreCase(nome));
        }

        private Categoria save(Categoria categoria) {
            if (categoria.getId() == null) {
                categoria.setId(store.nextId());
            }
            store.categorias.put(categoria.getId(), categoria);
            return categoria;
        }
    }

    public static final class UnidadeMedidaRepositoryAdapter implements UnidadeMedidaUseCase.UnidadeMedidaRepositoryPort {
        private final InventoryStore store;

        public UnidadeMedidaRepositoryAdapter(InventoryStore store) {
            this.store = store;
        }

        @Override
        public UnidadeMedida salvar(UnidadeMedida unidadeMedida) {
            return save(unidadeMedida);
        }

        @Override
        public UnidadeMedida atualizar(UnidadeMedida unidadeMedida) {
            return save(unidadeMedida);
        }

        @Override
        public void remover(Long id) {
            store.unidadesMedida.remove(id);
        }

        @Override
        public Optional<UnidadeMedida> buscarPorId(Long id) {
            return Optional.ofNullable(store.unidadesMedida.get(id));
        }

        @Override
        public List<UnidadeMedida> listarTodos() {
            return new ArrayList<>(store.unidadesMedida.values());
        }

        @Override
        public boolean existePorSigla(String sigla) {
            return store.unidadesMedida.values().stream().anyMatch(unidadeMedida -> unidadeMedida.getSigla().equalsIgnoreCase(sigla));
        }

        private UnidadeMedida save(UnidadeMedida unidadeMedida) {
            if (unidadeMedida.getId() == null) {
                unidadeMedida.setId(store.nextId());
            }
            store.unidadesMedida.put(unidadeMedida.getId(), unidadeMedida);
            return unidadeMedida;
        }
    }

    public static final class MedicamentoRepositoryAdapter implements MedicamentoUseCase.MedicamentoRepositoryPort {
        private final InventoryStore store;

        public MedicamentoRepositoryAdapter(InventoryStore store) {
            this.store = store;
        }

        @Override
        public Medicamento salvar(Medicamento medicamento) {
            return save(medicamento);
        }

        @Override
        public Medicamento atualizar(Medicamento medicamento) {
            return save(medicamento);
        }

        @Override
        public void remover(Long id) {
            store.medicamentos.remove(id);
            store.lotes.entrySet().removeIf(entry -> id.equals(entry.getValue().getMedicamento().getId()));
        }

        @Override
        public Optional<Medicamento> buscarPorId(Long id) {
            return Optional.ofNullable(store.medicamentos.get(id));
        }

        @Override
        public List<Medicamento> listarTodos() {
            return new ArrayList<>(store.medicamentos.values());
        }

        @Override
        public boolean existePorNomeEApresentacao(String nome, String apresentacao) {
            return store.medicamentos.values().stream().anyMatch(medicamento ->
                    medicamento.getNome().equalsIgnoreCase(nome)
                            && medicamento.getApresentacao().equalsIgnoreCase(apresentacao));
        }

        @Override
        public Optional<Categoria> buscarCategoriaPorId(Long id) {
            return Optional.ofNullable(store.categorias.get(id));
        }

        @Override
        public Optional<UnidadeMedida> buscarUnidadeMedidaPorId(Long id) {
            return Optional.ofNullable(store.unidadesMedida.get(id));
        }

        private Medicamento save(Medicamento medicamento) {
            if (medicamento.getId() == null) {
                medicamento.setId(store.nextId());
            }
            store.medicamentos.put(medicamento.getId(), medicamento);
            return medicamento;
        }
    }

    public static final class LoteRepositoryAdapter implements LoteUseCase.LoteRepositoryPort {
        private final InventoryStore store;

        public LoteRepositoryAdapter(InventoryStore store) {
            this.store = store;
        }

        @Override
        public Lote salvar(Lote lote) {
            if (lote.getId() == null) {
                lote.setId(store.nextId());
            }
            store.lotes.put(lote.getId(), lote);
            return lote;
        }

        @Override
        public void remover(Long id) {
            store.lotes.remove(id);
        }

        @Override
        public Optional<Lote> buscarPorMedicamentoIdEId(Long medicamentoId, Long loteId) {
            Lote lote = store.lotes.get(loteId);
            if (lote == null || !medicamentoId.equals(lote.getMedicamento().getId())) {
                return Optional.empty();
            }
            return Optional.of(lote);
        }

        @Override
        public List<Lote> listarPorMedicamento(Long medicamentoId) {
            return store.lotes.values().stream()
                    .filter(lote -> medicamentoId.equals(lote.getMedicamento().getId()))
                    .toList();
        }

        @Override
        public boolean existePorNumeroLoteEMedicamento(String numeroLote, Long medicamentoId) {
            return store.lotes.values().stream().anyMatch(lote ->
                    medicamentoId.equals(lote.getMedicamento().getId())
                            && lote.getNumeroLote().equalsIgnoreCase(numeroLote));
        }

        @Override
        public Optional<Medicamento> buscarMedicamentoPorId(Long id) {
            return Optional.ofNullable(store.medicamentos.get(id));
        }
    }
}
