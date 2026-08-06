package com.kennedy.tarifa_agua_api.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResultadoCalculoResponse(
        String categoria,
        Integer consumoTotal,
        BigDecimal valorTotal,
        List<DetalheFaixaResponse> detalhamento
) {
}
