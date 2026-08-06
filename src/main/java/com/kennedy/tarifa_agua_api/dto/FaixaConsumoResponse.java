package com.kennedy.tarifa_agua_api.dto;

import java.math.BigDecimal;

public record FaixaConsumoResponse(Integer inicio, Integer fim, BigDecimal valorUnitario) {
}
