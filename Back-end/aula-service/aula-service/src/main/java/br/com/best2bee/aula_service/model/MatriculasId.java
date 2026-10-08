package br.com.best2bee.aula_service.model;

import java.io.Serializable;
import java.util.Objects;

import lombok.*;

@Getter
@Setter 
public class MatriculasId implements Serializable {
    private Integer idAluno;
    private Integer idAula;

    public MatriculasId() {
    }

    public MatriculasId(Integer idAluno, Integer idAula) {
        this.idAluno = idAluno;
        this.idAula = idAula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MatriculasId that = (MatriculasId) o;
        return Objects.equals(idAluno, that.idAluno) && Objects.equals(idAula, that.idAula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAluno, idAula);
    }

}
