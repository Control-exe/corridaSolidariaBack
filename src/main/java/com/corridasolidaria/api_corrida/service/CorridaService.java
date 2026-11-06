package com.corridasolidaria.api_corrida.service;

import com.corridasolidaria.api_corrida.model.CorridaEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CorridaService {

    private final List<CorridaEntity> corrida = new ArrayList<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    public List<CorridaEntity> listarTodos() {
        return new ArrayList<>(corrida);
    }

    public CorridaEntity consultarCorredor(String email) {
        return null;
    }

    public CorridaEntity contagemKm() {
        return null;
    }

    public CorridaEntity inscreverCorredor(CorridaEntity corrida) {
        return null;
    }

    public CorridaEntity editarCorredor(String email, CorridaEntity corrida) {
        return null;
    }

    public void deletarCorredor(String email) {

    }


}
