package br.com.best2bee.alunos_service.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.best2bee.alunos_service.dto.PlanosDTO;
import br.com.best2bee.alunos_service.model.Planos;
import br.com.best2bee.alunos_service.service.PlanosService;

@RestController
@RequestMapping("/planos")
public class PlanosController {

    private final PlanosService planosService;

    public PlanosController(PlanosService planosService) {
        this.planosService = planosService;
    }

    @GetMapping
    public ResponseEntity<List<Planos>> listarTodos() {
        return ResponseEntity.ok(planosService.getAllPlanos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planos> getPlanosById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(planosService.getPlanosById(id));
    }

    @PostMapping
    public ResponseEntity<Planos> salvar(
            @Valid @RequestBody PlanosDTO dto) {

        Planos planosSalvos = planosService.criarPlanos(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(planosSalvos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Planos> atualizarPlanos(
            @PathVariable("id") Integer id,
            @Valid @RequestBody PlanosDTO dto) {

        return ResponseEntity.ok(planosService.atualizarPlanos(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPlanos(
            @PathVariable("id") Integer id) {

        planosService.excluirPlanos(id);

        return ResponseEntity.noContent().build();
    }
}