package com.kennedy.tarifa_agua_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CategoriaFaixasRequest(
        @NotBlank(message = "Nome da categoria é obrigatório")
        String categoria,

        @NotEmpty(message = "É necessário informar ao menos uma faixa de consumo")
        @Valid
        List<FaixaConsumoRequest> faixas
) {
}
