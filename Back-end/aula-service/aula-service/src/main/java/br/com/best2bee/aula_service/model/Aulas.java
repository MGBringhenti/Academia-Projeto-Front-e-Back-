package br.com.best2bee.aula_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table (name = "aulas")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter 
public class Aulas {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Integer idAula;

    @Column (name = "nome")
    private String nomeAula;

    @Column (name = "dias_semana")
    private String diasSemana;

    @Column (name = "capacidade")
    private Integer capacidadeAula;

    @Column (name = "horario")
    private LocalTime horarioAula;

    @Column (name = "id_professor")
    private Integer idProfessor;
}
