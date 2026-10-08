package br.com.best2bee.aula_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.best2bee.aula_service.model.Professores;

public interface ProfessoresRepository extends JpaRepository<Professores, Integer>{

}