package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import java.util.List;
import java.util.Optional;

public interface UnidadeMedidaUseCase {

    UnidadeMedida criar(UnidadeMedida unidadeMedida);

    UnidadeMedida atualizar(UnidadeMedida unidadeMedida);

    void remover(Long id);

    UnidadeMedida buscarPorId(Long id);

    List<UnidadeMedida> listarTodos();

    interface UnidadeMedidaRepositoryPort {

        UnidadeMedida salvar(UnidadeMedida unidadeMedida);

        UnidadeMedida atualizar(UnidadeMedida unidadeMedida);

        void remover(Long id);

        Optional<UnidadeMedida> buscarPorId(Long id);

        List<UnidadeMedida> listarTodos();

        boolean existePorSigla(String sigla);
    }
}
