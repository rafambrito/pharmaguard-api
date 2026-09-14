package com.pharmaguard.api.intelligence.adapters.out.ollama;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

class OllamaHealthIndicatorTest {

    @Test
    void deveRetornarUpQuandoIntelligenceDesabilitada() {
        OllamaHealthIndicator indicator = new OllamaHealthIndicator(false, "http://localhost:11434", "gemma3:4b");

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("enabled", false);
        assertThat(health.getDetails()).containsEntry("mode", "fallback-estatistico");
    }

    @Test
    void deveRetornarDownQuandoOllamaInacessivel() {
        OllamaHealthIndicator indicator = new OllamaHealthIndicator(true, "http://localhost:99999", "gemma3:4b");

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("enabled", true);
        assertThat(health.getDetails()).containsEntry("provider", "Motor IA / Ollama");
        assertThat(health.getDetails()).containsKey("error");
    }
}
