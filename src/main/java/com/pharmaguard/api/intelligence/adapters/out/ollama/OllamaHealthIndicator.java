package com.pharmaguard.api.intelligence.adapters.out.ollama;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("ollama")
public class OllamaHealthIndicator implements HealthIndicator {

    private final boolean enabled;
    private final String baseUrl;
    private final String model;
    private final RestClient restClient;

    public OllamaHealthIndicator(
            @Value("${intelligence.enabled:true}") boolean enabled,
            @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${ollama.model:gemma3:4b}") String model) {
        this.enabled = enabled;
        this.baseUrl = baseUrl;
        this.model = model;
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Health health() {
        if (!enabled) {
            return Health.up()
                    .withDetail("enabled", false)
                    .withDetail("provider", "Motor IA / Ollama")
                    .withDetail("mode", "fallback-estatistico")
                    .build();
        }

        try {
            String response = restClient.get()
                    .uri("/")
                    .retrieve()
                    .body(String.class);

            return Health.up()
                    .withDetail("enabled", true)
                    .withDetail("provider", "Motor IA / Ollama")
                    .withDetail("baseUrl", baseUrl)
                    .withDetail("model", model)
                    .withDetail("status", response != null ? response.trim() : "Ollama is running")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("enabled", true)
                    .withDetail("provider", "Motor IA / Ollama")
                    .withDetail("baseUrl", baseUrl)
                    .withDetail("model", model)
                    .withDetail("error", e.getMessage() != null ? e.getMessage() : "Ollama indisponivel")
                    .build();
        }
    }
}
