package br.com.best2bee.alunos_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.best2bee.alunos_service.model.Alunos;

public interface AlunosRepository extends JpaRepository<Alunos, Integer> {
    
}
