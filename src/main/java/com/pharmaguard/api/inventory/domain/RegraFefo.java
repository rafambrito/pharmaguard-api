package com.pharmaguard.api.inventory.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RegraFefo {

    public List<SaldoLoteEstoque> ordenarLotesValidosParaSaida(List<SaldoLoteEstoque> lotes) {
        Objects.requireNonNull(lotes, "lotes e obrigatorio");
        return lotes.stream()
                .filter(Objects::nonNull)
                .filter(SaldoLoteEstoque::estaValidoParaSaida)
                .sorted(Comparator.comparing(SaldoLoteEstoque::getDataValidade, Comparator.nullsLast(LocalDate::compareTo)))
                .toList();
    }

    public List<ItemBaixaFefo> planejarBaixa(int quantidadeSolicitada, List<SaldoLoteEstoque> lotes) {
        new RegraQuantidadeEstoque().validarQuantidadePositiva(quantidadeSolicitada, "quantidadeSolicitada");
        List<SaldoLoteEstoque> lotesOrdenados = ordenarLotesValidosParaSaida(lotes);

        List<ItemBaixaFefo> plano = new ArrayList<>();
        int restante = quantidadeSolicitada;

        for (SaldoLoteEstoque lote : lotesOrdenados) {
            if (restante == 0) {
                break;
            }

            int quantidadeConsumida = Math.min(lote.getQuantidadeDisponivel(), restante);
            if (quantidadeConsumida > 0) {
                plano.add(new ItemBaixaFefo(lote.getLoteId(), lote.getNumeroLote(), quantidadeConsumida));
                restante -= quantidadeConsumida;
            }
        }

        if (restante > 0) {
            throw new IllegalArgumentException("saldo insuficiente para atender a saida solicitada");
        }

        return plano;
    }

    public static class ItemBaixaFefo {

        private final Long loteId;
        private final String numeroLote;
        private final int quantidadeConsumida;

        public ItemBaixaFefo(Long loteId, String numeroLote, int quantidadeConsumida) {
            this.loteId = loteId;
            this.numeroLote = numeroLote;
            this.quantidadeConsumida = quantidadeConsumida;
        }

        public Long getLoteId() {
            return loteId;
        }

        public String getNumeroLote() {
            return numeroLote;
        }

        public int getQuantidadeConsumida() {
            return quantidadeConsumida;
        }
    }
}
