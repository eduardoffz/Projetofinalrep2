package com.frota.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.frota.model.*;
import com.frota.service.LocacaoService;

@RestController
@RequestMapping("/api/locacoes")
public class LocacaoController {
    private final LocacaoService service;

    public LocacaoController(LocacaoService s) { this.service = s; }

    @GetMapping
    public ResponseEntity<List<Locacao>> listar() { return ResponseEntity.ok(service.listarAtivas()); }

    @GetMapping("/{id}")
    public ResponseEntity<Locacao> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarDetalhes(id));
    }

    @PostMapping
    public ResponseEntity<Locacao> criar(@RequestBody LocacaoRequest dto, @RequestAttribute Long usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarLocacao(dto, usuarioId));
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovar(@PathVariable Long id, @RequestAttribute Long usuarioId) {
        service.aprovarLocacao(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<Void> concluir(@PathVariable Long id, @RequestAttribute Long usuarioId) {
        service.concluirLocacao(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, @RequestAttribute Long usuarioId) {
        service.cancelarLocacao(id);
        return ResponseEntity.ok().build();
    }
}
