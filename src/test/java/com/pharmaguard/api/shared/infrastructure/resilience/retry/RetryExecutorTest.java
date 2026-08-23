package com.pharmaguard.api.shared.infrastructure.resilience.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class RetryExecutorTest {

    @Test
    void deveRepetirOperacaoAteSucesso() {
        AtomicInteger tentativas = new AtomicInteger();
        RetryExecutor executor = executorPara(RuntimeException.class, tentativas);

        String resultado = executor.execute(() -> {
            if (tentativas.incrementAndGet() < 3) {
                throw new RuntimeException("falha transitoria");
            }
            return "ok";
        });

        assertThat(resultado).isEqualTo("ok");
        assertThat(tentativas).hasValue(3);
    }

    @Test
    void naoDeveRepetirFalhaNaoElegivel() {
        AtomicInteger tentativas = new AtomicInteger();
        RetryExecutor executor = executorPara(falha -> false, tentativas);

        assertThatThrownBy(() -> executor.execute(() -> {
            tentativas.incrementAndGet();
            throw new IllegalArgumentException("falha de validacao");
        })).isInstanceOf(IllegalArgumentException.class);

        assertThat(tentativas).hasValue(1);
    }

    @Test
    void deveRespeitarNumeroMaximoDeTentativas() {
        AtomicInteger tentativas = new AtomicInteger();
        RetryExecutor executor = executorPara(RuntimeException.class, tentativas);

        assertThatThrownBy(() -> executor.execute(() -> {
            tentativas.incrementAndGet();
            throw new RuntimeException("dependencia indisponivel");
        })).isInstanceOf(RuntimeException.class);

        assertThat(tentativas).hasValue(3);
    }

    private RetryExecutor executorPara(Class<? extends RuntimeException> tipo, AtomicInteger tentativas) {
        return executorPara(falha -> tipo.isInstance(falha), tentativas);
    }

    private RetryExecutor executorPara(java.util.function.Predicate<RuntimeException> retryable,
            AtomicInteger tentativas) {
        return new RetryExecutor(
                new RetryPolicy(3, Duration.ofMillis(1)),
                retryable,
                duration -> tentativas.get());
    }
}
