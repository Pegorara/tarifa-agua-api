package com.kennedy.tarifa_agua_api.service;

import com.kennedy.tarifa_agua_api.dto.DetalheFaixaResponse;
import com.kennedy.tarifa_agua_api.dto.FaixaResponse;
import com.kennedy.tarifa_agua_api.dto.ResultadoCalculoResponse;
import com.kennedy.tarifa_agua_api.entity.CategoriaConsumidor;
import com.kennedy.tarifa_agua_api.entity.FaixaConsumo;
import com.kennedy.tarifa_agua_api.entity.TabelaTarifaria;
import com.kennedy.tarifa_agua_api.exception.RecursoNaoEncontradoException;
import com.kennedy.tarifa_agua_api.exception.RegraNegocioException;
import com.kennedy.tarifa_agua_api.repository.CategoriaConsumidorRepository;
import com.kennedy.tarifa_agua_api.repository.FaixaConsumoRepository;
import com.kennedy.tarifa_agua_api.repository.TabelaTarifariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoTarifaService {

    private final TabelaTarifariaRepository tabelaTarifariaRepository;
    private final CategoriaConsumidorRepository categoriaConsumidorRepository;
    private final FaixaConsumoRepository faixaConsumoRepository;

    public CalculoTarifaService(TabelaTarifariaRepository tabelaTarifariaRepository,
                                CategoriaConsumidorRepository categoriaConsumidorRepository,
                                FaixaConsumoRepository faixaConsumoRepository) {
        this.tabelaTarifariaRepository = tabelaTarifariaRepository;
        this.categoriaConsumidorRepository = categoriaConsumidorRepository;
        this.faixaConsumoRepository = faixaConsumoRepository;
    }

    @Transactional(readOnly = true)
    public ResultadoCalculoResponse calcular(String nomeCategoria, Integer consumo) {
        if (consumo == null || consumo < 0) {
            throw new RegraNegocioException("Consumo deve ser um valor maior ou igual a zero.");
        }

        TabelaTarifaria tabelaVigente = tabelaTarifariaRepository
                .findFirstByAtivoTrueAndDataVigenciaLessThanEqualOrderByDataVigenciaDesc(LocalDate.now())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma tabela tarifária vigente encontrada para a data atual."));

        CategoriaConsumidor categoria = categoriaConsumidorRepository.findByNome(nomeCategoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de consumidor não encontrada: " + nomeCategoria));

        List<FaixaConsumo> faixas = faixaConsumoRepository
                .findByTabelaTarifariaIdAndCategoriaConsumidorIdOrderByInicioAsc(
                        tabelaVigente.getId(), categoria.getId());

        if (faixas.isEmpty()) {
            throw new RegraNegocioException(
                    "Não há faixas de consumo cadastradas para a categoria '" + nomeCategoria
                            + "' na tabela tarifária vigente.");
        }

        return calcularProgressivo(nomeCategoria, consumo, faixas);
    }

    private ResultadoCalculoResponse calcularProgressivo(String categoria, Integer consumo,
                                                         List<FaixaConsumo> faixas) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        List<DetalheFaixaResponse> detalhamento = new ArrayList<>();

        int restante = consumo;
        int limiteAnterior = 0;

        for (int i = 0; i < faixas.size() && restante > 0; i++) {
            FaixaConsumo faixa = faixas.get(i);
            boolean ultimaFaixa = (i == faixas.size() - 1);

            int capacidadeFaixa = faixa.getFim() - limiteAnterior;
            int m3Cobrados = ultimaFaixa ? restante : Math.min(restante, capacidadeFaixa);

            BigDecimal subtotal = faixa.getValorUnitario().multiply(BigDecimal.valueOf(m3Cobrados));
            valorTotal = valorTotal.add(subtotal);

            detalhamento.add(new DetalheFaixaResponse(
                    new FaixaResponse(faixa.getInicio(), faixa.getFim()),
                    m3Cobrados,
                    faixa.getValorUnitario(),
                    subtotal
            ));

            restante -= m3Cobrados;
            limiteAnterior = faixa.getFim();
        }

        return new ResultadoCalculoResponse(categoria, consumo, valorTotal, detalhamento);
    }
}
