package com.corridasolidaria.api_corrida.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CorridaEntity {
    private String nome;
    private String email;
    private String dataNascimento; // aaaa-mm-dd
    private String percurso;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idadeNaProva;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String categoria;
}
