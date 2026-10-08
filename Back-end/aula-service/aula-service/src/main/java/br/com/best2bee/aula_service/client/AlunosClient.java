package br.com.best2bee.aula_service.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.best2bee.aula_service.exception.RecursoNaoEncontradoException;
import br.com.best2bee.aula_service.exception.ServicoIndisponivelException;

@Component
public class AlunosClient {

    private final HttpClient httpClient;
    private final String baseUrl;

    public AlunosClient(
            @Value("${alunos-service.url}") String baseUrl) {

        this.baseUrl = baseUrl.replaceAll("/+$", "");

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public void verificarAluno(Integer idAluno) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/alunos/" + idAluno))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            int status = response.statusCode();

            if (status == 404) {
                throw new RecursoNaoEncontradoException(
                        "Aluno não encontrado com o id " + idAluno);
            }

            if (status != 200) {
                throw new ServicoIndisponivelException(
                        "Não foi possível validar o aluno. "
                                + "O serviço de alunos retornou HTTP " + status);
            }

        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();

            throw new ServicoIndisponivelException(
                    "A consulta ao serviço de alunos foi interrompida.");

        } catch (IOException ex) {
            throw new ServicoIndisponivelException(
                    "Não foi possível acessar o serviço de alunos.");
        }
    }
}