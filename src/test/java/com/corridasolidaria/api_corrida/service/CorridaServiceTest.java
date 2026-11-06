package com.corridasolidaria.api_corrida.service;

import com.corridasolidaria.api_corrida.model.CorridaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// Prova em 14/03/2027.
class CorridaServiceTest {

    private final CorridaService service = new CorridaService();

    // Executa a ação e devolve o status que a API responderia: o de sucesso ou o da recusa lançada.
    private int status(Runnable acao, int sucesso) {
        try {
            acao.run();
            return sucesso;
        } catch (ResponseStatusException ex) {
            return ex.getStatusCode().value();
        }
    }

    private int cadastrar(String email, String nascimento, String percurso) {
        CorridaEntity inscricao = new CorridaEntity("Ana Souza", email, nascimento, percurso, null, null);
        return status(() -> service.inscreverCorredor(inscricao), 201);
    }

    @ParameterizedTest
    @CsvSource({
            "2009-03-15, 17, JUVENIL", // aniversário depois da prova
            "2009-03-14, 18, ADULTO",  // aniversário no dia da prova já conta
            "1987-03-15, 39, ADULTO",
            "1987-03-14, 40, MASTER",
            "1967-03-15, 59, MASTER",
            "1967-03-14, 60, SENIOR",
            "2013-03-14, 14, JUVENIL",
    })
    void calculaIdadeECategoriaNasBordas(String nascimento, int idade, String categoria) {
        assertThat(service.calcularIdade(LocalDate.parse(nascimento))).isEqualTo(idade);
        assertThat(service.calcularCategoria(idade)).isEqualTo(categoria);
    }

    @Test
    void recusaMenorDe14MesmoNos10Km() {
        assertThat(cadastrar("ana@email.com", "2013-03-15", "KM_10")).isEqualTo(400);
        assertThat(cadastrar("ana@email.com", "2013-03-14", "KM_5")).isEqualTo(201);
    }

    @Test
    void permite10KmSomenteAPartirDe18Anos() {
        assertThat(cadastrar("ana@email.com", "2009-03-15", "KM_10")).isEqualTo(400);
        assertThat(cadastrar("ana@email.com", "2009-03-14", "KM_10")).isEqualTo(201);
    }

    @Test
    void recusaEmailRepetidoComCaixaEEspacosDiferentes() {
        assertThat(cadastrar("ana.souza@email.com", "1990-05-20", "KM_5")).isEqualTo(201);
        assertThat(cadastrar(" Ana.Souza@Email.COM ", "1990-05-20", "KM_5")).isEqualTo(409);
    }

    @ParameterizedTest
    @CsvSource({
            "ana..s@email.com, 400",
            ".ana@email.com, 400",
            "ana.@email.com, 400",
            "ana@-email.com, 400",
            "ana@email-.com, 400",
            "ana@email, 400",
            "ana.souza+corrida@email.com, 400",
            "ana_souza@email.com, 400",
            "ana-souza@email.com, 400",
            "ana%souza@email.com, 400",
            "Ana.Souza2@Email.COM.br, 201",
    })
    void validaFormatoDoEmail(String email, int status) {
        assertThat(cadastrar(email, "1990-05-20", "KM_5")).isEqualTo(status);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Ana Souza Jr.|201",
            "Ana - Souza|201",
            "Maria d'Ávila-Souza|201",
            "José Silva|201",    // acento decomposto (NFD)
            "Ana Souza|201",      // espaço não separável
            "Ana 2|400",
            "Ana..Souza|400",
            "<b>Ana</b>|400",
    })
    void validaNome(String nome, int status) {
        CorridaEntity inscricao = new CorridaEntity(nome, "nome@email.com", "1990-05-20", "KM_5", null, null);
        assertThat(status(() -> service.inscreverCorredor(inscricao), 201)).isEqualTo(status);
    }

    @Test
    void edicaoRecusadaPreservaDados() {
        cadastrar("ana@email.com", "1990-05-20", "KM_10");
        CorridaEntity invalida = new CorridaEntity("Ana Souza", null, "2012-01-01", "KM_10", null, null);

        assertThat(status(() -> service.editarCorredor("ana@email.com", invalida), 200)).isEqualTo(400);
        assertThat(service.listarTodos().getFirst().getDataNascimento()).isEqualTo("1990-05-20");
        assertThat(service.contagemKm()).containsEntry("km10", 1L);
    }

    @Test
    void cancelamentoLiberaRecadastro() {
        cadastrar("ana@email.com", "1990-05-20", "KM_5");
        assertThat(status(() -> service.deletarCorredor("ANA@email.com"), 204)).isEqualTo(204);
        assertThat(status(() -> service.deletarCorredor("ana@email.com"), 204)).isEqualTo(404);
        assertThat(cadastrar("ana@email.com", "1990-05-20", "KM_5")).isEqualTo(201);
    }
}
