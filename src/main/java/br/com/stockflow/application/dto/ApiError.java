package br.com.stockflow.application.dto;

import java.time.LocalDateTime;

public record ApiError(String mensagem, LocalDateTime dataHora) {
}
