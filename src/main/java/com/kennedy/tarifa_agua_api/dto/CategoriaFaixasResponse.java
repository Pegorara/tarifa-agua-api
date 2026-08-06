package com.kennedy.tarifa_agua_api.dto;

import java.util.List;

public record CategoriaFaixasResponse(String categoria, List<FaixaConsumoResponse> faixas) {
}
