package br.com.best2bee.alunos_service.dto;

import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter 
@Setter 
public class AlunosDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "E-mail é obrigatório")
    @Size(max = 100, message = "E-mail pode ter até 100 caracteres")
    @Email(message = "E-mail inválido")
    private String email;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve estar no passado")
    private LocalDate dataNascimento;

    @NotNull (message = "ID do plano é obrigatório")
    private Integer idPlano;

    
}
