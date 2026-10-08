package br.com.best2bee.aula_service.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;
import br.com.best2bee.aula_service.model.CheckIn;
import br.com.best2bee.aula_service.model.Matriculas;
import br.com.best2bee.aula_service.repository.CheckinRepository;
import br.com.best2bee.aula_service.dto.CheckinDTO;
import br.com.best2bee.aula_service.exception.RecursoNaoEncontradoException;

@Service 
public class CheckinService {

    private final CheckinRepository checkinRepository;

    private final MatriculasService matriculasService;

    public CheckinService (CheckinRepository checkinRepository, MatriculasService matriculasService){
        this.checkinRepository = checkinRepository;
        this.matriculasService = matriculasService;
    }

    public List<CheckIn> getAllCheckIns(){
        return checkinRepository.findAll();
    }

    public CheckIn findById (Integer id){
        return checkinRepository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException
        ("Check in não encontrado com o id " + id));
    }

    public CheckIn registrarCheckIn (CheckinDTO dto){
        CheckIn checkIn = new CheckIn();
        preencherDados(checkIn, dto);
        Matriculas matricula = matriculasService.findMatriculaById(dto.getIdAluno(), dto.getIdAula());
        if (!matricula.getStatus().equals("Ativa")) {
            throw new RecursoNaoEncontradoException(
                    "Matrícula não está ativa para o aluno "
                            + dto.getIdAluno() + " na aula " + dto.getIdAula());
        }
        return checkinRepository.save(checkIn);
    }

    public void apagarCheckIn (Integer id){
        CheckIn checkIn = findById(id);
        checkinRepository.delete(checkIn);
    }

    private void preencherDados(CheckIn checkIn, CheckinDTO dto) {
        checkIn.setIdAluno(dto.getIdAluno());
        checkIn.setIdAula(dto.getIdAula());
        checkIn.setDataHoraCheckIn(LocalDateTime.now());
    }
}