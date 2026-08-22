package com.pharmaguard.api.inventory.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class EstoqueAtual {

    private Medicamento medicamento;
    private int quantidadeDisponivel;
    private int quantidadeReservada;
    private LocalDate validadeMaisProxima;
    private final List<SaldoLoteEstoque> lotesAtivos = new ArrayList<>();

    public EstoqueAtual() {
    }

    public EstoqueAtual(Medicamento medicamento, int quantidadeDisponivel, int quantidadeReservada,
            LocalDate validadeMaisProxima, List<SaldoLoteEstoque> lotesAtivos) {
        setMedicamento(medicamento);
        setQuantidadeDisponivel(quantidadeDisponivel);
        setQuantidadeReservada(quantidadeReservada);
        this.validadeMaisProxima = validadeMaisProxima;
        definirLotesAtivos(lotesAtivos);
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = Objects.requireNonNull(medicamento, "medicamento e obrigatorio");
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        new RegraQuantidadeEstoque().validarSaldoNaoNegativo(quantidadeDisponivel, "quantidadeDisponivel");
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public int getQuantidadeReservada() {
        return quantidadeReservada;
    }

    public void setQuantidadeReservada(int quantidadeReservada) {
        new RegraQuantidadeEstoque().validarSaldoNaoNegativo(quantidadeReservada, "quantidadeReservada");
        this.quantidadeReservada = quantidadeReservada;
    }

    public LocalDate getValidadeMaisProxima() {
        return validadeMaisProxima;
    }

    public void setValidadeMaisProxima(LocalDate validadeMaisProxima) {
        this.validadeMaisProxima = validadeMaisProxima;
    }

    public List<SaldoLoteEstoque> getLotesAtivos() {
        return List.copyOf(lotesAtivos);
    }

    public void definirLotesAtivos(List<SaldoLoteEstoque> lotesAtivos) {
        Objects.requireNonNull(lotesAtivos, "lotesAtivos e obrigatorio");
        this.lotesAtivos.clear();
        this.lotesAtivos.addAll(lotesAtivos.stream().filter(Objects::nonNull).toList());
    }

    public void recalcularComBaseNosLotes() {
        this.quantidadeDisponivel = new RegraSaldoEstoque().calcularQuantidadeDisponivel(lotesAtivos);
        this.validadeMaisProxima = lotesAtivos.stream()
                .filter(SaldoLoteEstoque::estaValidoParaSaida)
                .map(SaldoLoteEstoque::getDataValidade)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }
}
