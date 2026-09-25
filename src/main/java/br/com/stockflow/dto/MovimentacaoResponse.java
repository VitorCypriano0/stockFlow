package br.com.stockflow.dto;

import br.com.stockflow.entities.TipoMovimentacao;
import java.time.LocalDateTime;

public record MovimentacaoResponse(
        Long id,
        Long itemId,
        String itemNome,
        Long almoxarifeId,
        String almoxarifeNome,
        TipoMovimentacao tipo,
        int quantidade,
        LocalDateTime dataHora,
        String observacao,
        boolean ativa
) {
}
