package com.pharmaguard.api.inventory.domain;

import java.util.Arrays;

public enum CategoriaMedicamento {
    ANTIBIOTICO("Antibiótico", "Medicamentos para tratamento de infecções"),
    ANALGESICO("Analgésico", "Medicamentos para controle de dor"),
    ANTIINFLAMATORIO("Anti-inflamatório", "Medicamentos para controle de inflamações"),
    ANTITERMICO("Antitérmico", "Medicamentos para controle de febre"),
    CONTROLADO("Controlado", "Medicamentos sujeitos a controle especial"),
    INSUMO("Insumo", "Materiais e insumos de apoio assistencial"),
    OUTROS("Outros", "Demais medicamentos e insumos");

    private final String nome;
    private final String descricao;

    CategoriaMedicamento(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CategoriaMedicamento fromNome(String nome) {
        return Arrays.stream(values())
                .filter(categoria -> categoria.nome.equalsIgnoreCase(nome))
                .findFirst()
                .orElse(OUTROS);
    }
}