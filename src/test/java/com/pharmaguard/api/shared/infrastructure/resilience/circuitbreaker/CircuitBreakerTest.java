package com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class CircuitBreakerTest {

    @Test
    void deveAbrirAposLimiteDeFalhasERejeitarNovasExecucoes() {
        AtomicLong clock = new AtomicLong();
        CircuitBreaker circuitBreaker = circuitBreaker(clock, 2, Duration.ofSeconds(30));

        assertThatThrownBy(() -> circuitBreaker.execute(this::falhaTransitoria))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> circuitBreaker.execute(this::falhaTransitoria))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> circuitBreaker.execute(() -> "nao executado"))
                .isInstanceOf(CircuitBreakerOpenException.class);
    }

    @Test
    void devePermitirNovaExecucaoAposJanelaEFecharComSucesso() {
        AtomicLong clock = new AtomicLong();
        CircuitBreaker circuitBreaker = circuitBreaker(clock, 1, Duration.ofSeconds(30));

        assertThatThrownBy(() -> circuitBreaker.execute(this::falhaTransitoria))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> circuitBreaker.execute(() -> "bloqueado"))
                .isInstanceOf(CircuitBreakerOpenException.class);

        clock.set(Duration.ofSeconds(31).toNanos());
        circuitBreaker.execute(() -> "recuperado");
        circuitBreaker.execute(() -> "continua disponivel");
    }

    @Test
    void naoDeveContabilizarFalhaNaoElegivel() {
        AtomicLong clock = new AtomicLong();
        CircuitBreaker circuitBreaker = new CircuitBreaker(
                new CircuitBreakerPolicy(1, Duration.ofSeconds(30)),
                falha -> false,
                clock::get);

        assertThatThrownBy(() -> circuitBreaker.execute(() -> {
            throw new IllegalArgumentException("falha de validacao");
        })).isInstanceOf(IllegalArgumentException.class);
        circuitBreaker.execute(() -> "continua disponivel");
    }

    private CircuitBreaker circuitBreaker(AtomicLong clock, int threshold, Duration openDuration) {
        return new CircuitBreaker(
                new CircuitBreakerPolicy(threshold, openDuration),
                falha -> true,
                clock::get);
    }

    private String falhaTransitoria() {
        throw new RuntimeException("dependencia indisponivel");
    }
}
