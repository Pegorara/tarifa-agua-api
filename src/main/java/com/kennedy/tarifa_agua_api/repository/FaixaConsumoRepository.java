package com.kennedy.tarifa_agua_api.repository;

import com.kennedy.tarifa_agua_api.entity.FaixaConsumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaixaConsumoRepository extends JpaRepository<FaixaConsumo, Long> {

    List<FaixaConsumo> findByTabelaTarifariaIdAndCategoriaConsumidorIdOrderByInicioAsc(
            Long tabelaId, Long categoriaId
    );

    List<FaixaConsumo> findByTabelaTarifariaId(Long tabelaId);
}
