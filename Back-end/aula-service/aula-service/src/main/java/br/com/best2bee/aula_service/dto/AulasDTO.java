package br.com.best2bee.aula_service.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter 
@Setter 
public class AulasDTO {

    @NotBlank (message = "Nome da aula é obrigatório")
    @Size (max = 100, message = "Texto não pode passar de 100 caracteres")
    private String nomeAula;

    @NotBlank (message = "Deve inserir os dias da semana da aula")
    @Size (max = 100, message = "Texto não pode passar de 100 caracteres")
    private String diasSemana;

    @NotNull (message = "Obrigatório inserir a capacidade da aula")
    private Integer capacidadeAula;

    @NotNull (message = "Obrigatório inserir o horário da aula")
    private LocalTime horarioAula;

    @NotNull (message = "Obrigatório inserir o ID do professor")
    private Integer idProfessor;
}