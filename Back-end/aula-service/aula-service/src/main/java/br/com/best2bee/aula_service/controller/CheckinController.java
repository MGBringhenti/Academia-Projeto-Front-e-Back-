package br.com.best2bee.aula_service.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.best2bee.aula_service.model.CheckIn;
import br.com.best2bee.aula_service.service.CheckinService;
import br.com.best2bee.aula_service.dto.CheckinDTO;

@RestController
@RequestMapping("/checkin")
public class CheckinController {

    private final CheckinService checkinService;

    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @GetMapping
    public ResponseEntity<List<CheckIn>> listarTodos() {
        return ResponseEntity.ok(checkinService.getAllCheckIns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckIn> listarPorId(Integer id) {
        return ResponseEntity.ok(checkinService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CheckIn> registrarCheckIn(@Valid @RequestBody CheckinDTO dto) {
        CheckIn checkInSalvo = checkinService.registrarCheckIn(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(checkInSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarCheckIn(@PathVariable Integer id) {
        checkinService.apagarCheckIn(id);
        return ResponseEntity.noContent().build();
    }

}