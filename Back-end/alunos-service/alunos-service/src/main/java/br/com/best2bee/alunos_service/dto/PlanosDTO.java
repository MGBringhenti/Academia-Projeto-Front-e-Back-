package br.com.best2bee.alunos_service.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter 
@Setter 
public class PlanosDTO {
    
    @NotBlank(message = "Nome do plano é obrigatório")
    private String nome;

    @NotNull (message = "Duração em meses do plano é obrigatória")
    @Positive (message = "Duração em meses do plano deve ser um número positivo")   
    private Integer duracaoMeses;

    @NotNull (message = "Valor do plano é obrigatório")
    @Positive (message = "Valor do plano deve ser um número positivo")
    private BigDecimal preco;
}
