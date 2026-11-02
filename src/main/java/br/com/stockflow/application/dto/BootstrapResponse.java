package br.com.stockflow.application.dto;

public record BootstrapResponse(
        Long empresaId,
        String empresaNome,
        Long administradorId,
        String administradorNome,
        String administradorEmail
) {
}
