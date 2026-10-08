package br.com.best2bee.aula_service.service;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.best2bee.aula_service.client.AlunosClient;
import br.com.best2bee.aula_service.dto.AtualizarMatriculaDTO;
import br.com.best2bee.aula_service.dto.MatriculasDTO;
import br.com.best2bee.aula_service.exception.AulaLotadaException;
import br.com.best2bee.aula_service.exception.RecursoNaoEncontradoException;
import br.com.best2bee.aula_service.model.Aulas;
import br.com.best2bee.aula_service.model.Matriculas;
import br.com.best2bee.aula_service.model.MatriculasId;
import br.com.best2bee.aula_service.producer.MatriculasProducer;
import br.com.best2bee.aula_service.repository.AulasRepository;
import br.com.best2bee.aula_service.repository.MatriculasRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculasServiceTest {

    @Mock
    private MatriculasRepository matriculasRepository;

    @Mock
    private AulasRepository aulasRepository;

    @Mock
    private AlunosClient alunosClient;

    @Mock
    private MatriculasProducer matriculasProducer;

    @InjectMocks
    private MatriculasService matriculasService;

    @Test
    void deveMatricularQuandoHouverVaga() {
        MatriculasDTO dto = criarDTO();

        when(matriculasRepository.existsById(new MatriculasId(1, 5)))
                .thenReturn(false);

        simularAulaComMatriculasAtivas(9L);

        // Retorna a matrícula recebida para salvar.
        when(matriculasRepository.save(any(Matriculas.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Matriculas resultado = matriculasService.matricular(dto);

        assertNotNull(resultado);
        assertEquals(Integer.valueOf(1), resultado.getIdAluno());
        assertEquals(Integer.valueOf(5), resultado.getIdAula());
        assertEquals("Ativa", resultado.getStatus());
        assertNotNull(resultado.getDataInscricao());

        verify(alunosClient).verificarAluno(1);
        verify(matriculasRepository).save(same(resultado));
        verify(matriculasProducer)
                .publicarMatriculaCriada(same(resultado));
    }

    @Test
    void deveImpedirMatriculaQuandoAulaEstiverLotada() {
        MatriculasDTO dto = criarDTO();

        when(matriculasRepository.existsById(new MatriculasId(1, 5)))
                .thenReturn(false);

        simularAulaComMatriculasAtivas(10L);

        assertThrows(
                AulaLotadaException.class,
                () -> matriculasService.matricular(dto)
        );

        verificarQueNaoSalvouNemPublicou();
    }

    @Test
    void deveImpedirMatriculaDuplicada() {
        MatriculasDTO dto = criarDTO();

        when(matriculasRepository.existsById(new MatriculasId(1, 5)))
                .thenReturn(true);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> matriculasService.matricular(dto)
        );

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());

        verificarQueNaoSalvouNemPublicou();
        verifyNoInteractions(alunosClient, aulasRepository);
    }

    @Test
    void deveImpedirMatriculaQuandoAlunoNaoExistir() {
        MatriculasDTO dto = criarDTO();

        when(matriculasRepository.existsById(new MatriculasId(1, 5)))
                .thenReturn(false);

        RecursoNaoEncontradoException erroEsperado =
                new RecursoNaoEncontradoException("Aluno não encontrado");

        doThrow(erroEsperado)
                .when(alunosClient).verificarAluno(1);

        RecursoNaoEncontradoException erro = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> matriculasService.matricular(dto)
        );

        assertSame(erroEsperado, erro);

        verificarQueNaoSalvouNemPublicou();
        verifyNoInteractions(aulasRepository);
    }

    @Test
    void deveImpedirMatriculaQuandoAulaNaoExistir() {
        MatriculasDTO dto = criarDTO();

        when(matriculasRepository.existsById(new MatriculasId(1, 5)))
                .thenReturn(false);

        when(aulasRepository.findById(5))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> matriculasService.matricular(dto)
        );

        verify(alunosClient).verificarAluno(1);
        verificarQueNaoSalvouNemPublicou();
    }

    @Test
    void deveImpedirReativacaoQuandoAulaEstiverLotada() {
        Matriculas matricula = criarMatricula("Cancelada");
        LocalDate dataOriginal = matricula.getDataInscricao();

        when(matriculasRepository.findById(new MatriculasId(1, 5)))
                .thenReturn(Optional.of(matricula));

        simularAulaComMatriculasAtivas(10L);

        AtualizarMatriculaDTO dto = criarAtualizacao("Ativa");

        assertThrows(
                AulaLotadaException.class,
                () -> matriculasService.atualizarMatricula(1, 5, dto)
        );

        // A matrícula permanece cancelada.
        assertEquals("Cancelada", matricula.getStatus());
        assertEquals(dataOriginal, matricula.getDataInscricao());

        verify(alunosClient).verificarAluno(1);
        verificarQueNaoSalvouNemPublicou();
    }

    @Test
    void deveReativarMatriculaQuandoHouverVaga() {
        Matriculas matricula = criarMatricula("Cancelada");
        LocalDate dataOriginal = matricula.getDataInscricao();

        when(matriculasRepository.findById(new MatriculasId(1, 5)))
                .thenReturn(Optional.of(matricula));

        simularAulaComMatriculasAtivas(9L);

        when(matriculasRepository.save(same(matricula)))
                .thenReturn(matricula);

        AtualizarMatriculaDTO dto = criarAtualizacao("Ativa");

        Matriculas resultado =
                matriculasService.atualizarMatricula(1, 5, dto);

        assertEquals("Ativa", resultado.getStatus());
        assertEquals(Integer.valueOf(1), resultado.getIdAluno());
        assertEquals(Integer.valueOf(5), resultado.getIdAula());
        assertEquals(dataOriginal, resultado.getDataInscricao());

        verify(alunosClient).verificarAluno(1);
        verify(matriculasRepository).save(same(matricula));

        // Seu service publica evento apenas na criação.
        verifyNoInteractions(matriculasProducer);
    }

    @Test
    void deveCancelarMatriculaPreservandoIdsEData() {
        Matriculas matricula = criarMatricula("Ativa");
        LocalDate dataOriginal = matricula.getDataInscricao();

        when(matriculasRepository.findById(new MatriculasId(1, 5)))
                .thenReturn(Optional.of(matricula));

        when(matriculasRepository.save(same(matricula)))
                .thenReturn(matricula);

        AtualizarMatriculaDTO dto = criarAtualizacao("Cancelada");

        Matriculas resultado =
                matriculasService.atualizarMatricula(1, 5, dto);

        assertEquals("Cancelada", resultado.getStatus());
        assertEquals(Integer.valueOf(1), resultado.getIdAluno());
        assertEquals(Integer.valueOf(5), resultado.getIdAula());
        assertEquals(dataOriginal, resultado.getDataInscricao());

        verify(matriculasRepository).save(same(matricula));

        // Cancelar não exige consultar aluno ou disponibilidade.
        verifyNoInteractions(
                alunosClient,
                aulasRepository,
                matriculasProducer
        );
    }

    // Métodos auxiliares para evitar repetir a preparação dos testes.

    private MatriculasDTO criarDTO() {
        MatriculasDTO dto = new MatriculasDTO();
        dto.setIdAluno(1);
        dto.setIdAula(5);
        return dto;
    }

    private AtualizarMatriculaDTO criarAtualizacao(String status) {
        AtualizarMatriculaDTO dto = new AtualizarMatriculaDTO();
        dto.setStatus(status);
        return dto;
    }

    private Matriculas criarMatricula(String status) {
        Matriculas matricula = new Matriculas();
        matricula.setIdAluno(1);
        matricula.setIdAula(5);
        matricula.setDataInscricao(LocalDate.of(2026, 9, 1));
        matricula.setStatus(status);
        return matricula;
    }

    private void simularAulaComMatriculasAtivas(long quantidade) {
        Aulas aula = new Aulas();
        aula.setIdAula(5);
        aula.setCapacidadeAula(10);

        when(aulasRepository.findById(5))
                .thenReturn(Optional.of(aula));

        when(matriculasRepository.countByIdAulaAndStatus(5, "Ativa"))
                .thenReturn(quantidade);
    }

    private void verificarQueNaoSalvouNemPublicou() {
        verify(matriculasRepository, never())
                .save(any(Matriculas.class));

        verifyNoInteractions(matriculasProducer);
    }
}
