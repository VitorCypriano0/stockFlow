package br.com.stockflow.application.dto;

public record FornecedorResponse(
        Long id,
        String nome,
        String cnpj,
        String contato,
        String telefone,
        String email,
        boolean ativo
) {
}
