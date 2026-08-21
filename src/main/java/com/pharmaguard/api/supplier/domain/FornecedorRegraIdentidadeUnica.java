package com.pharmaguard.api.supplier.domain;

import java.util.Objects;

public class FornecedorRegraIdentidadeUnica {

    public void validarParaCriacao(Fornecedor fornecedor, FornecedorIdentidadeUnicaPort unicidadePort) {
        Objects.requireNonNull(fornecedor, "fornecedor e obrigatorio");
        Objects.requireNonNull(unicidadePort, "unicidadePort e obrigatorio");

        if (fornecedor.getCodigo() != null && unicidadePort.existePorCodigo(fornecedor.getCodigo())) {
            throw new IllegalArgumentException("codigo de fornecedor ja cadastrado");
        }

        if (fornecedor.getDocumento() != null && unicidadePort.existePorDocumento(fornecedor.getDocumento())) {
            throw new IllegalArgumentException("documento de fornecedor ja cadastrado");
        }
    }
}