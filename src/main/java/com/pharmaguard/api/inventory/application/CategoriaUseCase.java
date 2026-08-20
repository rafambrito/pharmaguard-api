package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaUseCase {

    Categoria criar(Categoria categoria);

    Categoria atualizar(Categoria categoria);

    void remover(Long id);

    Categoria buscarPorId(Long id);

    List<Categoria> listarTodos();

    interface CategoriaRepositoryPort {

        Categoria salvar(Categoria categoria);

        Categoria atualizar(Categoria categoria);

        void remover(Long id);

        Optional<Categoria> buscarPorId(Long id);

        List<Categoria> listarTodos();

        boolean existePorNome(String nome);
    }
}
