package com.pharmaguard.api.supplier.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FornecedorTest {

    @Test
    void deveValidarIdentidadeUnicaPorCodigoOuDocumento() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Distribuidora Alfa");
        fornecedor.setCodigo("FORN-001");
        fornecedor.setDocumento("12.345.678/0001-90");
        fornecedor.setStatus(Fornecedor.Status.ATIVO);

        assertThrows(IllegalArgumentException.class, () -> new Fornecedor().setNome(null));
    }

    @Test
    void deveClassificarLeadTimeQuandoConfigurado() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Distribuidora Beta");
        fornecedor.setCodigo("FORN-002");
        fornecedor.setStatus(Fornecedor.Status.ATIVO);

        fornecedor.setLeadTimeDias(10);

        assertEquals(StatusLeadTime.USUAL, fornecedor.getStatusLeadTime());
        fornecedor.setLeadTimeDias(31);
        assertEquals(StatusLeadTime.ELEVADO, fornecedor.getStatusLeadTime());
    }

    @Test
    void deveAdicionarERemoverContato() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Distribuidora Gama");
        fornecedor.setCodigo("FORN-003");
        fornecedor.setStatus(Fornecedor.Status.ATIVO);

        ContatoFornecedor contato = new ContatoFornecedor();
        contato.setNome("Maria");
        contato.setCanalPrincipal(ContatoFornecedor.CanalPrincipal.EMAIL);
        fornecedor.adicionarContato(contato);

        assertEquals(1, fornecedor.getContatos().size());
        assertFalse(fornecedor.getContatos().isEmpty());

        fornecedor.removerContato(contato);
        assertEquals(0, fornecedor.getContatos().size());
    }
}