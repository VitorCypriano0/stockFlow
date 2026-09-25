package br.com.stockflow.dto;

import java.time.LocalDateTime;

public record ApiError(String mensagem, LocalDateTime dataHora) {
}
