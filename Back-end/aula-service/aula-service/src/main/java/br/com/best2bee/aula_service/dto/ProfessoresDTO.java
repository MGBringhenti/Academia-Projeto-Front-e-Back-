package br.com.best2bee.aula_service.dto;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class ProfessoresDTO {

    @NotBlank (message = "Nome do professor é obrigatório")
    @Size (max = 100, message = "Nome não pode ter mais de 100 caracteres")
    private String nomeProfessor;

    @NotBlank (message = "E-mail é obrigatório")
    @Email (message = "E-mail inválido")
    private String emailProfessor;

    @NotBlank (message = "CPF é obrigatório")
    @CPF (message = "CPF inválido")
    private String cpf;

    @Size (max = 50, message = "Texto não pode ter mais de 50 caracteres")
    private String especializacao;

}