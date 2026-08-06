package com.kennedy.tarifa_agua_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CalculoRequest(
        @NotBlank(message = "Categoria é obrigatória")
        String categoria,

        @NotNull(message = "Consumo é obrigatório")
        @PositiveOrZero(message = "Consumo não pode ser negativo")
        Integer consumo
) {
}
