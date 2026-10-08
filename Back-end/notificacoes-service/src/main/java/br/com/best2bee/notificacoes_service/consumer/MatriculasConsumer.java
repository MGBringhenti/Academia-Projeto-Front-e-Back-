package br.com.best2bee.notificacoes_service.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.best2bee.notificacoes_service.event.MatriculaCriadaEvent;

@Component
public class MatriculasConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(MatriculasConsumer.class);

    @KafkaListener(topics = "academia.matriculas.criadas")
    public void receberMatriculaCriada(MatriculaCriadaEvent evento) {
        log.info(
                "Notificação simulada: aluno {} matriculado na aula {} em {}. Evento: {}",
                evento.idAluno(),
                evento.idAula(),
                evento.dataInscricao(),
                evento.eventId()
        );
    }
}
