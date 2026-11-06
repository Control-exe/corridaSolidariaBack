package com.corridasolidaria.api_corrida.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

// Recusas da service e erros que o Spring gera antes de chegar na service,
// todos devolvidos no formato { "mensagem": "..." }.
@RestControllerAdvice
public class ErrosHandler {

    // Recusas lançadas pela service (400, 404, 409).
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> recusa(ResponseStatusException ex) {
        return erro(ex.getStatusCode(), ex.getReason());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido() {
        return erro(HttpStatus.BAD_REQUEST, "JSON inválido. Envie nome, email, dataNascimento e percurso como texto.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> formatoNaoSuportado() {
        return erro(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Envie o corpo em JSON (Content-Type: application/json).");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> metodoNaoPermitido() {
        return erro(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido para esta rota.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> rotaNaoEncontrada() {
        return erro(HttpStatus.NOT_FOUND, "Rota não encontrada.");
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatusCode status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
    }
}
