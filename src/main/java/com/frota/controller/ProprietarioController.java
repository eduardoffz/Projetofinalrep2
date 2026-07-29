package com.frota.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.frota.model.*;
import com.frota.service.ProprietarioService;

@RestController
@RequestMapping("/api/proprietarios")
public class ProprietarioController {
    private final ProprietarioService service;

    public ProprietarioController(ProprietarioService s) { this.service = s; }

    @GetMapping
    public ResponseEntity<List<Proprietario>> listar() { return ResponseEntity.ok(service.listarTodos()); }

    @GetMapping("/meu-perfil")
    public ResponseEntity<Proprietario> meuPerfil(@RequestAttribute Long usuarioId) {
        Proprietario p = service.buscarPorUsuario(usuarioId);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestAttribute Long usuarioId) {
        Proprietario p = service.buscarPorUsuario(usuarioId);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(service.listarMaquinas(p.getId()));
    }
}
