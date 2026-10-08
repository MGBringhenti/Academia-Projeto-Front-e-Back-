package br.com.best2bee.aula_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity 
@Table (name = "checkin")
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter 
public class CheckIn {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Integer idCheckIn;

    @JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")
    @Column (name = "data_hora")
    private LocalDateTime dataHoraCheckIn;

    @Column (name = "id_aluno")
    private Integer idAluno;

    @Column (name = "id_aula")
    private Integer idAula;
}
