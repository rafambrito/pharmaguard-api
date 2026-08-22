package com.pharmaguard.api.inventory.adapters.out.repository;

import com.pharmaguard.api.inventory.domain.Categoria;
import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.inventory.domain.Lote;
import com.pharmaguard.api.inventory.domain.Medicamento;
import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.inventory.domain.UnidadeMedida;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryInventoryStore {

    private final AtomicLong sequence = new AtomicLong(1L);
    final Map<Long, Categoria> categorias = new ConcurrentHashMap<>();
    final Map<Long, UnidadeMedida> unidadesMedida = new ConcurrentHashMap<>();
    final Map<Long, Medicamento> medicamentos = new ConcurrentHashMap<>();
    final Map<Long, Lote> lotes = new ConcurrentHashMap<>();
    final Map<Long, EntradaEstoque> entradasEstoque = new ConcurrentHashMap<>();
    final Map<Long, SaidaEstoque> saidasEstoque = new ConcurrentHashMap<>();
    final Map<Long, MovimentacaoEstoque> movimentacoesEstoque = new ConcurrentHashMap<>();
    final Map<Long, Integer> saldosPorLote = new ConcurrentHashMap<>();

    Long nextId() {
        return sequence.getAndIncrement();
    }
}