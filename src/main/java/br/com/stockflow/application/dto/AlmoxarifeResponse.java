package br.com.stockflow.application.dto;

public record AlmoxarifeResponse(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        boolean ativo,
        boolean bloqueado,
        String role
) {
}
