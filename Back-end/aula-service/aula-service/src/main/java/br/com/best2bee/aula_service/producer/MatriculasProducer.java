package br.com.best2bee.aula_service.producer;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import br.com.best2bee.aula_service.event.MatriculaCriadaEvent;
import br.com.best2bee.aula_service.model.Matriculas;

@Component
public class MatriculasProducer {

    private static final Logger log =
            LoggerFactory.getLogger(MatriculasProducer.class);

    private static final String TOPICO =
            "academia.matriculas.criadas";

    private final KafkaTemplate<String, MatriculaCriadaEvent> kafkaTemplate;

    public MatriculasProducer(
            KafkaTemplate<String, MatriculaCriadaEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarMatriculaCriada(Matriculas matricula) {
        MatriculaCriadaEvent evento = new MatriculaCriadaEvent(
                UUID.randomUUID().toString(),
                matricula.getIdAluno(),
                matricula.getIdAula(),
                matricula.getDataInscricao()
        );

        String chave = matricula.getIdAluno()
                + ":" + matricula.getIdAula();

        try {
            kafkaTemplate.send(TOPICO, chave, evento)
                    .whenComplete((resultado, erro) -> {
                        if (erro != null) {
                            log.error(
                                    "Falha ao publicar evento {} da matrícula {}",
                                    evento.eventId(), chave, erro);
                        } else {
                            log.info(
                                    "Evento {} publicado no tópico {}",
                                    evento.eventId(), TOPICO);
                        }
                    });
        } catch (RuntimeException erro) {
            log.error(
                    "Falha ao iniciar envio do evento {} da matrícula {}",
                    evento.eventId(), chave, erro);
        }
    }
}