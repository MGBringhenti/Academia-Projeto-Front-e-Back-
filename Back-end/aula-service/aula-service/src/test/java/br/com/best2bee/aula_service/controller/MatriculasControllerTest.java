package br.com.best2bee.aula_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.best2bee.aula_service.config.SecurityConfig;
import br.com.best2bee.aula_service.dto.AtualizarMatriculaDTO;
import br.com.best2bee.aula_service.dto.MatriculasDTO;
import br.com.best2bee.aula_service.exception.AulaLotadaException;
import br.com.best2bee.aula_service.exception.GlobalExceptionHandler;
import br.com.best2bee.aula_service.exception.RecursoNaoEncontradoException;
import br.com.best2bee.aula_service.model.Matriculas;
import br.com.best2bee.aula_service.service.MatriculasService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatriculasController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class MatriculasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatriculasService matriculasService;

    @Test
    @WithMockUser
    void deveRetornar201AoMatricular() throws Exception {
        Matriculas matricula = criarMatricula("Ativa");

        when(matriculasService.matricular(any(MatriculasDTO.class)))
                .thenReturn(matricula);

        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idAluno": 1,
                                    "idAula": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAluno").value(1))
                .andExpect(jsonPath("$.idAula").value(5))
                .andExpect(jsonPath("$.status").value("Ativa"));

        verify(matriculasService).matricular(argThat(dto ->
                Integer.valueOf(1).equals(dto.getIdAluno())
                        && Integer.valueOf(5).equals(dto.getIdAula())
        ));
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoIdAlunoNaoForInformado() throws Exception {
        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idAula": 5
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(matriculasService);
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoIdAulaNaoForInformado() throws Exception {
        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idAluno": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(matriculasService);
    }

    @Test
    @WithMockUser
    void deveRetornar409QuandoAulaEstiverLotada() throws Exception {
        when(matriculasService.matricular(any(MatriculasDTO.class)))
                .thenThrow(new AulaLotadaException(
                        "Não há vagas disponíveis na aula 5"
                ));

        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idAluno": 1,
                                    "idAula": 5
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Aula lotada"));
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoAlunoNaoExistir() throws Exception {
        when(matriculasService.matricular(any(MatriculasDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException(
                        "Aluno não encontrado"
                ));

        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idAluno": 999,
                                    "idAula": 5
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem")
                        .value("Aluno não encontrado"));
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoAulaNaoExistir() throws Exception {
        when(matriculasService.matricular(any(MatriculasDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException(
                        "Aula não encontrada"
                ));

        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idAluno": 1,
                                    "idAula": 999
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem")
                        .value("Aula não encontrada"));
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoStatusForInvalido() throws Exception {
        mockMvc.perform(put("/matriculas/1/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "Pendente"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(matriculasService);
    }

    @Test
    @WithMockUser
    void deveRetornar200AoCancelarMatricula() throws Exception {
        when(matriculasService.atualizarMatricula(
                eq(1), eq(5), any(AtualizarMatriculaDTO.class)
        )).thenReturn(criarMatricula("Cancelada"));

        mockMvc.perform(put("/matriculas/1/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "Cancelada"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAluno").value(1))
                .andExpect(jsonPath("$.idAula").value(5))
                .andExpect(jsonPath("$.status").value("Cancelada"));

        verify(matriculasService).atualizarMatricula(
                eq(1),
                eq(5),
                argThat(dto -> "Cancelada".equals(dto.getStatus()))
        );
    }

    @Test
    void devePermitirGetSemAutenticacao() throws Exception {
        when(matriculasService.findAllMatriculas())
                .thenReturn(List.of(criarMatricula("Ativa")));

        mockMvc.perform(get("/matriculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAluno").value(1))
                .andExpect(jsonPath("$[0].idAula").value(5));

        verify(matriculasService).findAllMatriculas();
    }

    @Test
    void deveRetornar401AoPostarSemAutenticacao() throws Exception {
        mockMvc.perform(post("/matriculas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "idAluno": 1,
                                    "idAula": 5
                                }
                                """))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(matriculasService);
    }

    @Test
    @WithMockUser
    void deveRetornar204AoExcluirMatricula() throws Exception {
        mockMvc.perform(delete("/matriculas/1/5"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(matriculasService).deletarMatricula(1, 5);
    }

    private Matriculas criarMatricula(String status) {
        Matriculas matricula = new Matriculas();
        matricula.setIdAluno(1);
        matricula.setIdAula(5);
        matricula.setDataInscricao(LocalDate.of(2026, 10, 5));
        matricula.setStatus(status);
        return matricula;
    }
} 
