package br.com.best2bee.aula_service.service;

import org.springframework.stereotype.Service;

import br.com.best2bee.aula_service.dto.AulasDTO;
import br.com.best2bee.aula_service.model.Aulas;
import br.com.best2bee.aula_service.repository.AulasRepository;

import java.util.List;

@Service 
public class AulasService {

    private final AulasRepository aulasRepository;

    public AulasService(AulasRepository aulasRepository){
        this.aulasRepository = aulasRepository;
    }

    public List<Aulas> getAllAulas() {
        return aulasRepository.findAll();
    };

    public Aulas getAulaById(Integer id){
        return aulasRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Aula não encontrada com o id " + id));
    }

    public Aulas registrarAula (AulasDTO dto){
        Aulas aula = new Aulas();
        preencherDados(aula, dto);
        return aulasRepository.save(aula);
    }

    public Aulas atualizarAula(Integer id, AulasDTO dto){

        Aulas aula = getAulaById(id);
        preencherDados(aula, dto);
        return aulasRepository.save(aula);
    }

    public void excluirAula (Integer id){
        Aulas aulas = getAulaById(id);
        aulasRepository.delete(aulas);
    }

    private void preencherDados (Aulas aula, AulasDTO dto){
        aula.setNomeAula(dto.getNomeAula());
        aula.setDiasSemana(dto.getDiasSemana());
        aula.setHorarioAula(dto.getHorarioAula());
        aula.setCapacidadeAula(dto.getCapacidadeAula());
        aula.setIdProfessor(dto.getIdProfessor());
    }


}