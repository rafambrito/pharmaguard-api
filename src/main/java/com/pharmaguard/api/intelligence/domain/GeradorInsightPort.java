package com.pharmaguard.api.intelligence.domain;

public interface GeradorInsightPort {

    String gerar(InsightPrompt prompt);
}