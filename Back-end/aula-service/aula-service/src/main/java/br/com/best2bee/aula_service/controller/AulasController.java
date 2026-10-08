package br.com.best2bee.aula_service.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.best2bee.aula_service.dto.AulasDTO;
import br.com.best2bee.aula_service.model.Aulas;
import br.com.best2bee.aula_service.service.AulasService;

@RestController
@RequestMapping("/aulas")
public class AulasController {

    private final AulasService aulasService;

    public AulasController(AulasService aulasService) {
        this.aulasService = aulasService;
    }

    @GetMapping
    public ResponseEntity<List<Aulas>> listarTodas() {
        return ResponseEntity.ok(aulasService.getAllAulas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aulas> listarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(aulasService.getAulaById(id));

    }

    @PostMapping
    public ResponseEntity<Aulas> salvarAula(@Valid @RequestBody AulasDTO dto) {
        Aulas aulaSalva = aulasService.registrarAula(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(aulaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aulas> atualizarAula(@PathVariable Integer id, @Valid @RequestBody AulasDTO dto) {
        Aulas aula = aulasService.atualizarAula(id, dto);
        return ResponseEntity.ok(aula);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Integer id) {
        aulasService.excluirAula(id);
        return ResponseEntity.noContent().build();
    }

}