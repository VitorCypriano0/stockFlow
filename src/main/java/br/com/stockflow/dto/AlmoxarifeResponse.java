package br.com.stockflow.dto;

public record AlmoxarifeResponse(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        boolean ativo
) {
}
