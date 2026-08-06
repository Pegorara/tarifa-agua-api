package com.kennedy.tarifa_agua_api.service;

import com.kennedy.tarifa_agua_api.dto.*;
import com.kennedy.tarifa_agua_api.entity.CategoriaConsumidor;
import com.kennedy.tarifa_agua_api.entity.FaixaConsumo;
import com.kennedy.tarifa_agua_api.entity.TabelaTarifaria;
import com.kennedy.tarifa_agua_api.exception.RecursoNaoEncontradoException;
import com.kennedy.tarifa_agua_api.repository.CategoriaConsumidorRepository;
import com.kennedy.tarifa_agua_api.repository.FaixaConsumoRepository;
import com.kennedy.tarifa_agua_api.repository.TabelaTarifariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TabelaTarifariaService {

    private final TabelaTarifariaRepository tabelaTarifariaRepository;
    private final CategoriaConsumidorRepository categoriaConsumidorRepository;
    private final FaixaConsumoRepository faixaConsumoRepository;
    private final FaixaConsumoValidator faixaConsumoValidator;

    public TabelaTarifariaService(TabelaTarifariaRepository tabelaTarifariaRepository,
                                  CategoriaConsumidorRepository categoriaConsumidorRepository,
                                  FaixaConsumoRepository faixaConsumoRepository,
                                  FaixaConsumoValidator faixaConsumoValidator) {
        this.tabelaTarifariaRepository = tabelaTarifariaRepository;
        this.categoriaConsumidorRepository = categoriaConsumidorRepository;
        this.faixaConsumoRepository = faixaConsumoRepository;
        this.faixaConsumoValidator = faixaConsumoValidator;
    }

    @Transactional
    public TabelaTarifariaResponse criar(TabelaTarifariaRequest request) {
        TabelaTarifaria tabela = new TabelaTarifaria();
        tabela.setNome(request.nome());
        tabela.setDataVigencia(request.dataVigencia());
        tabela.setAtivo(true);
        tabela = tabelaTarifariaRepository.save(tabela);

        for (CategoriaFaixasRequest categoriaRequest : request.categorias()) {
            CategoriaConsumidor categoria = resolverCategoria(categoriaRequest.categoria());

            List<FaixaConsumo> faixas = new ArrayList<>();
            for (FaixaConsumoRequest faixaRequest : categoriaRequest.faixas()) {
                FaixaConsumo faixa = new FaixaConsumo();
                faixa.setTabelaTarifaria(tabela);
                faixa.setCategoriaConsumidor(categoria);
                faixa.setInicio(faixaRequest.inicio());
                faixa.setFim(faixaRequest.fim());
                faixa.setValorUnitario(faixaRequest.valorUnitario());
                faixas.add(faixa);
            }

            faixaConsumoValidator.validar(faixas);
            faixaConsumoRepository.saveAll(faixas);
        }

        return buscarPorId(tabela.getId());
    }

    @Transactional(readOnly = true)
    public List<TabelaTarifariaResponse> listar() {
        return tabelaTarifariaRepository.findByAtivoTrue().stream()
                .map(tabela -> montarResponse(tabela, faixaConsumoRepository.findByTabelaTarifariaId(tabela.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public TabelaTarifariaResponse buscarPorId(Long id) {
        TabelaTarifaria tabela = tabelaTarifariaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tabela tarifária não encontrada: " + id));
        return montarResponse(tabela, faixaConsumoRepository.findByTabelaTarifariaId(id));
    }

    @Transactional
    public void excluir(Long id) {
        TabelaTarifaria tabela = tabelaTarifariaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tabela tarifária não encontrada: " + id));
        tabela.setAtivo(false);
        tabelaTarifariaRepository.save(tabela);
    }

    private CategoriaConsumidor resolverCategoria(String nome) {
        return categoriaConsumidorRepository.findByNome(nome)
                .orElseGet(() -> {
                    CategoriaConsumidor nova = new CategoriaConsumidor();
                    nova.setNome(nome);
                    return categoriaConsumidorRepository.save(nova);
                });
    }

    private TabelaTarifariaResponse montarResponse(TabelaTarifaria tabela, List<FaixaConsumo> faixas) {
        List<CategoriaFaixasResponse> categorias = faixas.stream()
                .collect(Collectors.groupingBy(f -> f.getCategoriaConsumidor().getNome()))
                .entrySet().stream()
                .map(entry -> new CategoriaFaixasResponse(
                        entry.getKey(),
                        entry.getValue().stream()
                                .sorted((a, b) -> a.getInicio().compareTo(b.getInicio()))
                                .map(f -> new FaixaConsumoResponse(f.getInicio(), f.getFim(), f.getValorUnitario()))
                                .toList()
                ))
                .toList();

        return new TabelaTarifariaResponse(tabela.getId(), tabela.getNome(), tabela.getDataVigencia(), categorias);
    }
}
