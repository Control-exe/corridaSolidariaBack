package com.corridasolidaria.api_corrida.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CorridaEntity {

    private Long id;
    private String nome;
    private String email;
    private Date dataNascimento;
    private Integer percurso;
}
