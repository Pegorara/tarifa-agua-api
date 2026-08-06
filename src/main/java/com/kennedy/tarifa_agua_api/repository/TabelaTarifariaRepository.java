package com.kennedy.tarifa_agua_api.repository;

import com.kennedy.tarifa_agua_api.entity.TabelaTarifaria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TabelaTarifariaRepository extends JpaRepository<TabelaTarifaria, Long> {
}
