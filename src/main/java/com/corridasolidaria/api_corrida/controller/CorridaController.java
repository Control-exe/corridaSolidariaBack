package com.corridasolidaria.api_corrida.controller;

import com.corridasolidaria.api_corrida.model.CorridaEntity;
import com.corridasolidaria.api_corrida.service.CorridaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CorridaController {

    @Autowired
    private CorridaService corridaService;

    @GetMapping("/inscricoes")
    public List<CorridaEntity> listarTodos() {
        return corridaService.listarTodos();
    }

    @GetMapping("/inscricoes/{email}")
    public CorridaEntity consultarCorredor(@PathVariable String email) {
        return corridaService.consultarCorredor(email);
    }

    @GetMapping("/resumo")
    public CorridaEntity contagemKm() {
        return corridaService.contagemKm();
    }

    @PostMapping("/inscricoes")
    public CorridaEntity inscreverCorredor(@RequestBody CorridaEntity corrida) {
        return corridaService.inscreverCorredor(corrida);
    }

    @PutMapping("/inscricoes/{email}")
    public CorridaEntity editarCorredor(@PathVariable String email, @RequestBody CorridaEntity corrida) {
        return corridaService.editarCorredor(email, corrida);
    }

    @DeleteMapping("/inscricoes/{email}")
    public void deletarCorredor(@PathVariable String email) {
        corridaService.deletarCorredor(email);
    }
}
