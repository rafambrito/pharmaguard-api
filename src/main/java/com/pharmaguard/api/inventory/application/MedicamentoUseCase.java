package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.CategoriaMedicamento;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import java.util.List;
import java.util.Optional;

public interface MedicamentoUseCase {

    Medicamento criar(Medicamento medicamento, CategoriaMedicamento categoria, Long unidadeMedidaId);

    Medicamento atualizar(Medicamento medicamento, CategoriaMedicamento categoria, Long unidadeMedidaId);

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

        Optional<Categoria> buscarCategoriaPorNome(String nome);

        Categoria salvarCategoria(Categoria categoria);

        Optional<UnidadeMedida> buscarUnidadeMedidaPorId(Long id);
    }
}
