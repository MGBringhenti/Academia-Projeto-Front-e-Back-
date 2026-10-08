package br.com.best2bee.aula_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.best2bee.aula_service.model.Matriculas;
import br.com.best2bee.aula_service.model.MatriculasId;

public interface MatriculasRepository extends JpaRepository<Matriculas, MatriculasId>{
    
    long countByIdAulaAndStatus(Integer idAula, String Status);
}