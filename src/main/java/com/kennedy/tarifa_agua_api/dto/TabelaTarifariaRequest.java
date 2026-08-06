package com.kennedy.tarifa_agua_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record TabelaTarifariaRequest(
        @NotBlank(message = "Nome da tabela tarifária é obrigatório")
        String nome,

        @NotNull(message = "Data de vigência é obrigatória")
        LocalDate dataVigencia,

        @NotEmpty(message = "É necessário informar ao menos uma categoria com suas faixas")
        @Valid
        List<CategoriaFaixasRequest> categorias
) {
}
