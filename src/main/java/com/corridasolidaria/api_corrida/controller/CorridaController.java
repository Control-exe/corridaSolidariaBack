package com.corridasolidaria.api_corrida.controller;

import com.corridasolidaria.api_corrida.model.CorridaEntity;
import com.corridasolidaria.api_corrida.service.CorridaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/inscricoes/resumo")
    public Map<String, Long> contagemKm() {
        return corridaService.contagemKm();
    }

    @PostMapping("/inscricoes")
    @ResponseStatus(HttpStatus.CREATED)
    public CorridaEntity inscreverCorredor(@RequestBody CorridaEntity inscricao) {
        return corridaService.inscreverCorredor(inscricao);
    }

    @PutMapping("/inscricoes/{email}")
    public CorridaEntity editarCorredor(@PathVariable String email, @RequestBody CorridaEntity inscricao) {
        return corridaService.editarCorredor(email, inscricao);
    }

    @DeleteMapping("/inscricoes/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarCorredor(@PathVariable String email) {
        corridaService.deletarCorredor(email);
    }
}
