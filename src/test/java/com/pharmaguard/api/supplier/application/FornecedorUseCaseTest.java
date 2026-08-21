package com.pharmaguard.api.supplier.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pharmaguard.api.supplier.domain.Fornecedor;
import com.pharmaguard.api.supplier.support.SupplierTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FornecedorUseCaseTest {

    private FornecedorUseCase fornecedorUseCase;

    @BeforeEach
    void setUp() {
        SupplierTestSupport.SupplierStore store = SupplierTestSupport.store();
        fornecedorUseCase = new FornecedorUseCaseImpl(new SupplierTestSupport.FornecedorRepositoryAdapter(store));
    }

    @Test
    void deveSalvarEListarFornecedor() {
        Fornecedor fornecedor = fornecedorBasico();

        Fornecedor salvo = fornecedorUseCase.salvar(fornecedor);

        assertEquals(1L, salvo.getId());
        assertEquals(1, fornecedorUseCase.buscarTodos().size());
    }

    @Test
    void deveImpedirFornecedorDuplicadoPorCodigo() {
        fornecedorUseCase.salvar(fornecedorBasico());

        Fornecedor duplicado = fornecedorBasico();
        duplicado.setDocumento("98.765.432/0001-10");

        assertThrows(IllegalArgumentException.class, () -> fornecedorUseCase.salvar(duplicado));
    }

    private Fornecedor fornecedorBasico() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Distribuidora Alfa");
        fornecedor.setCodigo("FORN-001");
        fornecedor.setDocumento("12.345.678/0001-90");
        fornecedor.setLeadTimeDias(5);
        return fornecedor;
    }
}