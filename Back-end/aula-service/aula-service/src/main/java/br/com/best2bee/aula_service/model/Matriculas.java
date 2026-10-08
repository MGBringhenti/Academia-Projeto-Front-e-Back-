package br.com.best2bee.aula_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity 
@Table (name = "matriculas")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter 
@IdClass (MatriculasId.class)
public class Matriculas {
    
    @Id 
    @Column (name = "idAluno")
    private Integer idAluno;

    @Id 
    @Column (name = "idAula")
    private Integer idAula;

    @Column (name = "data_inscricao")
    private LocalDate dataInscricao;

    @Column (name = "status")
    private String status;
}
