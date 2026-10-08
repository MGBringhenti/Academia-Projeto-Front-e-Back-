package br.com.best2bee.alunos_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.best2bee.alunos_service.dto.PlanosDTO;
import br.com.best2bee.alunos_service.exception.RecursoNaoEncontradoException;
import br.com.best2bee.alunos_service.model.Planos;
import br.com.best2bee.alunos_service.repository.PlanosRepository;

@Service
@Transactional(readOnly = true)
public class PlanosService {

    private final PlanosRepository planosRepository;

    public PlanosService(PlanosRepository planosRepository) {
        this.planosRepository = planosRepository;
    }

    public List<Planos> getAllPlanos() {
        return planosRepository.findAll();
    }

    public Planos getPlanosById(Integer id) {
        return buscarPlanosOuLancarExcecao(id);
    }

    @Transactional
    public Planos criarPlanos(PlanosDTO dto) {
        Planos planos = new Planos();
        preencherDados(planos, dto);

        return planosRepository.save(planos);
    }

    @Transactional
    public Planos atualizarPlanos(Integer id, PlanosDTO dto) {
        Planos planos = buscarPlanosOuLancarExcecao(id);
        preencherDados(planos, dto);

        return planosRepository.save(planos);
    }

    @Transactional
    public void excluirPlanos(Integer id) {
        Planos planos = buscarPlanosOuLancarExcecao(id);
        planosRepository.delete(planos);
    }

    private Planos buscarPlanosOuLancarExcecao(Integer id) {
        return planosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Plano não encontrado com o id " + id));
    }

    private void preencherDados(Planos planos, PlanosDTO dto) {
        planos.setNome(dto.getNome());
        planos.setDuracaoMeses(dto.getDuracaoMeses());
        planos.setPreco(dto.getPreco());
    }
}