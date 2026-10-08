package br.com.best2bee.aula_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizarMatriculaDTO {

    @NotBlank(message = "Status é obrigatório")
    @Pattern(
        regexp = "Ativa|Cancelada",
        message = "Status deve ser Ativa ou Cancelada"
    )
    private String status;
}