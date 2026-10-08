package br.com.best2bee.aula_service.service;

import org.springframework.stereotype.Service;
import java.util.List;

import br.com.best2bee.aula_service.dto.ProfessoresDTO;
import br.com.best2bee.aula_service.exception.RecursoNaoEncontradoException;
import br.com.best2bee.aula_service.model.Professores;
import br.com.best2bee.aula_service.repository.ProfessoresRepository;

@Service 
public class ProfessoresService {

    private final ProfessoresRepository professoresRepository;

    public ProfessoresService(ProfessoresRepository professoresRepository){
        this.professoresRepository = professoresRepository;
    }

    public List<Professores> findAllProfessores(){
        return professoresRepository.findAll();
    }

    public Professores findProfessorById (Integer id){
        return professoresRepository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Não foi encontrado professor com o id " + id));
    }

    public Professores cadastrarProfessor (ProfessoresDTO dto){
        Professores professor = new Professores();
        preencherDados(professor, dto);
        return professoresRepository.save(professor);
    }

    public Professores atualizarProfessor (Integer id, ProfessoresDTO dto){
        Professores professor = findProfessorById(id);
        preencherDados(professor, dto);
        return professoresRepository.save(professor);
    }

    public void excluirProfessor(Integer id){
        Professores professor = findProfessorById(id);
        professoresRepository.delete(professor);
    }

    private void preencherDados (Professores professor, ProfessoresDTO dto){
       professor.setNomeProfessor(dto.getNomeProfessor());
       professor.setEmailProfessor(dto.getEmailProfessor());
       professor.setCpf(dto.getCpf());
       professor.setEspecializacao(dto.getEspecializacao());
    }
}