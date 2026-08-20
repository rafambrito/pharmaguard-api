package com.pharmaguard.api.inventory.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InventoryDomainTest {

    @Test
    void deveNormalizarCamposDeCategoriaEUnidadeDeMedida() {
        Categoria categoria = new Categoria();
        categoria.setNome("  Antibioticos  ");
        categoria.setDescricao("  Medicamentos para infeccoes  ");
        categoria.setStatus(Categoria.Status.ATIVA);

        UnidadeMedida unidadeMedida = new UnidadeMedida();
        unidadeMedida.setNome("  Miligrama  ");
        unidadeMedida.setSigla("  mg  ");
        unidadeMedida.setStatus(UnidadeMedida.Status.ATIVA);

        assertEquals("Antibioticos", categoria.getNome());
        assertEquals("Medicamentos para infeccoes", categoria.getDescricao());
        assertEquals("MG", unidadeMedida.getSigla());
    }

    @Test
    void deveValidarUnicidadeDeCategoriaUnidadeMedidaELote() {
        Categoria categoria = criarCategoria();
        UnidadeMedida unidadeMedida = criarUnidadeMedida();
        Medicamento medicamento = criarMedicamento(categoria, unidadeMedida);
        Lote lote = new Lote(1L, "LOT-001", LocalDate.now().plusDays(10), 10, medicamento);

        assertThrows(IllegalArgumentException.class,
                () -> categoria.validarIdentidadeUnica(nome -> true));
        assertThrows(IllegalArgumentException.class,
                () -> unidadeMedida.validarIdentidadeUnica(sigla -> true));
        assertThrows(IllegalArgumentException.class,
                () -> lote.validarIdentidadeUnica((numeroLote, medicamentoId) -> true));
    }

    @Test
    void deveRejeitarDataDeValidadeVencidaNoLote() {
        Medicamento medicamento = criarMedicamento(criarCategoria(), criarUnidadeMedida());

        assertThrows(IllegalArgumentException.class,
                () -> new Lote(1L, "LOT-001", LocalDate.now().minusDays(1), 10, medicamento));
    }

    private Categoria criarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNome("Antibioticos");
        categoria.setDescricao("Categoria");
        categoria.setStatus(Categoria.Status.ATIVA);
        return categoria;
    }

    private UnidadeMedida criarUnidadeMedida() {
        UnidadeMedida unidadeMedida = new UnidadeMedida();
        unidadeMedida.setNome("Miligrama");
        unidadeMedida.setSigla("mg");
        unidadeMedida.setStatus(UnidadeMedida.Status.ATIVA);
        return unidadeMedida;
    }

    private Medicamento criarMedicamento(Categoria categoria, UnidadeMedida unidadeMedida) {
        Medicamento medicamento = new Medicamento();
        medicamento.setNome("Amoxicilina");
        medicamento.setApresentacao("500mg capsula");
        medicamento.setDescricao("Antibiotico");
        medicamento.setCategoria(categoria);
        medicamento.setUnidadeMedida(unidadeMedida);
        medicamento.setCriticidade(Medicamento.Criticidade.MEDIA);
        medicamento.setStatus(Medicamento.Status.ATIVO);
        return medicamento;
    }
}
