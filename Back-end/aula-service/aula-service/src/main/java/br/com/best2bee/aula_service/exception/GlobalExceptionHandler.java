package br.com.best2bee.aula_service.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        ErroResposta erro = new ErroResposta(HttpStatus.NOT_FOUND.value(), "Recurso não encontrado", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> handleIntegridade(
            DataIntegrityViolationException ex) {

        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Conflito nos dados",
                "A operação viola uma restrição do banco. "
                        + "Verifique se CPF ou e-mail já estão cadastrados "
                        + "e se o plano informado existe.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> handleValidacao(MethodArgumentNotValidException ex) {
        String mensagens = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErroResposta erro = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                mensagens);
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(AulaLotadaException.class)
    public ResponseEntity<ErroResposta> handleAulaLotada(
            AulaLotadaException ex) {

        ErroResposta erro = new ErroResposta(
                HttpStatus.CONFLICT.value(),
                "Aula lotada",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler (ServicoIndisponivelException.class)
    public ResponseEntity<ErroResposta> handleServicoIndisponivel(
            ServicoIndisponivelException ex) {

        ErroResposta erro = new ErroResposta(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Serviço Indisponível",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(erro);
    }
}
