package br.com.stockflow.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BootstrapRequest(
        @NotBlank @Size(max = 180) String empresaNome,
        @NotBlank String nome,
        @NotBlank String cpf,
        @NotBlank @Email String email,
        String telefone,
        @NotBlank @Size(min = 8) String senha
) {
}
