package com.frota.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.frota.model.*;
import com.frota.repository.*;
import com.frota.service.TelemetriaService;

@RestController
@RequestMapping("/api/maquinas")
public class MaquinaController {
    private final MaquinaRepository maqRepo;
    private final TelemetriaRepository telRepo;
    private final TelemetriaService telService;

    public MaquinaController(MaquinaRepository m, TelemetriaRepository t, TelemetriaService s) {
        this.maqRepo=m; this.telRepo=t; this.telService=s;
    }

    @GetMapping
    public ResponseEntity<List<Maquina>> listar(@RequestParam(required=false) Long proprietarioId) {
        if (proprietarioId != null) return ResponseEntity.ok(maqRepo.listarPorProprietario(proprietarioId));
        return ResponseEntity.ok(maqRepo.listarTodos());
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Maquina>> disponiveis() {
        return ResponseEntity.ok(maqRepo.listarDisponiveis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Maquina> buscar(@PathVariable Long id) {
        return maqRepo.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Maquina> criar(@RequestBody Maquina m) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maqRepo.criar(m));
    }

    @GetMapping("/{id}/telemetria")
    public ResponseEntity<List<Telemetria>> telemetria(@PathVariable Long id) {
        return ResponseEntity.ok(telRepo.listarPorMaquina(id));
    }

    @PostMapping("/{id}/telemetria")
    public ResponseEntity<Telemetria> registrarTelemetria(@PathVariable Long id, @RequestBody Telemetria t) {
        t.setMaquinaId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(telService.registrarTelemetria(t));
    }
}
