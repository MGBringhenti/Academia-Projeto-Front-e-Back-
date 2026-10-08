package br.com.best2bee.aula_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.best2bee.aula_service.model.Aulas;

public interface AulasRepository extends JpaRepository <Aulas, Integer>{

}