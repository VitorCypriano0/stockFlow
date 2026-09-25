package br.com.stockflow.dto;

import java.time.LocalDateTime;

public record HistoricoResponse(
        Long id,
        Long movimentacaoId,
        String acao,
        String tipo,
        String itemNome,
        int quantidade,
        Long almoxarifeId,
        String almoxarifeNome,
        LocalDateTime dataHora,
        String observacao
) {
}
