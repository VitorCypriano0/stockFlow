package br.com.stockflow.application.dto;

import jakarta.validation.constraints.NotNull;

public record AlmoxarifeBloqueioRequest(@NotNull Boolean bloqueado) {
}
