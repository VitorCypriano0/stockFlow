package br.com.stockflow.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AlmoxarifeRequest(
        @NotBlank String nome,
        @NotBlank String cpf,
        @Email @NotBlank String email,
        String telefone,
        String senha,
        String empresaNome
) {
}
