package com.pharmaguard.api.intelligence.adapters.out.ollama;

import com.pharmaguard.api.intelligence.domain.GeradorInsightPort;
import com.pharmaguard.api.intelligence.domain.InsightPrompt;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class OllamaInsightAdapter implements GeradorInsightPort {

    private final RestClient restClient;
    private final String model;

    public OllamaInsightAdapter(RestClient restClient, String model) {
        this.restClient = restClient;
        this.model = model;
    }

    @Override
    public String gerar(InsightPrompt prompt) {
        OllamaChatResponse response = restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new OllamaChatRequest(model, false, List.of(
                        new OllamaMessage("system", prompt.sistema()),
                        new OllamaMessage("user", prompt.contexto()))))
                .retrieve()
                .body(OllamaChatResponse.class);
        if (response == null || response.message() == null) {
            throw new IllegalStateException("Resposta invalida do Ollama");
        }
        return response.message().content();
    }

    private record OllamaChatRequest(String model, boolean stream, List<OllamaMessage> messages) {
    }

    private record OllamaMessage(String role, String content) {
    }

    private record OllamaChatResponse(OllamaMessage message) {
    }
}