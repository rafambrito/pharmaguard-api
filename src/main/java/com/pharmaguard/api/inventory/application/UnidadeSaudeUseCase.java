package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.UnidadeSaude;
import java.util.List;
import java.util.Optional;

public interface UnidadeSaudeUseCase {
    UnidadeSaude criar(UnidadeSaude unidade);
    UnidadeSaude atualizar(UnidadeSaude unidade);
    UnidadeSaude buscarPorId(Long id);
    List<UnidadeSaude> listarTodos();
    void inativar(Long id);

    interface UnidadeSaudeRepositoryPort {
        UnidadeSaude salvar(UnidadeSaude unidade);
        UnidadeSaude atualizar(UnidadeSaude unidade);
        Optional<UnidadeSaude> buscarPorId(Long id);
        List<UnidadeSaude> listarTodos();
        boolean existePorIdentificacao(String identificacao);
    }
}