package com.pharmaguard.api.inventory.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import com.pharmaguard.api.inventory.support.InventoryTestSupport;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class InventoryUseCaseImplTest {

    @Test
    void deveCriarCategoriaEUniadeMedida() {
        var store = InventoryTestSupport.store();
        CategoriaUseCase categoriaUseCase = new CategoriaUseCaseImpl(new InventoryTestSupport.CategoriaRepositoryAdapter(store));
        UnidadeMedidaUseCase unidadeMedidaUseCase = new UnidadeMedidaUseCaseImpl(new InventoryTestSupport.UnidadeMedidaRepositoryAdapter(store));

        Categoria categoriaSalva = categoriaUseCase.criar(criarCategoria());
        UnidadeMedida unidadeSalva = unidadeMedidaUseCase.criar(criarUnidadeMedida());

        assertNotNull(categoriaSalva.getId());
        assertNotNull(unidadeSalva.getId());
        assertEquals(1, store.categorias.size());
        assertEquals(1, store.unidadesMedida.size());
    }

    @Test
    void deveBloquearCategoriaDuplicada() {
        var store = InventoryTestSupport.store();
        CategoriaUseCase categoriaUseCase = new CategoriaUseCaseImpl(new InventoryTestSupport.CategoriaRepositoryAdapter(store));

        categoriaUseCase.criar(criarCategoria());

        assertThrows(BusinessException.class, () -> categoriaUseCase.criar(criarCategoria()));
    }

    @Test
    void deveCriarMedicamentoEValidarDependencias() {
        var store = InventoryTestSupport.store();
        CategoriaUseCase categoriaUseCase = new CategoriaUseCaseImpl(new InventoryTestSupport.CategoriaRepositoryAdapter(store));
        UnidadeMedidaUseCase unidadeMedidaUseCase = new UnidadeMedidaUseCaseImpl(new InventoryTestSupport.UnidadeMedidaRepositoryAdapter(store));
        MedicamentoUseCase medicamentoUseCase = new MedicamentoUseCaseImpl(new InventoryTestSupport.MedicamentoRepositoryAdapter(store));

        Categoria categoria = categoriaUseCase.criar(criarCategoria());
        UnidadeMedida unidadeMedida = unidadeMedidaUseCase.criar(criarUnidadeMedida());

        Medicamento salvo = medicamentoUseCase.criar(criarMedicamento(), categoria.getId(), unidadeMedida.getId());

        assertNotNull(salvo.getId());
        assertEquals(categoria.getId(), salvo.getCategoria().getId());
        assertEquals(unidadeMedida.getId(), salvo.getUnidadeMedida().getId());
    }

    @Test
    void deveBloquearMedicamentoDuplicado() {
        var store = InventoryTestSupport.store();
        CategoriaUseCase categoriaUseCase = new CategoriaUseCaseImpl(new InventoryTestSupport.CategoriaRepositoryAdapter(store));
        UnidadeMedidaUseCase unidadeMedidaUseCase = new UnidadeMedidaUseCaseImpl(new InventoryTestSupport.UnidadeMedidaRepositoryAdapter(store));
        MedicamentoUseCase medicamentoUseCase = new MedicamentoUseCaseImpl(new InventoryTestSupport.MedicamentoRepositoryAdapter(store));

        Categoria categoria = categoriaUseCase.criar(criarCategoria());
        UnidadeMedida unidadeMedida = unidadeMedidaUseCase.criar(criarUnidadeMedida());
        medicamentoUseCase.criar(criarMedicamento(), categoria.getId(), unidadeMedida.getId());

        assertThrows(BusinessException.class,
                () -> medicamentoUseCase.criar(criarMedicamento(), categoria.getId(), unidadeMedida.getId()));
    }

    @Test
    void deveCriarListarERemoverLote() {
        var store = InventoryTestSupport.store();
        CategoriaUseCase categoriaUseCase = new CategoriaUseCaseImpl(new InventoryTestSupport.CategoriaRepositoryAdapter(store));
        UnidadeMedidaUseCase unidadeMedidaUseCase = new UnidadeMedidaUseCaseImpl(new InventoryTestSupport.UnidadeMedidaRepositoryAdapter(store));
        MedicamentoUseCase medicamentoUseCase = new MedicamentoUseCaseImpl(new InventoryTestSupport.MedicamentoRepositoryAdapter(store));
        LoteUseCase loteUseCase = new LoteUseCaseImpl(new InventoryTestSupport.LoteRepositoryAdapter(store));

        Categoria categoria = categoriaUseCase.criar(criarCategoria());
        UnidadeMedida unidadeMedida = unidadeMedidaUseCase.criar(criarUnidadeMedida());
        Medicamento medicamento = medicamentoUseCase.criar(criarMedicamento(), categoria.getId(), unidadeMedida.getId());

        Lote lote = new Lote();
        lote.setNumeroLote("LOT-001");
        lote.setDataValidade(LocalDate.now().plusDays(30));
        lote.setQuantidadeInicial(10);

        Lote salvo = loteUseCase.cadastrar(medicamento.getId(), lote);
        assertNotNull(salvo.getId());
        assertEquals(1, loteUseCase.listarPorMedicamento(medicamento.getId()).size());
        assertEquals(salvo.getId(), loteUseCase.buscarPorId(medicamento.getId(), salvo.getId()).getId());

        loteUseCase.remover(medicamento.getId(), salvo.getId());
        assertTrue(loteUseCase.listarPorMedicamento(medicamento.getId()).isEmpty());
    }

    @Test
    void deveLancarErroQuandoRecursoNaoExistir() {
        var store = InventoryTestSupport.store();
        MedicamentoUseCase medicamentoUseCase = new MedicamentoUseCaseImpl(new InventoryTestSupport.MedicamentoRepositoryAdapter(store));
        LoteUseCase loteUseCase = new LoteUseCaseImpl(new InventoryTestSupport.LoteRepositoryAdapter(store));

        assertThrows(ResourceNotFoundException.class, () -> medicamentoUseCase.buscarPorId(99L));
        assertThrows(ResourceNotFoundException.class, () -> loteUseCase.listarPorMedicamento(99L));
    }

    private Categoria criarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNome("Antibioticos");
        categoria.setDescricao("Categoria");
        return categoria;
    }

    private UnidadeMedida criarUnidadeMedida() {
        UnidadeMedida unidadeMedida = new UnidadeMedida();
        unidadeMedida.setNome("Miligrama");
        unidadeMedida.setSigla("mg");
        return unidadeMedida;
    }

    private Medicamento criarMedicamento() {
        Medicamento medicamento = new Medicamento();
        medicamento.setNome("Amoxicilina");
        medicamento.setApresentacao("500mg capsula");
        medicamento.setDescricao("Antibiotico");
        medicamento.setCriticidade(Medicamento.Criticidade.MEDIA);
        return medicamento;
    }
}
