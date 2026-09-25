package br.com.stockflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FornecedorRequest(
        @NotBlank String nome,
        String cnpj,
        String contato,
        String telefone,
        @Email String email
) {
}
