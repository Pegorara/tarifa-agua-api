package com.kennedy.tarifa_agua_api.controller;

import com.kennedy.tarifa_agua_api.dto.TabelaTarifariaRequest;
import com.kennedy.tarifa_agua_api.dto.TabelaTarifariaResponse;
import com.kennedy.tarifa_agua_api.service.TabelaTarifariaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tabelas-tarifarias")
public class TabelaTarifariaController {

    private final TabelaTarifariaService tabelaTarifariaService;

    public TabelaTarifariaController(TabelaTarifariaService tabelaTarifariaService) {
        this.tabelaTarifariaService = tabelaTarifariaService;
    }

    @PostMapping
    public ResponseEntity<TabelaTarifariaResponse> criar(@Valid @RequestBody TabelaTarifariaRequest request) {
        TabelaTarifariaResponse response = tabelaTarifariaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TabelaTarifariaResponse>> listar() {
        return ResponseEntity.ok(tabelaTarifariaService.listar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        tabelaTarifariaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
