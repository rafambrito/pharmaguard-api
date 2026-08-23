package com.pharmaguard.api.shared.infrastructure.resilience.bulkhead;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class BulkheadExecutorTest {

    @Test
    void deveRejeitarExecucaoQuandoCapacidadeEstiverOcupada() {
        BulkheadExecutor bulkhead = new BulkheadExecutor(new BulkheadPolicy(1));

        String resultado = bulkhead.execute(() -> {
            assertThatThrownBy(() -> bulkhead.execute(() -> "segunda execucao"))
                    .isInstanceOf(BulkheadFullException.class);
            return "primeira execucao";
        });

        assertThat(resultado).isEqualTo("primeira execucao");
    }

    @Test
    void deveLiberarCapacidadeAposFalha() {
        BulkheadExecutor bulkhead = new BulkheadExecutor(new BulkheadPolicy(1));

        assertThatThrownBy(() -> bulkhead.execute(() -> {
            throw new IllegalStateException("falha na execucao");
        })).isInstanceOf(IllegalStateException.class);

        assertThat(bulkhead.execute(() -> "disponivel novamente")).isEqualTo("disponivel novamente");
    }
}
