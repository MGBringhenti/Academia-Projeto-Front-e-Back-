package br.com.best2bee.alunos_service.repository;

import br.com.best2bee.alunos_service.model.Planos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanosRepository extends JpaRepository<Planos, Integer>{
    
}
