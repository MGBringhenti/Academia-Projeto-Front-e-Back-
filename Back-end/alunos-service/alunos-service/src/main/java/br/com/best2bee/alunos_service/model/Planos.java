package br.com.best2bee.alunos_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity 
@Table(name = "planos")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter 
public class Planos {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idPlano;

    @Column(name = "nome")
    private String nome;

    @Column(name = "duracao_meses")
    private Integer duracaoMeses;

    @Column (name = "preco")
    private BigDecimal preco;


}
