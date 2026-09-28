package br.com.stockflow.dto;

import java.time.Instant;

public record AuthResponse(
        String token,
        String tipo,
        Instant expiraEm,
        AlmoxarifeResponse almoxarife
) {
}
