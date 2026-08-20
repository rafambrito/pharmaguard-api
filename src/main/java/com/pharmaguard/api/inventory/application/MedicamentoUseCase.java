package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import java.util.List;
import java.util.Optional;

public interface MedicamentoUseCase {

    Medicamento criar(Medicamento medicamento, Long categoriaId, Long unidadeMedidaId);

    Medicamento atualizar(Medicamento medicamento, Long categoriaId, Long unidadeMedidaId);

    void remover(Long id);

    Medicamento buscarPorId(Long id);

    List<Medicamento> listarTodos();

    interface MedicamentoRepositoryPort {

        Medicamento salvar(Medicamento medicamento);

        Medicamento atualizar(Medicamento medicamento);

        void remover(Long id);

        Optional<Medicamento> buscarPorId(Long id);

        List<Medicamento> listarTodos();

        boolean existePorNomeEApresentacao(String nome, String apresentacao);

        Optional<Categoria> buscarCategoriaPorId(Long id);

        Optional<UnidadeMedida> buscarUnidadeMedidaPorId(Long id);
    }
}
