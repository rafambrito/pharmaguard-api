package com.pharmaguard.api.inventory.domain;

import java.util.Objects;

public class CategoriaRegraIdentidadeUnica {

    public void validarParaCriacao(Categoria categoria, CategoriaIdentidadeUnicaPort unicidadePort) {
        Objects.requireNonNull(categoria, "categoria e obrigatorio");
        Objects.requireNonNull(unicidadePort, "unicidadePort e obrigatorio");

        if (unicidadePort.existePorNome(categoria.getNome())) {
            throw new IllegalArgumentException("nome de categoria ja cadastrado");
        }
    }
}
