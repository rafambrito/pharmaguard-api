package com.pharmaguard.api.supplier.domain;

public interface FornecedorIdentidadeUnicaPort {

    boolean existePorCodigo(String codigo);

    boolean existePorDocumento(String documento);
}