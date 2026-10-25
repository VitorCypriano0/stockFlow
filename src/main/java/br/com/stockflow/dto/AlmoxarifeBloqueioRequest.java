package br.com.stockflow.dto;

import jakarta.validation.constraints.NotNull;

public record AlmoxarifeBloqueioRequest(@NotNull Boolean bloqueado) {
}
