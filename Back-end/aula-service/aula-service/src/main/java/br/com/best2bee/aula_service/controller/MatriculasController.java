package br.com.best2bee.aula_service.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.best2bee.aula_service.model.Matriculas;
import br.com.best2bee.aula_service.service.MatriculasService;
import br.com.best2bee.aula_service.dto.AtualizarMatriculaDTO;
import br.com.best2bee.aula_service.dto.MatriculasDTO;

@RestController
@RequestMapping("/matriculas")
public class MatriculasController {

    private final MatriculasService matriculasService;

    public MatriculasController(MatriculasService matriculasService) {
        this.matriculasService = matriculasService;
    }

    @GetMapping
    public ResponseEntity<List<Matriculas>> listarTodos() {
        return ResponseEntity.ok(matriculasService.findAllMatriculas());
    }

    @GetMapping("/{idAluno}/{idAula}")
    public ResponseEntity<Matriculas> listarPorId(@PathVariable Integer idAluno, @PathVariable Integer idAula) {
        return ResponseEntity.ok(matriculasService.findMatriculaById(idAluno, idAula));
    }

    @PostMapping
    public ResponseEntity<Matriculas> matricular(@Valid @RequestBody MatriculasDTO dto) {
        Matriculas matriculaSalva = matriculasService.matricular(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(matriculaSalva);
    }

    @PutMapping("/{idAluno}/{idAula}")
    public ResponseEntity<Matriculas> editarMatricula
    (@PathVariable Integer idAluno, @PathVariable Integer idAula, @Valid @RequestBody AtualizarMatriculaDTO dto) {

        return ResponseEntity.ok(matriculasService.atualizarMatricula(idAluno, idAula, dto));
    }

    @DeleteMapping("/{idAluno}/{idAula}")
    public ResponseEntity<Void> apagarMatricula(@PathVariable Integer idAluno, @PathVariable Integer idAula){
        matriculasService.deletarMatricula(idAluno, idAula);
        return ResponseEntity.noContent().build();
    }
    

}