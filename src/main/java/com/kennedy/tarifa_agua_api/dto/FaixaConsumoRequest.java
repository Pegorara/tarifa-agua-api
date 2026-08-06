package com.kennedy.tarifa_agua_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record FaixaConsumoRequest(
        @NotNull(message = "Início da faixa é obrigatório")
        @PositiveOrZero(message = "Início da faixa não pode ser negativo")
        Integer inicio,

        @NotNull(message = "Fim da faixa é obrigatório")
        @Positive(message = "Fim da faixa deve ser maior que zero")
        Integer fim,

        @NotNull(message = "Valor unitário é obrigatório")
        @Positive(message = "Valor unitário deve ser maior que zero")
        BigDecimal valorUnitario
) {
}
