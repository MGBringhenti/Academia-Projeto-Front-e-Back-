package br.com.best2bee.aula_service.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.best2bee.aula_service.model.Professores;
import br.com.best2bee.aula_service.service.ProfessoresService;
import br.com.best2bee.aula_service.dto.ProfessoresDTO;

@RestController
@RequestMapping("/professores")
public class ProfessoresController {

    private final ProfessoresService professoresService;

    public ProfessoresController(ProfessoresService professoresService) {
        this.professoresService = professoresService;
    }

    @GetMapping
    public ResponseEntity<List<Professores>> listarTodos() {
        return ResponseEntity.ok(professoresService.findAllProfessores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professores> listarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(professoresService.findProfessorById(id));
    }

    @PostMapping
    public ResponseEntity<Professores> cadastrar(@Valid @RequestBody ProfessoresDTO dto) {
        Professores professor = professoresService.cadastrarProfessor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(professor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professores> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProfessoresDTO dto) {

        return ResponseEntity.ok(
                professoresService.atualizarProfessor(id, dto));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletarCadastro(@PathVariable Integer id) {
        professoresService.excluirProfessor(id);
        return ResponseEntity.noContent().build();
    }

    private void preencherDados(Professores professor, ProfessoresDTO dto) {
        professor.setNomeProfessor(dto.getNomeProfessor());
        professor.setEmailProfessor(dto.getEmailProfessor());
        professor.setCpf(dto.getCpf());
        professor.setEspecializacao(dto.getEspecializacao());
    }

}