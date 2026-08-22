package com.pharmaguard.api.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class EstoqueDomainTest {

    private final RegraFefo regraFefo = new RegraFefo();

    @Test
    void devePlanejarBaixaPorFefoEUsarMaisDeUmLoteQuandoNecessario() {
        SaldoLoteEstoque loteMaisAntigo = saldo(1L, "LOT-001", 10, 10);
        SaldoLoteEstoque loteMaisRecente = saldo(2L, "LOT-002", 20, 20);

        List<RegraFefo.ItemBaixaFefo> plano = regraFefo.planejarBaixa(
                25, List.of(loteMaisRecente, loteMaisAntigo));

        assertThat(plano).extracting(RegraFefo.ItemBaixaFefo::getNumeroLote)
                .containsExactly("LOT-001", "LOT-002");
        assertThat(plano).extracting(RegraFefo.ItemBaixaFefo::getQuantidadeConsumida)
                .containsExactly(10, 15);
    }

    @Test
    void naoDeveUsarLoteVencidoNaBaixaFefo() {
        SaldoLoteEstoque loteVencido = saldo(1L, "LOT-001", -1, 10);
        SaldoLoteEstoque loteValido = saldo(2L, "LOT-002", 30, 10);

        List<SaldoLoteEstoque> ordenados = regraFefo.ordenarLotesValidosParaSaida(
                List.of(loteVencido, loteValido));

        assertThat(ordenados).extracting(SaldoLoteEstoque::getNumeroLote)
                .containsExactly("LOT-002");
    }

    @Test
    void deveRejeitarQuantidadeInvalidaESaldoInsuficiente() {
        SaldoLoteEstoque lote = saldo(1L, "LOT-001", 10, 5);

        assertThatThrownBy(() -> regraFefo.planejarBaixa(0, List.of(lote)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> regraFefo.planejarBaixa(6, List.of(lote)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("saldo insuficiente");
    }

    private SaldoLoteEstoque saldo(Long id, String numero, long diasParaVencer, int quantidade) {
        return new SaldoLoteEstoque(id, numero, LocalDate.now().plusDays(diasParaVencer), quantidade);
    }
}
