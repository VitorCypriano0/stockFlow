package br.com.stockflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemRequest(
        @NotBlank String codigo,
        @NotBlank String nome,
        String descricao,
        @NotBlank String unidade,
        @NotNull @Min(0) Integer estoqueMinimo,
        @NotNull Long fornecedorId
) {
}
