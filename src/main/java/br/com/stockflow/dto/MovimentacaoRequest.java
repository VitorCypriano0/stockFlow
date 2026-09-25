package br.com.stockflow.dto;

import br.com.stockflow.entities.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimentacaoRequest(
        @NotNull Long itemId,
        @NotNull Long almoxarifeId,
        @NotNull TipoMovimentacao tipo,
        @NotNull @Positive Integer quantidade,
        String observacao
) {
}
