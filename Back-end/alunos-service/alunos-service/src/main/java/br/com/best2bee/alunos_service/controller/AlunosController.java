package br.com.best2bee.alunos_service.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.best2bee.alunos_service.dto.AlunosDTO;
import br.com.best2bee.alunos_service.model.Alunos;
import br.com.best2bee.alunos_service.service.AlunosService;

@RestController
@RequestMapping("/alunos")
public class AlunosController {

    private final AlunosService alunosService;

    public AlunosController(AlunosService alunosService) {
        this.alunosService = alunosService;
    }

    @GetMapping
    public ResponseEntity<List<Alunos>> listarTodos() {
        return ResponseEntity.ok(alunosService.getAllAlunos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alunos> getAlunoById(
            @PathVariable("id") Integer id) {

        return ResponseEntity.ok(alunosService.getAlunoById(id));
    }

    @PostMapping
    public ResponseEntity<Alunos> salvar(
            @Valid @RequestBody AlunosDTO dto) {

        Alunos alunoSalvo = alunosService.matricularAlunos(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(alunoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alunos> atualizarAlunos(
            @PathVariable("id") Integer id,
            @Valid @RequestBody AlunosDTO dto) {

        Alunos alunoAtualizado = alunosService.atualizarAluno(id, dto);

        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAluno(
            @PathVariable("id") Integer id) {

        alunosService.excluirAlunos(id);

        return ResponseEntity.noContent().build();
    }
}