package com.kennedy.tarifa_agua_api.dto;

import java.time.LocalDate;
import java.util.List;

public record TabelaTarifariaResponse(
        Long id,
        String nome,
        LocalDate dataVigencia,
        List<CategoriaFaixasResponse> categorias
) {
}
