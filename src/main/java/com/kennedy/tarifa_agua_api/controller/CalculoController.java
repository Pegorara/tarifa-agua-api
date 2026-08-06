package com.kennedy.tarifa_agua_api.controller;


import com.kennedy.tarifa_agua_api.dto.CalculoRequest;
import com.kennedy.tarifa_agua_api.dto.ResultadoCalculoResponse;
import com.kennedy.tarifa_agua_api.service.CalculoTarifaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calculos")
public class CalculoController {

    private final CalculoTarifaService calculoTarifaService;

    public CalculoController(CalculoTarifaService calculoTarifaService) {
        this.calculoTarifaService = calculoTarifaService;
    }

    @PostMapping
    public ResponseEntity<ResultadoCalculoResponse> calcular(@Valid @RequestBody CalculoRequest request) {
        ResultadoCalculoResponse resultado = calculoTarifaService.calcular(request.categoria(), request.consumo());
        return ResponseEntity.ok(resultado);
    }
}
