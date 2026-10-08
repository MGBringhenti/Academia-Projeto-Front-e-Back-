package br.com.best2bee.aula_service.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class MatriculasDTO {

    @NotNull (message = "ID do aluno é obrigatório")
    private Integer idAluno;

    @NotNull (message = "ID da aula é obrigatória")
    private Integer idAula;
}