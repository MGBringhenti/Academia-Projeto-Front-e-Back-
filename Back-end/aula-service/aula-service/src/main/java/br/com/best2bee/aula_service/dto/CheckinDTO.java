package br.com.best2bee.aula_service.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class CheckinDTO {

    @NotNull (message = "ID do aluno é obrigatório")
    private Integer idAluno;

    @NotNull (message = "ID da aula é obrigatório")
    private Integer idAula;
    
}