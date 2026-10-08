package br.com.best2bee.aula_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table (name = "professores")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter 
public class Professores {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Integer idProfessor;

    @Column (name = "nome")
    private String nomeProfessor;

    @Column (name = "email")
    private String emailProfessor;

    @Column (name = "cpf")
    private String cpf;

    @Column (name = "especializacao")
    private String especializacao;
}
