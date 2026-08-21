package com.pharmaguard.api.inventory.adapters.in.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarMedicamentoRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_NOME_OBRIGATORIO)
        @Size(max = 200, message = MessageKeys.MSG_VALIDACAO_NOME_TAMANHO_MAXIMO)
        String nome,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_APRESENTACAO_OBRIGATORIA)
        @Size(max = 200, message = MessageKeys.MSG_VALIDACAO_APRESENTACAO_TAMANHO_MAXIMO)
        String apresentacao,
        @Size(max = 500, message = MessageKeys.MSG_VALIDACAO_DESCRICAO_TAMANHO_MAXIMO)
        String descricao,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_CATEGORIA_ID_OBRIGATORIO)
        Long categoriaId,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_UNIDADE_MEDIDA_ID_OBRIGATORIO)
        Long unidadeMedidaId,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_CRITICIDADE_OBRIGATORIA)
        @Pattern(regexp = "(?i)BAIXA|MEDIA|ALTA|CRITICA", message = MessageKeys.MSG_VALIDACAO_CRITICIDADE_INVALIDA)
        String criticidade,
        Boolean ativo) {
}
