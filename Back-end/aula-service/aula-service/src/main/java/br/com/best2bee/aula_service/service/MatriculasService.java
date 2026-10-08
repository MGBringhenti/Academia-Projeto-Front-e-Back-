package br.com.best2bee.aula_service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.best2bee.aula_service.client.AlunosClient;
import br.com.best2bee.aula_service.dto.AtualizarMatriculaDTO;
import br.com.best2bee.aula_service.dto.MatriculasDTO;
import br.com.best2bee.aula_service.exception.AulaLotadaException;
import br.com.best2bee.aula_service.exception.RecursoNaoEncontradoException;
import br.com.best2bee.aula_service.model.Aulas;
import br.com.best2bee.aula_service.model.Matriculas;
import br.com.best2bee.aula_service.model.MatriculasId;
import br.com.best2bee.aula_service.repository.AulasRepository;
import br.com.best2bee.aula_service.repository.MatriculasRepository;
import br.com.best2bee.aula_service.producer.MatriculasProducer;

@Service
public class MatriculasService {

    private final MatriculasRepository matriculasRepository;
    private final AulasRepository aulasRepository;
    private final AlunosClient alunosClient;
    private final MatriculasProducer matriculasProducer;

    public MatriculasService(
            MatriculasRepository matriculasRepository,
            AulasRepository aulasRepository,
            AlunosClient alunosClient,
            MatriculasProducer matriculasProducer) {

        this.matriculasRepository = matriculasRepository;
        this.aulasRepository = aulasRepository;
        this.alunosClient = alunosClient;
        this.matriculasProducer = matriculasProducer;
    }

    public List<Matriculas> findAllMatriculas() {
        return matriculasRepository.findAll();
    }

    public Matriculas findMatriculaById(Integer idAluno, Integer idAula) {
        MatriculasId id = new MatriculasId(idAluno, idAula);

        return matriculasRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Matrícula não encontrada para o aluno "
                                + idAluno + " na aula " + idAula));
    }

    public Matriculas matricular(MatriculasDTO dto) {
        MatriculasId id = new MatriculasId(
                dto.getIdAluno(),
                dto.getIdAula()
        );

        if (matriculasRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Matrícula já existe. Use a atualização para alterar o status."
            );
        }

        alunosClient.verificarAluno(dto.getIdAluno());

        verificarVaga(dto.getIdAula());

        Matriculas matricula = new Matriculas();
        matricula.setIdAluno(dto.getIdAluno());
        matricula.setIdAula(dto.getIdAula());
        matricula.setDataInscricao(LocalDate.now());
        matricula.setStatus("Ativa");

        Matriculas matriculaSalva = matriculasRepository.save(matricula);
        matriculasProducer.publicarMatriculaCriada(matriculaSalva);

        return matriculaSalva;
    }

    public Matriculas atualizarMatricula(
            Integer idAluno,
            Integer idAula,
            AtualizarMatriculaDTO dto) {

        Matriculas matricula = findMatriculaById(idAluno, idAula);

        // Reativar uma matrícula exige aluno válido e uma vaga.
        if ("Cancelada".equals(matricula.getStatus())
                && "Ativa".equals(dto.getStatus())) {

            alunosClient.verificarAluno(idAluno);
            verificarVaga(idAula);
        }

        // Preserva os IDs e a data original da inscrição.
        matricula.setStatus(dto.getStatus());

        return matriculasRepository.save(matricula);
    }

    public void deletarMatricula(Integer idAluno, Integer idAula) {
        Matriculas matricula = findMatriculaById(idAluno, idAula);
        matriculasRepository.delete(matricula);
    }

    private void verificarVaga(Integer idAula) {
        Aulas aula = aulasRepository.findById(idAula)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aula não encontrada com o id " + idAula));

        long matriculasAtivas =
                matriculasRepository.countByIdAulaAndStatus(
                        idAula, "Ativa");

        if (matriculasAtivas >= aula.getCapacidadeAula()) {
            throw new AulaLotadaException(
                    "Não há vagas disponíveis na aula " + idAula);
        }
    }
}