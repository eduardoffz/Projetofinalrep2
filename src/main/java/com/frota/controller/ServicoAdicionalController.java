package com.frota.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.frota.model.ServicoAdicional;
import com.frota.repository.ServicoAdicionalRepository;

@RestController
@RequestMapping("/api/servicos")
public class ServicoAdicionalController {
    private final ServicoAdicionalRepository repo;

    public ServicoAdicionalController(ServicoAdicionalRepository r) { this.repo = r; }

    @GetMapping
    public ResponseEntity<List<ServicoAdicional>> listar() { return ResponseEntity.ok(repo.listarTodos()); }

    @PostMapping
    public ResponseEntity<ServicoAdicional> criar(@RequestBody ServicoAdicional s) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.criar(s));
    }
}
