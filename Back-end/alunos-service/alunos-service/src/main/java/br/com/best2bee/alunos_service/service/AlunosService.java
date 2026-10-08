package br.com.best2bee.alunos_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.best2bee.alunos_service.dto.AlunosDTO;
import br.com.best2bee.alunos_service.exception.RecursoNaoEncontradoException;
import br.com.best2bee.alunos_service.model.Alunos;
import br.com.best2bee.alunos_service.repository.AlunosRepository;

@Service
@Transactional(readOnly = true)
public class AlunosService {

    private final AlunosRepository alunosRepository;

    public AlunosService(AlunosRepository alunosRepository) {
        this.alunosRepository = alunosRepository;
    }

    public List<Alunos> getAllAlunos() {
        return alunosRepository.findAll();
    }

    public Alunos getAlunoById(Integer id) {
        return buscarAlunoOuLancarExcecao(id);
    }

    @Transactional
    public Alunos matricularAlunos(AlunosDTO dto) {
        Alunos aluno = new Alunos();
        preencherDados(aluno, dto);

        return alunosRepository.save(aluno);
    }

    @Transactional
    public Alunos atualizarAluno(Integer id, AlunosDTO dto) {
        Alunos aluno = buscarAlunoOuLancarExcecao(id);
        preencherDados(aluno, dto);

        return alunosRepository.save(aluno);
    }

    @Transactional
    public void excluirAlunos(Integer id) {
        Alunos aluno = buscarAlunoOuLancarExcecao(id);
        alunosRepository.delete(aluno);
    }

    private Alunos buscarAlunoOuLancarExcecao(Integer id) {
        return alunosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Aluno não encontrado com o id " + id));
    }

    private void preencherDados(Alunos aluno, AlunosDTO dto) {
        aluno.setNome(dto.getNome());
        aluno.setCpf(dto.getCpf());
        aluno.setEmail(dto.getEmail());
        aluno.setDataNascimento(dto.getDataNascimento());
        aluno.setIdPlano(dto.getIdPlano());
    }
}