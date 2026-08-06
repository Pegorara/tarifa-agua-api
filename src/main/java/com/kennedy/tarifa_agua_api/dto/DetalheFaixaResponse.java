package com.kennedy.tarifa_agua_api.dto;

import java.math.BigDecimal;

public record DetalheFaixaResponse(
        FaixaResponse faixa,
        Integer m3Cobrados,
        BigDecimal valorUnitario,
        BigDecimal subTotal
) {
}
