package com.pharmaguard.api.inventory.application;

import com.pharmaguard.api.inventory.domain.Dispensacao;
import com.pharmaguard.api.inventory.domain.Paciente;
import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

public class DispensacaoUseCaseImpl implements DispensacaoUseCase {
    private final DispensacaoRepositoryPort repository;
    private final PacienteUseCase pacienteUseCase;
    private final SaidaEstoqueUseCase saidaEstoqueUseCase;

    public DispensacaoUseCaseImpl(DispensacaoRepositoryPort repository, PacienteUseCase pacienteUseCase,
            SaidaEstoqueUseCase saidaEstoqueUseCase) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
        this.pacienteUseCase = Objects.requireNonNull(pacienteUseCase, "pacienteUseCase e obrigatorio");
        this.saidaEstoqueUseCase = Objects.requireNonNull(saidaEstoqueUseCase, "saidaEstoqueUseCase e obrigatorio");
    }

    @Override
    @Transactional
    public Dispensacao registrar(Long unidadeId, Long pacienteId, Long medicamentoId, int quantidade,
            String numeroReceita, String crmPrescritor, String observacao) {
        Paciente paciente = pacienteUseCase.buscarPorId(pacienteId);
        if (!paciente.estaAtivo()) {
            throw new BusinessException("paciente inativo nao pode receber dispensacao");
        }

        SaidaEstoque saida = new SaidaEstoque();
        saida.setQuantidadeTotal(quantidade);
        saida.setMotivo(SaidaEstoque.Motivo.DISPENSACAO);
        saida.setObservacao(observacao);
        SaidaEstoque saidaRegistrada = saidaEstoqueUseCase.registrar(unidadeId, medicamentoId, saida);

        Dispensacao dispensacao = new Dispensacao();
        dispensacao.setPaciente(paciente);
        dispensacao.setSaidaEstoque(saidaRegistrada);
        dispensacao.setNumeroReceita(numeroReceita);
        dispensacao.setCrmPrescritor(crmPrescritor);
        return repository.salvar(dispensacao);
    }

    @Override
    public Dispensacao buscarPorId(Long id) {
        Objects.requireNonNull(id, "id da dispensacao e obrigatorio");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("dispensacao nao encontrada"));
    }

    @Override
    public List<Dispensacao> listar(Long unidadeId, Long pacienteId, Long medicamentoId,
            LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException("dataInicial deve ser anterior ou igual a dataFinal");
        }
        return repository.listar(unidadeId, pacienteId, medicamentoId, dataInicial, dataFinal);
    }
}