package br.com.stockflow.application.dto;

import br.com.stockflow.domain.entities.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimentacaoRequest(
        @NotNull Long itemId,
        @NotNull TipoMovimentacao tipo,
        @NotNull @Positive Integer quantidade,
        String observacao
) {
}
