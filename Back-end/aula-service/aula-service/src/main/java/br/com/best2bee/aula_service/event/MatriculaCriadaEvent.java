package br.com.best2bee.aula_service.event;

import java.time.LocalDate;

public record MatriculaCriadaEvent(
        String eventId,
        Integer idAluno,
        Integer idAula,
        LocalDate dataInscricao
) {
}
