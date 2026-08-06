package com.kennedy.tarifa_agua_api.repository;

import com.kennedy.tarifa_agua_api.entity.CategoriaConsumidor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaConsumidorRepository extends JpaRepository<CategoriaConsumidor, Long> {

    Optional<CategoriaConsumidor> findByNome(String nome);
}
