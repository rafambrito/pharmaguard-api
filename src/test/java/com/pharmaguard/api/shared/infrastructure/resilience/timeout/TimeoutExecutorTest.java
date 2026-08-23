package com.pharmaguard.api.shared.infrastructure.resilience.timeout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;

class TimeoutExecutorTest {

    @Test
    void deveInterromperOperacaoQueExcedePrazo() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            TimeoutExecutor timeoutExecutor = new TimeoutExecutor(
                    new TimeoutPolicy(Duration.ofMillis(30)), executorService);

            assertThatThrownBy(() -> timeoutExecutor.execute(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                }
                return "resultado";
            })).isInstanceOf(OperationTimeoutException.class);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void devePreservarFalhaDaOperacao() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            TimeoutExecutor timeoutExecutor = new TimeoutExecutor(
                    new TimeoutPolicy(Duration.ofSeconds(1)), executorService);

            assertThatThrownBy(() -> timeoutExecutor.execute(() -> {
                throw new IllegalArgumentException("falha de validacao");
            })).isInstanceOf(IllegalArgumentException.class);
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    void deveRetornarResultadoAntesDoPrazo() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            TimeoutExecutor timeoutExecutor = new TimeoutExecutor(
                    new TimeoutPolicy(Duration.ofSeconds(1)), executorService);

            assertThat(timeoutExecutor.execute(() -> "ok")).isEqualTo("ok");
        } finally {
            executorService.shutdownNow();
        }
    }
}
