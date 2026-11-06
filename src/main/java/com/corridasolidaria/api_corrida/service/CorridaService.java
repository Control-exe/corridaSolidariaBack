package com.corridasolidaria.api_corrida.service;

import com.corridasolidaria.api_corrida.data.Evento;
import com.corridasolidaria.api_corrida.model.CorridaEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CorridaService {

    private final List<CorridaEntity> inscricoes = new ArrayList<>();

    public List<CorridaEntity> listarTodos() {
        return new ArrayList<>(inscricoes);
    }

    public CorridaEntity consultarCorredor(String email) {
        return inscricoes.get(buscarPosicao(email));
    }

    public Map<String, Long> contagemKm() {
        Map<String, Long> resumo = new LinkedHashMap<>();
        resumo.put("total", (long) inscricoes.size());
        resumo.put("km5", inscricoes.stream().filter(i -> i.getPercurso().equals("KM_5")).count());
        resumo.put("km10", inscricoes.stream().filter(i -> i.getPercurso().equals("KM_10")).count());
        return resumo;
    }

    public CorridaEntity inscreverCorredor(CorridaEntity inscricao) {
        validarInscricao(inscricao, inscricao.getEmail());
        if (inscricoes.stream().anyMatch(i -> i.getEmail().equals(inscricao.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma inscrição ativa com este e-mail.");
        }
        inscricoes.add(inscricao);
        return inscricao;
    }

    public CorridaEntity editarCorredor(String email, CorridaEntity inscricao) {
        int posicao = buscarPosicao(email);
        validarInscricao(inscricao, inscricoes.get(posicao).getEmail());
        inscricoes.set(posicao, inscricao);
        return inscricao;
    }

    public void deletarCorredor(String email) {
        inscricoes.remove(buscarPosicao(email));
    }

    // Mesma validação no cadastro e na edição.
    private void validarInscricao(CorridaEntity inscricao, String email) {
        inscricao.setNome(validarNome(inscricao.getNome()));
        inscricao.setEmail(validarEmail(email));
        LocalDate nascimento = validarDataNascimento(inscricao.getDataNascimento());
        int idade = calcularIdade(nascimento);
        if (idade < 14) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A idade mínima para participar é 14 anos.");
        }
        String percurso = validarPercurso(inscricao.getPercurso());
        if (percurso.equals("KM_10") && idade < 18) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O percurso de 10 km é permitido somente a partir de 18 anos.");
        }
        inscricao.setDataNascimento(nascimento.toString());
        inscricao.setPercurso(percurso);
        inscricao.setIdadeNaProva(idade);
        inscricao.setCategoria(calcularCategoria(idade));
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório.");
        }

        // NFC junta acentos digitados separados; (?U)\s também cobre o espaço não separável.
        String nomeNormalizado = Normalizer.normalize(nome, Normalizer.Form.NFC)
                .replaceAll("(?U)\\s+", " ").strip();

        if (nomeNormalizado.length() < 3 || nomeNormalizado.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome deve ter entre 3 e 100 caracteres.");
        }
        // Palavras de letras, separadas por espaço, apóstrofo ou hífen; aceita abreviação com ponto (Jr.).
        if (!nomeNormalizado.matches("^\\p{L}+\\.?(?:(?: ?['’-] ?| )\\p{L}+\\.?)*$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome deve conter apenas letras, espaços, apóstrofos, hífens e pontos de abreviação.");
        }
        return nomeNormalizado;
    }

    private String validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail é obrigatório.");
        }

        String emailNormalizado = email.strip().toLowerCase(Locale.ROOT);

        // Antes do @: só a-z, 0-9 e ponto (sem ponto no início, no fim ou repetido), até 64 caracteres.
        // Domínio: partes sem hífen no início ou no fim. Total de até 254 caracteres.
        String formato = "^(?=.{1,254}$)(?=.{1,64}@)[a-z0-9]+(\\.[a-z0-9]+)*"
                + "@([a-z0-9]([a-z0-9-]*[a-z0-9])?\\.)+[a-z]{2,}$";
        if (!emailNormalizado.matches(formato)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "E-mail inválido. Antes do @ use apenas letras (a-z), números (0-9) e ponto.");
        }
        return emailNormalizado;
    }

    private LocalDate validarDataNascimento(String data) {
        if (data == null || data.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data de nascimento é obrigatória.");
        }

        String mensagem = "Data de nascimento inválida, use o formato aaaa-mm-dd.";
        if (!data.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }

        LocalDate nascimento;
        try {
            // O parse é estrito: recusa datas inexistentes como 2023-02-30.
            nascimento = LocalDate.parse(data);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }

        if (!nascimento.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Data de nascimento deve ser anterior a hoje.");
        }
        return nascimento;
    }

    private String validarPercurso(String percurso) {
        if (percurso == null || percurso.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Percurso é obrigatório.");
        }
        if (!percurso.equals("KM_5") && !percurso.equals("KM_10")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Percurso inválido, use KM_5 ou KM_10.");
        }
        return percurso;
    }

    // Idade completa na data da prova; aniversário no próprio dia já conta.
    int calcularIdade(LocalDate nascimento) {
        return Period.between(nascimento, Evento.DATA_PROVA).getYears();
    }

    String calcularCategoria(int idade) {
        if (idade < 18) return "JUVENIL";
        if (idade < 40) return "ADULTO";
        if (idade < 60) return "MASTER";
        return "SENIOR";
    }

    private int buscarPosicao(String email) {
        String emailNormalizado = email.strip().toLowerCase(Locale.ROOT);
        for (int i = 0; i < inscricoes.size(); i++) {
            if (inscricoes.get(i).getEmail().equals(emailNormalizado)) return i;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não existe inscrição ativa para o e-mail informado.");
    }
}
