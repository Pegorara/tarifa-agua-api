package com.kennedy.tarifa_agua_api.service;

import com.kennedy.tarifa_agua_api.entity.FaixaConsumo;
import com.kennedy.tarifa_agua_api.exception.RegraNegocioException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class FaixaConsumoValidator {

    public void validar(List<FaixaConsumo> faixas) {
        if (faixas == null || faixas.isEmpty()) {
            throw new RegraNegocioException("É necessário informar ao menos uma faixa de consumo.");
        }

        List<FaixaConsumo> ordenadas = faixas.stream()
                .sorted(Comparator.comparingInt(FaixaConsumo::getInicio))
                .toList();

        if (ordenadas.get(0).getInicio() != 0) {
            throw new RegraNegocioException("A primeira faixa deve iniciar em 0 (zero) m³.");
        }

        int limiteAnterior = -1;
        for (FaixaConsumo faixa : ordenadas) {
            if (faixa.getInicio() >= faixa.getFim()) {
                throw new RegraNegocioException(
                        "Faixa inválida: início (%d) deve ser menor que fim (%d)."
                                .formatted(faixa.getInicio(), faixa.getFim()));
            }
            if (limiteAnterior != -1 && faixa.getInicio() != limiteAnterior + 1) {
                throw new RegraNegocioException(
                        "Faixas devem ser contínuas, sem sobreposição ou lacunas: esperado início %d, encontrado %d."
                                .formatted(limiteAnterior + 1, faixa.getInicio()));
            }
            limiteAnterior = faixa.getFim();
        }
    }
}
